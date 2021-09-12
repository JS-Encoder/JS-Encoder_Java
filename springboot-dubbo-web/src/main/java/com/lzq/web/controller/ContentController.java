package com.lzq.web.controller;

import com.lzq.api.pojo.Content;
import com.lzq.api.service.ContentService;
import com.lzq.web.utils.ResultMapUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    /**
     * 获取实例内容
     *
     * @param exampleId 实例id
     * @param username  用户名
     * @return
     */
    @GetMapping("/getContent")
    @ApiOperation("获取实例内容")
    public Map<String, Object> getContent(String exampleId, String username) {
        log.info("获取实例内容接口"+exampleId+"------"+username);
        Content content = contentService.getContent(exampleId, username);
        return ResultMapUtils.ResultMap(true, 0, content);
    }
}
