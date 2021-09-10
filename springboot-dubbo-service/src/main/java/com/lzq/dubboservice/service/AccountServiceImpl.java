package com.lzq.dubboservice.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.lzq.api.pojo.Account;
import com.lzq.api.service.AccountService;
import com.lzq.dubboservice.mapper.AccountMapper;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author ：LZQ
 * @description：AccountService实现类
 * @date ：2021/8/23 15:52
 */
@Component
@Service(interfaceClass = AccountService.class)
public class AccountServiceImpl extends ServiceImpl<AccountMapper, Account> implements AccountService {


    @Override
    public Account queryByEmail(String email) {
        QueryWrapper<Account> wrapper = new QueryWrapper<>();
        wrapper.eq("email",email);
        return baseMapper.selectOne(wrapper);
    }

    @Override
    public void insert(Account account) {
        baseMapper.insert(account);
    }

    @Override
    public Boolean update(Account account){
        QueryWrapper<Account> wrapper = new QueryWrapper<>();
        //当同时存在时则修改邮箱
        if (StringUtils.isNotBlank(account.getUsername())){
            wrapper.eq("username",account.getUsername());
        }else if (!StringUtils.isNotBlank(account.getUsername())
                && StringUtils.isNotBlank(account.getEmail())){
            //当用户名为空邮箱不为空时修改密码
            wrapper.eq("email",account.getEmail());
        }

        return baseMapper.update(account,wrapper)>0?true:false;
    }

    @Override
    public Boolean bindGit(Account account) {
        return baseMapper.bindGit(account)>0?true:false;
    }

    @Override
    public Boolean addWorks(String username) {
        return baseMapper.addWorks(username)>0?true:false;
    }

    @Override
    public Boolean reduceWorks(String username) {
        return baseMapper.reduceWorks(username)>0?true:false;
    }

    @Override
    public Boolean addFavorites(String username) {
        return baseMapper.addFavorites(username)>0?true:false;
    }

    @Override
    public Boolean updateFavorites(Account account) {
        return baseMapper.updateFavorites(account)>0?true:false;
    }

    @Override
    public Account queryByGitId(String githubId, String giteeId) {
        QueryWrapper<Account> wrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(githubId)){
            wrapper.eq("github_id",githubId);
        }
        if (StringUtils.isNotBlank(giteeId)){
            wrapper.eq("gitee_id",giteeId);
        }
        return baseMapper.selectOne(wrapper);
    }

    @Override
    public Account queryByUsername(String username) {
        QueryWrapper<Account> wrapper = new QueryWrapper<>();
        wrapper.eq("username",username);
        return baseMapper.selectOne(wrapper);
    }

    @Override
    public void deleteAccount(Account account) {
        QueryWrapper<Account> wrapper = new QueryWrapper<>();
        wrapper.eq("username",account.getUsername());
        baseMapper.delete(wrapper);
    }

    @Override
    public Boolean increaseRecycle(String username) {
        return baseMapper.increaseRecycle(username)>0?true:false;
    }

    @Override
    public Boolean reduceRecycle(String username) {
        return baseMapper.reduceRecycle(username)>0?true:false;
    }

    @Override
    public PageInfo<Account> getFollowList(Account result, Integer currentPage) {
        PageHelper.startPage(currentPage,24);
        List<Account> list = baseMapper.getFollowList(result);
        return new PageInfo<>(list);

    }

    @Override
    public PageInfo<Account> getFanList(Account result,Integer currentPage) {
        PageHelper.startPage(currentPage,24);
        List<Account> list = baseMapper.getFanList(result);
        return new PageInfo<>(list);
    }

}
