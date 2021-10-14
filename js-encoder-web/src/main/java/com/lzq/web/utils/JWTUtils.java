package com.lzq.web.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Calendar;
import java.util.Map;

public class JWTUtils {
    private static final String SING = "!QEFFF&^*%%%^&LZQLOVEWXR";


    //生产验证码
    public static String getToken(Map<String, String> map,Integer timeType,Integer time) {
        Calendar instance = Calendar.getInstance();
        //过期时间24小时后过期
        instance.add(timeType, time);
        JWTCreator.Builder builder = JWT.create();
        map.forEach((k, v) -> {
            builder.withClaim(k, v);
        });
        String token = builder.withExpiresAt(instance.getTime()).sign(Algorithm.HMAC256(SING));
        return token;
    }

    //验证token
    public static DecodedJWT verify(String token) {
        return JWT.require(Algorithm.HMAC256(SING)).build().verify(token);
    }
}
