package com.lzq.dubboservice.service.oauth;


import com.lzq.api.service.ouath.BaseOauth;
import org.springframework.stereotype.Component;

@Component
public class GiteeOauth implements BaseOauth {

    private static final String GITEE_CLIENT_ID = "a1169e70a8897a7911997856abb65956137998c3c79ffdd05c1856150eb7504f";
    private static final String GITEE_CLIENT_SECRET = "988f9440d692789a2a927fcc7403f1246e6a55ae3349ebb38504c9cc7e1525e2";
    private static final String REDIRECT_URI = "http://localhost:8080/?type=gitee";

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
