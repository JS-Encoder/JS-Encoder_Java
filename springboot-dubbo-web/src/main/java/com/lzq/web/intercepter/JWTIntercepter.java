package com.lzq.web.intercepter;

import com.auth0.jwt.exceptions.AlgorithmMismatchException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lzq.web.utils.JWTUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

public class JWTIntercepter implements HandlerInterceptor {


    /**
     * 自定义拦截器
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Map<String, Object> map = new HashMap();
        //获取头信息
        String token = request.getHeader("token");

        try {
            //验证jwt
            JWTUtils.verify(token);
            return true;
        } catch (SignatureVerificationException e) {
            map.put("msg", "无效签名");
        } catch (TokenExpiredException e) {
            map.put("msg", "签名过期");
        } catch (AlgorithmMismatchException e) {
            map.put("msg", "token算法不一致");
        } catch (Exception e) {
            map.put("msg", "未知错误");
        }

        map.put("state", false);
        String json = (new ObjectMapper()).writeValueAsString(map);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().println(json);
        return false;
    }
}