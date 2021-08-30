package com.lzq.dubboservice.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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
    public List<AccountResult> searchUserByName(AccountResult result) {
        QueryWrapper<AccountResult> wrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(result.getName())){
            wrapper.like("name",result.getName());
            return baseMapper.selectList(wrapper);
        }else {
            return null;
        }

    }

    @Override
    public List<AccountResult> getFollowList(AccountResult result) {
        if (StringUtils.isNotBlank(result.getUsername())){
            return baseMapper.getFollowList(result);
        }else {
            return null;
        }
    }

    @Override
    public List<AccountResult> getFanList(AccountResult result) {
        if (StringUtils.isNotBlank(result.getUsername())){
            return baseMapper.getFanList(result);
        }else {
            return null;
        }
    }
}
