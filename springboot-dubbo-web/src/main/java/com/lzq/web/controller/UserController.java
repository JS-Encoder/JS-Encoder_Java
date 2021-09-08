package com.lzq.web.controller;

import com.lzq.api.pojo.Account;
import com.lzq.api.pojo.Follow;
import com.lzq.api.pojo.Role;
import com.lzq.api.service.AccountService;
import com.lzq.api.service.FollowService;
import com.lzq.api.service.MailService;
import com.lzq.api.service.RoleService;
import com.lzq.web.utils.ResultMapUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author ：LZQ
 * @description：用户个人信息接口
 * @date ：2021/8/24 15:52
 */
@Slf4j
@RestController
@RequestMapping("/user")
@Api(value = "用户个人信息接口", description = "用户个人信息接口")
public class UserController {

    @Reference
    private AccountService accountService;

    @Reference
    private FollowService followService;

    @Reference
    RoleService roleService;

    @Reference
    private MailService mailService;

    @Autowired
    private RedisTemplate redisTemplate;


    /**
     * 更新用户信息
     *
     * @param account
     * @return
     */
    @PutMapping("/")
    @ApiOperation("更新用户信息")
    public Map<String, Object> updateUserInfo(Account account) {
        try {
            accountService.update(account);
            return ResultMapUtils.ResultMap(true, 0, null);
        } catch (Exception e) {
            return ResultMapUtils.ResultMap(false, 0, null);
        }
    }

    /**
     * 解绑Github
     *
     * @param account
     * @return
     */
    @PutMapping("/unbindGithub")
    @ApiOperation("解绑Github")
    public Map<String, Object> unbindGithub(HttpServletRequest request, Account account) {
        account.setGithubId("");
        try {
            Boolean update = accountService.update(account);
            if (update) {
                //更新成功则更新accunt基本信息
                Account query = accountService.queryByUsername(account.getUsername());
                Role role = roleService.queryById(query.getRoleId());
                query.setRole(role);
                request.getSession().setAttribute("map", ResultMapUtils.ResultMap(true, 0, query));
            }

            return ResultMapUtils.ResultMap(update, 0, null);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMapUtils.ResultMap(false, 0, null);
        }
    }

    /**
     * 解绑Gitee
     *
     * @param account
     * @return
     */
    @PutMapping("/unbindGitee")
    @ApiOperation("解绑Gitee")
    public Map<String, Object> unbindGitee(HttpServletRequest request, Account account) {
        log.info("进入了解绑gitee接口："+account.toString());
        account.setGiteeId("");
        try {
            Boolean update = accountService.update(account);
            if (update) {
                //更新成功则更新accunt基本信息
                Account query = accountService.queryByUsername(account.getUsername());
                Role role = roleService.queryById(query.getRoleId());
                query.setRole(role);
                request.getSession().setAttribute("map", ResultMapUtils.ResultMap(true, 0, query));
            }
            return ResultMapUtils.ResultMap(update, 0, null);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMapUtils.ResultMap(false, 0, null);
        }

    }

    /**
     * 删除用户
     *
     * @param account
     * @return
     */
    @DeleteMapping("/")
    @ApiOperation("删除用户")
    public Map<String, Object> deleteAccount(Account account) {
        try {
            accountService.deleteAccount(account);
            return ResultMapUtils.ResultMap(true, 0, null);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMapUtils.ResultMap(false, 0, null);
        }
    }


    /**
     * 更新注册邮箱
     *
     * @return
     */
    @PutMapping("/updateEmail")
    @ApiOperation("更新注册邮箱")
    public Map<String, Object> updatEmail(Account account) {
        try {
            Boolean bol = accountService.update(account);
            return ResultMapUtils.ResultMapWithToken(bol, 0, null, null);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMapUtils.ResultMapWithToken(false, 1, null, null);
        }
    }

    /**
     * 添加关注
     *
     * @param follow 实体类
     * @return
     */
    @PostMapping("/addFollow")
    @ApiOperation("添加关注")
    public Map<String, Object> addFollow(Follow follow) {
        if (StringUtils.isNotBlank(follow.getUsername())) {
            boolean bol = followService.addFollow(follow);
            if (bol) {
                //把数据存到redis缓存中
                redisTemplate.opsForList().leftPush(follow.getUsername(), follow.getFollowUsername());
            }
            return ResultMapUtils.ResultMap(bol, 0, null);
        } else {
            return ResultMapUtils.ResultMap(false, 0, null);
        }
    }

    /**
     * 取消关注
     *
     * @param follow
     * @return
     */
    @PutMapping("/cancelFollow")
    @ApiOperation("取消关注")
    public Map<String, Object> cancelFollow(Follow follow) {
        boolean bol = followService.cancelFollow(follow);
        if (bol) {
            //移除缓存
            redisTemplate.opsForList().remove(follow.getUsername(), 0, follow.getFollowUsername());
        }
        return ResultMapUtils.ResultMap(bol, 0, null);
    }


}
