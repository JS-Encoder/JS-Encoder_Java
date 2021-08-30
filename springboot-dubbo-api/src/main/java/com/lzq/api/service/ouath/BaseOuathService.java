package com.lzq.api.service.ouath;

import com.alibaba.fastjson.JSONObject;

public interface BaseOuathService {

    String getAccessToken(String code, String state);

    JSONObject getUserInfo(String accessToken);
}
