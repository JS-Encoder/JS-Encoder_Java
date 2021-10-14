package com.lzq.api.service;

import com.lzq.api.pojo.Feedback;

import java.util.List;

/**
 * @author ：LZQ
 * @description：反馈接口
 * @date ：2021/9/6 14:04
 */
public interface FeedbackService {

    /**
     * 获取反馈列表
     * @return
     */
    List<Feedback> getAllFeedback();

    /**
     *
     * @return
     * @param feedback
     */
    Boolean addFeedback(Feedback feedback);
}
