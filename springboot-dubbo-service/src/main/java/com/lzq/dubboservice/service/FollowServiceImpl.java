package com.lzq.dubboservice.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lzq.api.pojo.Account;
import com.lzq.api.pojo.Follow;
import com.lzq.api.service.FollowService;
import com.lzq.dubboservice.mapper.AccountMapper;
import com.lzq.dubboservice.mapper.FollowMapper;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * @author ：LZQ
 * @description：FollowService实现类
 * @date ：2021/8/25 10:48
 */
@Component
@Service(interfaceClass = FollowService.class)
public class FollowServiceImpl extends ServiceImpl<FollowMapper, Follow> implements FollowService {

    @Resource
    private AccountMapper accountMapper;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addFollow(Follow follow) {
        //插入数据
        int i = baseMapper.insert(follow);
        int i1 = 0, i2 = 0;
        if (i > 0) {
            do {
                //用来查询关注人
                QueryWrapper<Account> wrapperO = new QueryWrapper<>();
                wrapperO.eq("username", follow.getUsername());
                //获取关注人信息
                Account accountO = accountMapper.selectOne(wrapperO);
                accountO.setFollowing(accountO.getFollowing() + 1);
                //更新关注数
                i1 = accountMapper.update(accountO, wrapperO);
            } while (i1 == 0);
            do {
                //用来查询被关注人
                QueryWrapper<Account> wrapperT = new QueryWrapper<>();
                wrapperT.eq("username", follow.getFollowUsername());
                //获取被关注人信息
                Account accountT = accountMapper.selectOne(wrapperT);
                accountT.setFan(accountT.getFan() + 1);
                //更新粉丝数
                i2 = accountMapper.update(accountT, wrapperT);
            } while (i2 == 0);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean cancelFollow(Follow follow) {
        QueryWrapper<Follow> wrapper = new QueryWrapper<>();
        wrapper.eq("username", follow.getUsername());
        wrapper.eq("follow_username", follow.getFollowUsername());
        int delete = baseMapper.delete(wrapper);
        int i1 = 0, i2 = 0;
        if (delete > 0) {
            do {
                //用来查询关注人
                QueryWrapper<Account> wrapperO = new QueryWrapper<>();
                wrapperO.eq("username", follow.getUsername());
                //获取关注人信息
                Account accountO = accountMapper.selectOne(wrapperO);
                accountO.setFollowing(accountO.getFollowing() - 1);
                //更新关注数
                i1 = accountMapper.update(accountO, wrapperO);
            } while (i1 == 0);
            do {
                //用来查询被关注人
                QueryWrapper<Account> wrapperT = new QueryWrapper<>();
                wrapperT.eq("username", follow.getFollowUsername());
                //获取被关注人信息
                Account accountT = accountMapper.selectOne(wrapperT);
                accountT.setFan(accountT.getFan() - 1);
                //更新粉丝数
                i2 = accountMapper.update(accountT, wrapperT);
            } while (i2 == 0);
            return true;
        } else {
            return false;
        }
    }
}
