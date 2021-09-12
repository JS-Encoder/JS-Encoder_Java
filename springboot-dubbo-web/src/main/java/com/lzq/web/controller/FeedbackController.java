package com.lzq.web.controller;

import com.lzq.api.pojo.Feedback;
import com.lzq.api.service.FeedbackService;
import com.lzq.web.utils.ResultMapUtils;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @author ：LZQ
 * @description：用户反馈
 * @date ：2021/9/6 13:57
 */
@Slf4j
@RestController
@RequestMapping("/feedback")
public class FeedbackController {

    @Reference
    private FeedbackService feedbackService;

    /**
     * 获取所有用户的反馈
     *
     * @return
     */
    @Secured("ROLE_root")
    @ApiOperation("获取所有用户的反馈")
    @GetMapping("/getAllFeedback")
    public Map<String, Object> getAllFeedback() {
        log.info("获取反馈列表");
        List<Feedback> feedback = feedbackService.getAllFeedback();
        return ResultMapUtils.ResultMap(true, 0, feedback);
    }

    /**
     * 添加反馈
     *
     * @param feedback
     * @return
     */
    @ApiOperation("添加反馈")
    @PostMapping("/addFeedback")
    public Map<String, Object> addFeedback(Feedback feedback) {
        log.info("添加反馈"+feedback.getUsername());
        Boolean bol = feedbackService.addFeedback(feedback);
        return ResultMapUtils.ResultMap(bol, 0, null);
    }
}
