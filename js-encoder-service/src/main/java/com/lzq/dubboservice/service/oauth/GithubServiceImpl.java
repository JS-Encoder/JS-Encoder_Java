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

import java.util.Objects;

@Service(version = "2.0",interfaceClass = BaseOuathService.class)
public class GithubServiceImpl implements BaseOuathService {

    @Autowired
    GithubOauth githubOauth;
    @Autowired
    private RestTemplate restTemplate;

    @Override
    public String getAccessToken(String code, String state) {
        String token = this.githubOauth.accessToken(code, state);
        ResponseEntity<Object> forEntity = this.restTemplate.exchange(token, HttpMethod.GET, gethttpEntity(), Object.class, new Object[0]);
        String[] split = Objects.requireNonNull(forEntity.getBody()).toString().split("=");
        String accessToken = split[1].split(",")[0];
        return accessToken;
    }

    @Override
    public JSONObject getUserInfo(String accessToken) {
        String userInfo = this.githubOauth.userInfo(accessToken);
        ResponseEntity<JSONObject> entity = this.restTemplate.exchange(userInfo, HttpMethod.GET, httpEntity(accessToken), JSONObject.class, new Object[0]);
        System.out.println(entity);
        JSONObject body = (JSONObject)entity.getBody();
        return body;
    }


    public static HttpEntity httpEntity(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "token " + token);
        HttpEntity<HttpHeaders> request = new HttpEntity(headers);
        return request;
    }

    public static HttpEntity gethttpEntity() {
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> string = new HttpEntity("String", headers);
        return string;
    }
}
