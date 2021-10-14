package com.lzq.api.service.ouath;

public interface BaseOauth {

    String accessToken(String code, String state);

    String userInfo(String accessToken);
}
