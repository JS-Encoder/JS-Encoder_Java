package com.lzq.dubboservice.service.oauth;

import com.alibaba.fastjson.JSONObject;

import com.lzq.api.service.ouath.BaseOuathService;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@Service(version ="1.0" ,interfaceClass = BaseOuathService.class)
public class GiteeServiceImpl implements BaseOuathService {

    @Autowired
    GiteeOauth giteeOauth;
    @Autowired
    RestTemplate restTemplate;


    @Override
    public String getAccessToken(String code, String state) {
        String token = this.giteeOauth.accessToken(code, state);
        ResponseEntity<Object> entity = this.restTemplate.postForEntity(token, httpEntity(), Object.class, new Object[0]);
        Object body = entity.getBody();
        assert body != null;
        String string = body.toString();
        String[] split = string.split("=");
        String accessToken = split[1].toString().split(",")[0];
        return accessToken;

    }

    @Override
    public JSONObject getUserInfo(String accessToken) {
        String userInfo = this.giteeOauth.userInfo(accessToken);
        ResponseEntity<JSONObject> forEntity = restTemplate.exchange(userInfo, HttpMethod.GET, httpEntity(), JSONObject.class, new Object[0]);
        System.out.println(forEntity);
        JSONObject body = forEntity.getBody();
        return body;
    }


    public static HttpEntity httpEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36");
        HttpEntity<HttpHeaders> request = new HttpEntity(headers);
        return request;
    }
}
