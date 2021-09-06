package com.lzq.web.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lzq.web.utils.ResultMapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

/**
 * @author ：LZQ
 * @description：请求权限接口时无权限无需跳转登录页面，改为返回字符串
 * @date ：2021/9/5 14:01
 */
public class MacLoginUrlAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
        httpServletResponse.setContentType("application/json;charset=utf-8");
        PrintWriter out = httpServletResponse.getWriter();
        StringBuffer sb = new StringBuffer();
        Map<String, Object> map = ResultMapUtils.ResultMap(false, 0, "权限不足");
        out.write(objectMapper.writeValueAsString(map));
        out.flush();
        out.close();
    }
}
