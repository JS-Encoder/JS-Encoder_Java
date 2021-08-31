package com.lzq.dubboservice.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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
    public List<ExampleAccount> queryByExampleName(String exampleName) {
        return baseMapper.queryByExampleName(exampleName);
    }
}
