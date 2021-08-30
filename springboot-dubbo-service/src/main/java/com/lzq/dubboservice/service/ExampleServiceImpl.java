package com.lzq.dubboservice.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lzq.api.pojo.Example;
import com.lzq.api.service.ExampleService;
import com.lzq.dubboservice.mapper.ExampleMapper;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author ：LZQ
 * @description：ExampleService实现类
 * @date ：2021/8/26 15:10
 */
@Component
@Service(interfaceClass = ExampleService.class)
public class ExampleServiceImpl extends ServiceImpl<ExampleMapper, Example> implements ExampleService {
    @Override
    public Example queryById(Integer exampleId) {
        return null;
    }

    @Override
    public List<Example> queryAllByLimit(int offset, int limit) {
        return null;
    }

    @Override
    public Boolean insert(Example example) {
        return baseMapper.insert(example) > 0 ? true : false;
    }

    @Override
    public Boolean update(Example example) {
        return baseMapper.updateById(example) > 0 ? true : false;
    }

    @Override
    public boolean deleteById(Integer exampleId) {
        return false;
    }

    @Override
    public List<Example> queryByAccount(String username) {
        QueryWrapper<Example> wrapper = new QueryWrapper<>();
        wrapper.eq("username",username);
        return baseMapper.selectList(wrapper);
    }

    @Override
    public List<Example> queryByPublic(String username) {
        QueryWrapper<Example> wrapper = new QueryWrapper<>();
        wrapper.eq("username",username);
        wrapper.eq("ispublic",0);
        return baseMapper.selectList(wrapper);
    }
}
