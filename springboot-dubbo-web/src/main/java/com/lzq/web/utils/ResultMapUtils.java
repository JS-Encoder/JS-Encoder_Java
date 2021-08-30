package com.lzq.web.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ：LZQ
 * @description：用来封装返回的数据
 * @date ：2021/8/24 11:05
 */
public class ResultMapUtils {

    public static Map<String,Object> ResultMapWithToken(Boolean state, Integer msg, Object data, String token){
        Map<String, Object> map = new HashMap<>();
        map.put("state",state);
        map.put("msg",msg);
        map.put("data",data);
        map.put("token",token);
        return map;
    }

    public static Map<String,Object> ResultMap(Boolean state, Integer msg, Object data){
        Map<String, Object> map = new HashMap<>();
        map.put("state",state);
        map.put("msg",msg);
        map.put("data",data);
        return map;
    }
}
