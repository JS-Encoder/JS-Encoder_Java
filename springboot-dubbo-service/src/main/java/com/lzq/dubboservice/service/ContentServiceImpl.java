package com.lzq.dubboservice.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lzq.api.pojo.Content;
import com.lzq.api.service.ContentService;
import com.lzq.dubboservice.mapper.ContentMapper;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.stereotype.Component;

/**
 * @author ：LZQ
 * @description：TODO
 * @date ：2021/8/27 14:10
 */
@Component
@Service(interfaceClass = ContentService.class)
public class ContentServiceImpl extends ServiceImpl<ContentMapper, Content> implements ContentService {

    @Override
    public Boolean addContent(Content content) {
        return baseMapper.insert(content) > 0 ? true : false;
    }

    @Override
    public Boolean updateContent(Content content) {
        QueryWrapper<Content> wrapper = new QueryWrapper<>();
        wrapper.eq("example_id", content.getExampleId());
        return baseMapper.update(content,wrapper) > 0 ? true : false;
    }

    @Override
    public Content getContent(Integer exampleId) {
        QueryWrapper<Content> wrapper = new QueryWrapper<>();
        wrapper.eq("example_id",exampleId);
        return baseMapper.selectOne(wrapper);
    }
}
