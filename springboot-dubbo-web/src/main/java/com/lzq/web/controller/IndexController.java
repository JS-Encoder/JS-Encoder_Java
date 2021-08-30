package com.lzq.web.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lzq.api.pojo.*;
import com.lzq.api.service.AccountService;
import com.lzq.api.service.MailService;
import com.lzq.api.service.ouath.BaseOuathService;
import com.lzq.web.utils.HttpClientUtil;
import com.lzq.web.utils.JWTUtils;
import com.lzq.web.utils.ResultMapUtils;
import com.lzq.web.utils.VerifyCodeUtils;
import com.qiniu.util.Auth;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author ：LZQ
 * @description：主页面接口
 * @date ：2021/8/23 15:40
 */
@RestController
@RequestMapping({"/index"})
@Api(value = "主页面接口",description = "主页面接口")
public class IndexController {

    @Reference(version = "1.0")
    private BaseOuathService giteeService;
    @Reference(version = "2.0")
    private BaseOuathService githubService;

    @Reference
    private AccountService accountService;

    @Reference
    private MailService mailService;
    //redis
    @Resource
    private StringRedisTemplate stringRedisTemplate;


    /**
     * github第三方回调地址
     * @param request
     * @return
     */
    @GetMapping({"/github"})
    @ApiOperation("gituhub第三方登录调用接口")
    public Map<String, Object> callback(HttpServletRequest request) throws JsonProcessingException {
        Map<String, String> jwtmap = new HashMap();
        String code = request.getParameter("code");
        String state = request.getParameter("state");
        //获取github返回的信息
        JSONObject userInfo = githubService.getUserInfo(githubService.getAccessToken(code, state));
        //把返回的信息封装成对象
        UserInfo info = JSON.parseObject(userInfo.toString(), UserInfo.class);
        //根据githubid查询用户信息
        Account rest = accountService.queryByGitId(Integer.toString(info.getId()), null);
        String token;
        //判断改github是否进行过绑定 未绑定则进行绑定，绑定则返回用户信息
        if (rest != null) {
            Map<String, String> hashMap = new HashMap<>();
            ObjectMapper mapper = new ObjectMapper();
            //存储用户用于登录请求的信息
            hashMap.put("username",rest.getUsername());
            hashMap.put("password",rest.getPassword());
            String s = HttpClientUtil.doPost("http://127.0.0.1/index/login", hashMap);
            //把json转换为map
            Map<String,Object> resultMap = mapper.readValue(s,Map.class);
            return resultMap;
        } else {
            jwtmap.put("githubId", info.getId().toString());
            token = JWTUtils.getToken(jwtmap);
            //返回token 用来绑定第三方账号
            return ResultMapUtils.ResultMapWithToken(false,0,null,token);
        }

    }

    /**
     * gitee回调地址
     * @param request
     * @return
     */
    @GetMapping({"/gitee"})
    @ApiOperation("gitee第三方登录调用接口")
    public Map<String, Object> giteeCallBack(HttpServletRequest request) throws IOException {
        Map<String, String> jwtmap = new HashMap();
        Map<String, Object> map = new HashMap();
        String code = request.getParameter("code");
        String state = request.getParameter("state");
        JSONObject userInfo = giteeService.getUserInfo(this.giteeService.getAccessToken(code, state));
        //把json转化为对象
        UserInfo info = JSON.parseObject(userInfo.toString(), UserInfo.class);
        //根据giteeid查询用户信息
        Account rest = accountService.queryByGitId(null,Integer.toString(info.getId()));
        String token;
        //判断改gitee是否进行过绑定 未绑定则进行绑定，绑定则返回用户信息
        if (rest != null) {
            Map<String, String> hashMap = new HashMap<>();
            ObjectMapper mapper = new ObjectMapper();
            //存储用户用于登录请求的信息
            hashMap.put("username",rest.getUsername());
            hashMap.put("password",rest.getPassword());
            //通过第三方登录获取用户信息后 进行登录验证
            String s = HttpClientUtil.doPost("http://127.0.0.1/index/login", hashMap);
            //把json转换为map
            Map<String,Object> resultMap = mapper.readValue(s,Map.class);
            return resultMap;
        } else {
            jwtmap.put("giteeId", Integer.toString(info.getId()));
            token = JWTUtils.getToken(jwtmap);
            //返回token 用来绑定第三方账号
            return ResultMapUtils.ResultMapWithToken(false,0,null,token);
        }
    }

    /**
     * 发送邮箱
     * @param email
     * @return
     */
    @GetMapping({"/send"})
    @ApiOperation("发送邮箱验证")
    public Map<String, Object> sendEmail(String email) {
        String code = VerifyCodeUtils.getCode();
        Mail mail = new Mail();
        mail.setTo(email);
        mail.setSubject("验证码");
        mail.setMailContent(code);
        boolean bol = mailService.sendActiveMail(mail);
        //把验证码存储到redis中
        stringRedisTemplate.opsForValue().set(email, code, 300L, TimeUnit.SECONDS);
        return ResultMapUtils.ResultMap(bol,0,null);
    }

    /**
     * 注册
     * @param account
     * @param code
     * @return
     */
    @PostMapping({"/register"})
    @ApiOperation("用户注册")
    public Map<String, Object> register(Account account, String code) {
        //获取redis中的验证码
        String s = stringRedisTemplate.opsForValue().get(account.getEmail());
        if (code.equals(s)) {
            try {
                System.out.println("我到了");
                accountService.insert(account);
                //注册成功
                return ResultMapUtils.ResultMap(true,0,null);
            } catch (DuplicateKeyException e) {
                e.printStackTrace();
                //注册失败,用户名已存在
                return ResultMapUtils.ResultMap(false,1,null);
            }
        } else {
            //注册失败，验证码不存在
            return ResultMapUtils.ResultMap(false,0,null);
        }
    }



    /**
     * 查询用户名是否已注册
     * @param username 用户名
     * @return
     */
    @GetMapping({"/isDuplicate"})
    @ApiOperation("用户是否已存在")
    public Boolean isDup(String username) {
        Account account = accountService.queryByUsername(username);
        return account != null ? false : true;
    }

    /**
     * 发送修改链接到邮箱
     * @param account
     * @return
     */
    @PostMapping("/sendPasswordEmail")
    @ApiOperation("发送修改链接到邮箱")
    public Map<String,Object> sendPasswordEmail(Account account){
        HashMap<String, String> map = new HashMap<>();
        //判断用户名是否不为空
        if (StringUtils.isNotBlank(account.getUsername())){
            map.put("email", account.getEmail());
            String token = JWTUtils.getToken(map);
            Mail mail = new Mail();
            mail.setTo(account.getEmail());
            mail.setSubject("修改密码链接");
            mail.setMailContent(token);
            //发送修改密码链接
            boolean b = mailService.sendActiveMail(mail);
            //返回token令牌
            return ResultMapUtils.ResultMap(true,0,null);
        }else {
            //用户名为空
            return ResultMapUtils.ResultMap(false,0,null);
        }
    }


    /**
     * 修改用户密码
     * @param request
     * @param account 实体对象
     * @return
     */
    @PutMapping("/modifyPossword")
    @ApiOperation("修改用户密码")
    public Map<String,Object> modifyPossword(HttpServletRequest request,Account account){
        //接收发送过来的token
        String token = request.getHeader("token");
        //判断token是否存在
        if (StringUtils.isNotBlank(token)){
            try {
                //验证token
                DecodedJWT verify = JWTUtils.verify(token);
                //获取用户名
                String email = verify.getClaim("email").asString();
                account.setEmail(email);
                //更新密码
                accountService.update(account);
                return ResultMapUtils.ResultMap(true,0,null);
            } catch (Exception e) {
                // e.printStackTrace();
                //token无效
                return ResultMapUtils.ResultMap(false,0,null);
            }
        }else {
            //token不存在
            return ResultMapUtils.ResultMap(false,1,null);
        }

    }



    /**
     * 获取七牛云的接口
     * @return
     */
    @GetMapping("/getToken")
    @ApiOperation("获取七牛云token")
    public String getToken(){
        //七牛云AK
        String ACCESS_KEY="Z_7eMJdtj_n4lrAdSs3zVuZ8rn4wZXu75b1gYJbC";
        //七牛云SK
        String SECRET_KEY="QIriVPlgNKoKdjU02q166-7IBPy3z9sQTMn5Ae7R";
        //空间名
        String bucket="js-encoder";
        Auth auth = Auth.create(ACCESS_KEY, SECRET_KEY);
        //获取当前空间的token
        String s = auth.uploadToken(bucket);
        //返回获取的token值
        return s;
    }

}