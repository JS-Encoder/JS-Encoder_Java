package com.lzq.dubboservice.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lzq.api.pojo.Feedback;
import com.lzq.api.service.FavoritesService;
import com.lzq.api.service.FeedbackService;
import com.lzq.dubboservice.mapper.FeedbackMapper;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author ：LZQ
 * @description：TODO
 * @date ：2021/9/6 14:10
 */
@Component
@Service(interfaceClass = FeedbackService.class)
public class FeedbackServiceImpl extends ServiceImpl<FeedbackMapper, Feedback> implements FeedbackService {

    @Override
    public List<Feedback> getAllFeedback() {
        return baseMapper.selectList(null);
    }
}
