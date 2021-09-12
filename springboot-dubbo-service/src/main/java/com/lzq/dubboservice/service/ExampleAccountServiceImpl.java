package com.lzq.dubboservice.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.lzq.api.dto.ExampleAccount;
import com.lzq.api.pojo.Content;
import com.lzq.api.service.ExampleAccountService;
import com.lzq.dubboservice.mapper.ExampleAccountMapper;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author ：LZQ
 * @description：ExampleAccountService实现类
 * @date ：2021/8/30 14:25
 */
@Component
@Service(interfaceClass = ExampleAccountService.class)
public class ExampleAccountServiceImpl extends ServiceImpl<ExampleAccountMapper, ExampleAccount> implements ExampleAccountService {

    @Override
    public PageInfo<ExampleAccount> queryExample(String queryContent, Integer currentPage, Integer orderCondition, Content content) {
        //当前页和每页条数
        PageHelper.startPage(currentPage, 12);
        //获取全部数据
        List<ExampleAccount> list = baseMapper.queryExample(queryContent, orderCondition, content);
        return new PageInfo<>(list);
    }

    @Override
    public PageInfo<ExampleAccount> queryPersonFavorites(String username, Integer currentPage, Integer orderCondition) {
        //当前页和每页条数
        PageHelper.startPage(currentPage, 12);
        List<ExampleAccount> list = baseMapper.queryPersonFavorites(username, orderCondition);
        return new PageInfo<>(list);
    }
}
