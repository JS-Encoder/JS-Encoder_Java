package com.lzq.dubboservice.service.oauth;


import com.lzq.api.service.ouath.BaseOauth;
import org.springframework.stereotype.Component;

@Component
public class GiteeOauth implements BaseOauth {

    private static final String GITEE_CLIENT_ID = "";
    private static final String GITEE_CLIENT_SECRET = "";
    private static final String REDIRECT_URI = "";

    @Override
    public String accessToken(String code, String state) {
       return "https://gitee.com/oauth/token?grant_type=authorization_code&code=" + code
               + "&client_id=" + GITEE_CLIENT_ID +
                "&redirect_uri=" + REDIRECT_URI +
               "&client_secret=" +GITEE_CLIENT_SECRET;
    }

    @Override
    public String userInfo(String accessToken) {
        return "https://gitee.com/api/v5/user?access_token=" + accessToken;
    }
}
