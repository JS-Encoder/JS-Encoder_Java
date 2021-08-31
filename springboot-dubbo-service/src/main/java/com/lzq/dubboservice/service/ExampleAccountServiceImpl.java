package com.lzq.dubboservice.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.lzq.api.dto.ExampleAccount;
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
    public PageInfo<ExampleAccount> queryByExampleName(String exampleName,Integer currentPage) {
        Page<ExampleAccount> page = new Page<>();
        //当前页和每页条数
        PageHelper.startPage(currentPage,12);
        //获取全部数据
        List<ExampleAccount> list = baseMapper.queryByExampleName(exampleName);
        return new PageInfo<>(list);
    }
}
