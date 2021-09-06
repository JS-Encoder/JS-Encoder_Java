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
    public Boolean insert(Example example) {
        return baseMapper.insert(example)>0?true:false;
    }

    @Override
    public Boolean update(Example example) {
        return baseMapper.updateById(example) > 0 ? true : false;
    }

    @Override
    public boolean deleteById(String exampleId) {
        return baseMapper.deleteById(exampleId)>0?true:false;
    }

    @Override
    public PageInfo<Example> queryByAccount(String username, Integer currentPage) {
        QueryWrapper<Example> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username);
        //当前页和每页条数
        PageHelper.startPage(currentPage, 12);
        List<Example> list = baseMapper.selectList(wrapper);
        return new PageInfo<>(list);
    }

    @Override
    public PageInfo<Example> queryByPublic(String username, Integer currentPage) {
        QueryWrapper<Example> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username);
        wrapper.eq("ispublic", 0);
        //当前页和每页条数
        PageHelper.startPage(currentPage, 12);
        List<Example> list = baseMapper.selectList(wrapper);
        return new PageInfo<>(list);
    }

    @Override
    public Boolean deleteExample(String exampleId) {
         return baseMapper.deleteExample(exampleId)>0?true:false;

    }

    @Override
    public Example queryById(String exampleId) {
        QueryWrapper<Example> wrapper = new QueryWrapper<>();
        wrapper.eq("example_id",exampleId);
        return baseMapper.selectOne(wrapper);
    }


}
