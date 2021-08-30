package com.lzq.web.utils;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lzq.api.pojo.Account;
import com.lzq.api.service.AccountService;


import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class UserUtils {

    /**
     * 用户与第三方id进行绑定
     * @param account
     * @param headtoken
     * @param accountService
     * @return
     */
    public static Map<String, Object> BindAccount(Account account, String headtoken, AccountService accountService) throws Exception {
        //用户保存jwt信息
        HashMap<String, String> jwtmap = new HashMap();
        //判断是否有token 有token则需要进行第三方绑定 没有则正常登录返回结果
        if (headtoken != null && headtoken != "") {
            //验证token
            DecodedJWT verify = JWTUtils.verify(headtoken);
            //获取token中的信息
            String id = verify.getClaim("githubId").asString();
            //判断是github登录还是gitee登录
            if (id != null && id != "") {
                if (account.getGithubId() != "" && account.getGithubId() != null) {
                    //该用户已经绑定了github账号
                    return ResultMapUtils.ResultMapWithToken(false,1,null,null);
                }
                account.setGithubId(id);
            } else {
                if (account.getGiteeId() != "" && account.getGiteeId() != null) {
                    //该用户已经绑定了gitee账号
                    return ResultMapUtils.ResultMapWithToken(false,1,null,null);

                }
                account.setGiteeId(verify.getClaim("giteeId").asString());
            }
            //更新数据进行账号绑定
            accountService.update(account);
        }
        //登录成功生产token
        jwtmap.put("username", account.getUsername());
        String token = JWTUtils.getToken(jwtmap);
        //返回信息
        return ResultMapUtils.ResultMapWithToken(true,0,account,token);
    }


    //返回信息和请求头的设置
    public static void responseMessage(HttpServletResponse response, Map<String, Object> map, ObjectMapper objectMapper) throws IOException {
        response.setContentType("application/json;charset=utf-8");
        response.getWriter().write(objectMapper.writeValueAsString(map));
    }

}
