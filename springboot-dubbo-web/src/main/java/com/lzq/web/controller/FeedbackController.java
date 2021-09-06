package com.lzq.web.controller;

import com.lzq.api.pojo.Feedback;
import com.lzq.api.service.FeedbackService;
import com.lzq.web.utils.ResultMapUtils;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @author ：LZQ
 * @description：用户反馈
 * @date ：2021/9/6 13:57
 */
@RestController
@RequestMapping("/feedback")
public class FeedbackController {

    @Reference
    private FeedbackService feedbackService;

    /**
     * 获取所有用户的反馈
     * @return
     */
    @Secured("ROLE_root")
    @ApiOperation("获取所有用户的反馈")
    @RequestMapping("/getAllFeedback")
    public Map<String, Object> getAllFeedback() {
        List<Feedback> feedback = feedbackService.getAllFeedback();
        return ResultMapUtils.ResultMap(true,0,feedback);
    }
}
