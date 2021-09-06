package com.lzq.api.service;

import com.lzq.api.pojo.Feedback;

import java.util.List;

/**
 * @author ：LZQ
 * @description：反馈接口
 * @date ：2021/9/6 14:04
 */
public interface FeedbackService {

    List<Feedback> getAllFeedback();
}
