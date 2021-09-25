package com.lzq.web.controller;

import com.auth0.jwt.JWT;
import com.lzq.api.pojo.Content;
import com.lzq.api.service.ContentService;
import com.lzq.web.utils.JWTUtils;
import com.lzq.web.utils.ResultMapUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * @author ：LZQ
 * @description：TODO
 * @date ：2021/8/27 16:53
 */
@Slf4j
@RestController
@RequestMapping("/content")
@Api(value = "实例内容接口", description = "实例内容接口")
public class ContentController {

    @Reference
    private ContentService contentService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 获取实例内容
     *
     * @param exampleId 实例id
     * @param username  用户名
     * @return
     */
    @GetMapping("/getContent")
    @ApiOperation("获取实例内容")
    public Map<String, Object> getContent(HttpServletRequest request, String exampleId, String username) {
        log.info("获取实例内容接口"+exampleId+"------"+username);
        String token = request.getHeader("token");
        Content content=null;
        //当token不为空时用户已登录
        if (StringUtils.isNotBlank(token)){
            content = contentService.getContent(exampleId, username,1);
            String s = JWTUtils.verify(token).getClaim("username").asString();
            List<Integer> favoriteslist = redisTemplate.opsForList().range(s + "fav", 0, -1);
            //判断改实例是否为用户的喜爱
            if (favoriteslist.contains(content.getExampleId())){
                content.setMyFavorites(true);
            }
        }else {
            content = contentService.getContent(exampleId, username,0);
        }
        return ResultMapUtils.ResultMap(true, 0, content);
    }
}
