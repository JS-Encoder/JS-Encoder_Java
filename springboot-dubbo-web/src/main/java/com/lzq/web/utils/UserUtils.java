package com.lzq.web.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lzq.api.pojo.Account;
import com.lzq.api.pojo.UserInfo;
import com.lzq.api.service.AccountService;
import com.lzq.api.service.ouath.BaseOuathService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
public class UserUtils {

    /**
     * 用户与第三方id进行绑定
     * @param account
     * @param headtoken
     * @param accountService
     * @return
     */
    public static Map<String, Object> BindAccount(Account account, String headtoken, AccountService accountService) {
        //用户保存jwt信息
        HashMap<String, String> jwtmap = new HashMap();
        jwtmap.put("username", account.getUsername());
        //登录成功生产token
        String token = JWTUtils.getToken(jwtmap);
        //判断是否有token 有token则需要进行第三方绑定 没有则正常登录返回结果
        if (StringUtils.isNotBlank(headtoken)) {
            //验证token
            DecodedJWT verify = JWTUtils.verify(headtoken);
            //获取token中的信息
            String id = verify.getClaim("githubId").asString();
            //判断是github登录还是gitee登录
            if (StringUtils.isNotBlank(id)) {
                if (StringUtils.isNotBlank(account.getGithubId())) {
                    //该用户已经绑定了github账号
                    return ResultMapUtils.ResultMapWithToken(true,1,account,token);
                }
                account.setGithubId(id);
            } else {
                log.info("gitee为："+account.getGiteeId());
                if (StringUtils.isNotBlank(account.getGiteeId())) {
                    //该用户已经绑定了gitee账号
                    return ResultMapUtils.ResultMapWithToken(true,1,account,token);

                }
                account.setGiteeId(verify.getClaim("giteeId").asString());
            }
            //更新数据进行账号绑定
            accountService.bindGit(account);
            return ResultMapUtils.ResultMapWithToken(true,2,account,token);
        }else {
            //返回信息
            return ResultMapUtils.ResultMapWithToken(true,0,account,token);
        }

    }


    /**
     * 返回信息和请求头的设置
     * @param response
     * @param map 数据集
     * @param objectMapper
     * @throws IOException
     */
    public static void responseMessage(HttpServletResponse response, Map<String, Object> map, ObjectMapper objectMapper) throws IOException {
        response.setContentType("application/json;charset=utf-8");
        response.getWriter().write(objectMapper.writeValueAsString(map));
    }


    /**
     * 查询第三方登录信息
     * @param request
     * @param baseOuathService
     * @param redisTemplate
     * @param accountService
     * @param gitType 第三方登录类型
     * @return
     */
    public static Map<String, Object> callback(HttpServletRequest request, BaseOuathService baseOuathService,
                                               StringRedisTemplate redisTemplate,AccountService accountService,String gitType){
        String header = request.getHeader("token");
        Map<String, String> jwtmap = new HashMap();
        Account rest=null;
        //获取code  和 state
        String code = request.getParameter("code");
        String state = request.getParameter("state");
        //获取github返回的信息
        JSONObject userInfo = baseOuathService.getUserInfo(baseOuathService.getAccessToken(code, state));
        //把返回的信息封装成对象
        UserInfo info = JSON.parseObject(userInfo.toString(), UserInfo.class);
        log.info("获取第三方信息"+info.toString());
        //用户登录的状态下进行第三方绑定
        //根据git类型查询用户信息
        if (gitType.equals("giteeId")){
            rest = accountService.queryByGitId(null, info.getId());
            if (StringUtils.isNotBlank(header)){
                //获取token中的用户名
                log.info(header);
                String username = JWTUtils.verify(header).getClaim("username").asString();
                log.info(username);
                if (rest==null){
                    rest=new Account();
                    rest.setUsername(username);
                    rest.setGiteeId(info.getId());
                    accountService.bindGit(rest);
                    return ResultMapUtils.ResultMap(true, 1, "登陆状态下绑定第三方成功");
                }else {
                    return ResultMapUtils.ResultMap(false,1,"登录状态下绑定第三方失败,该第三方已绑定其他账号");
                }

            }
        }else if (gitType.equals("githubId")){
            rest = accountService.queryByGitId(info.getId(),null );
            if (StringUtils.isNotBlank(header)){
                //获取token中的用户名
                String username = JWTUtils.verify(header).getClaim("username").asString();
                if (rest==null){
                    rest=new Account();
                    rest.setUsername(username);
                    rest.setGithubId(info.getId());
                    accountService.bindGit(rest);
                    return ResultMapUtils.ResultMapWithToken(true, 1, "登陆状态下绑定第三方成功", null);
                }else {
                    return ResultMapUtils.ResultMapWithToken(false,1,"登录状态下绑定第三方失败,该第三方已绑定其他账号",null);
                }
            }
        }
        String token;
        //判断改github是否进行过绑定 未绑定则进行绑定，绑定则返回用户信息
        if (rest != null) {
            jwtmap.put("username", rest.getUsername());
            if (gitType.equals("githubId")){
                jwtmap.put("git", rest.getGithubId());
            }else {
                jwtmap.put("git", rest.getGiteeId());
            }
            //把githubId存入数据库中用来严重token是否过期
            redisTemplate.opsForValue().set(info.getId(), info.getId(), 900L, TimeUnit.SECONDS);
            token = JWTUtils.getToken(jwtmap);
            log.info("该用户已绑定");
            return ResultMapUtils.ResultMapWithToken(true, 0, "返回token用于登录", token);
        } else {
            jwtmap.put(gitType, info.getId());
            token = JWTUtils.getToken(jwtmap);
            log.info("该用户未绑定");
            //返回token 用来绑定第三方账号
            return ResultMapUtils.ResultMapWithToken(false, 0, "返回token用于绑定账号", token);
        }
    }
}
