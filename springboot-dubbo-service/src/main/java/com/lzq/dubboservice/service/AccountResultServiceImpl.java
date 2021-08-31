package com.lzq.dubboservice.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.lzq.api.dto.AccountResult;
import com.lzq.api.service.AccountResultService;
import com.lzq.dubboservice.mapper.AccountResultMapper;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author ：LZQ
 * @description：AccountResultService实现类
 * @date ：2021/8/23 15:52
 */
@Component
@Service(interfaceClass = AccountResultService.class)
public class AccountResultServiceImpl extends ServiceImpl<AccountResultMapper, AccountResult> implements AccountResultService {


    @Override
    public AccountResult queryByUsername(String username) {
        QueryWrapper<AccountResult> wrapper = new QueryWrapper<>();
        wrapper.eq("username",username);
        return baseMapper.selectOne(wrapper);
    }

    @Override
    public PageInfo<AccountResult> getFollowList(AccountResult result, Integer currentPage) {
        Page<AccountResult> page = new Page<>();
        PageHelper.startPage(currentPage,24);
        List<AccountResult> list = baseMapper.getFollowList(result);
        return new PageInfo<>(list);

    }

    @Override
    public PageInfo<AccountResult> getFanList(AccountResult result,Integer currentPage) {
        Page<AccountResult> page = new Page<>();
        PageHelper.startPage(currentPage,24);
        List<AccountResult> list = baseMapper.getFanList(result);
        return new PageInfo<>(list);
    }
}
