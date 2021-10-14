package com.lzq.dubboservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lzq.api.pojo.Feedback;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author ：LZQ
 * @description：反馈接口
 * @date ：2021/9/6 14:04
 */
@Repository
public interface FeedbackMapper extends BaseMapper<Feedback> {

}
