package com.lzq.dubboservice.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
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
    public Example insert(Example example) {
        return baseMapper.insert(example) > 0 ? example : null;
    }

    @Override
    public Boolean update(Example example) {
        return baseMapper.updateById(example) > 0 ? true : false;
    }

    @Override
    public boolean deleteById(String exampleId) {
        return baseMapper.deleteById(exampleId) > 0 ? true : false;
    }

    @Override
    public PageInfo<Example> queryByAccount(String username, Integer currentPage, Integer orderCondition, Integer ispublic) {
        QueryWrapper<Example> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username);
        if (ispublic==0){
            wrapper.eq("ispublic", 0);
        }
        switch (orderCondition) {
            case 0:
                wrapper.orderByDesc("create_time");
                break;
            case 1:
                wrapper.orderByDesc("update_time");
                break;
            case 2:
                wrapper.orderByDesc("favorites");
                break;
        }
        //当前页和每页条数
        PageHelper.startPage(currentPage, 12);
        List<Example> list = baseMapper.selectList(wrapper);
        return new PageInfo<>(list);
    }

    @Override
    public Boolean deleteExample(String exampleId) {
        return baseMapper.deleteExample(exampleId) > 0 ? true : false;

    }

    @Override
    public Example queryById(String exampleId) {
        QueryWrapper<Example> wrapper = new QueryWrapper<>();
        wrapper.eq("example_id", exampleId);
        return baseMapper.selectOne(wrapper);
    }

    @Override
    public Example queryByIdUsername(Example example) {
        QueryWrapper<Example> wrapper = new QueryWrapper<>();
        wrapper.eq("username", example.getUsername());
        wrapper.eq("example_id", example.getExampleId());
        return baseMapper.selectOne(wrapper);
    }

    @Override
    public List<Example> queryRecycle(String username) {
        return baseMapper.queryDeleted(username);
    }

    @Override
    public Boolean resumeExample(String exampleId) {
        return baseMapper.resumeExample(exampleId) > 0 ? true : false;
    }

    @Override
    public Example getExampleByDeleted(Example example) {
        return baseMapper.getExampleByDeleted(example);
    }


}
