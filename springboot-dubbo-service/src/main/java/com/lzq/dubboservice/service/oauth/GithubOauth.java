package com.lzq.dubboservice.service.oauth;

import com.lzq.api.service.ouath.BaseOauth;
import org.springframework.stereotype.Component;

@Component
public class GithubOauth implements BaseOauth {
    private static final String GITHUB_CLIENT_ID = "62c3a5b4699818b5d1c1";
    private static final String GITHUB_CLIENT_SECRET = "609eaa8b1cf5a356b8deaa96a7d0be2f9689dec5";

    @Override
    public String accessToken(String code, String state) {
        return "https://github.com/login/oauth/access_token?" +
                "client_id="+GITHUB_CLIENT_ID+"&client_secret="+GITHUB_CLIENT_SECRET+"&code=" + code + "&state=" + state;
    }

    @Override
    public String userInfo(String accessToken) {
            return "https://api.github.com/user";
    }
}
