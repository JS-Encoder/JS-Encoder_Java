package com.lzq.web.controller;

import com.lzq.api.pojo.Account;
import com.lzq.api.pojo.Follow;
import com.lzq.api.pojo.Role;
import com.lzq.api.service.AccountService;
import com.lzq.api.service.FollowService;
import com.lzq.api.service.MailService;
import com.lzq.api.service.RoleService;
import com.lzq.web.utils.QiniuyunUtils;
import com.lzq.web.utils.ResultMapUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * @author ：LZQ
 * @description：用户个人信息接口
 * @date ：2021/8/24 15:52
 */
@Slf4j
@RestController
@RequestMapping(value = "/user")
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

    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    /**
     * 更新用户信息
     *
     * @param account
     * @return
     */
    @PutMapping("/")
    @ApiOperation("更新用户信息")
    public Map<String, Object> updateUserInfo(HttpSession session, Account account,String oldImg) {
        log.info(oldImg);
        if (StringUtils.isNotBlank(oldImg)){
            QiniuyunUtils.deleteFiles(oldImg);
        }
        Map<String, Object> map = (Map<String, Object>) session.getAttribute("map");
        Account data = (Account) map.get("data");
        log.info("进入更新用户信息接口：" + account);
        try {
            accountService.update(account);
            //更新成功则更新verify中的数据
            data.setName(account.getName());
            data.setUserPicture(account.getUserPicture());
            map.put("data", data);
            session.setAttribute("map", map);
            return ResultMapUtils.ResultMap(true, 0, null);
        } catch (Exception e) {
            return ResultMapUtils.ResultMap(false, 0, null);
        }
    }

    /**
     * 解绑Github
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
        log.info("进入了解绑gitee接口：" + account.toString());
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
    public Map<String, Object> updatEmail(Account account,String code) {
        log.info("我进入了更新注册邮箱接口：" + account.getUsername());
        Account byEmail = accountService.queryByEmail(account.getEmail());
        log.info("更新注册邮箱："+account.getEmail());
        String resultCode=stringRedisTemplate.opsForValue().get(account.getEmail());
        log.info(resultCode);
        Account byPassword = accountService.queryByPassword(account);
        //当新邮箱没被注册过且用户名和验证码正确时进行邮箱修改
        if (code.equals(resultCode)){
            if (byEmail != null) {
                return ResultMapUtils.ResultMap(false, 2, "邮箱已被注册");
            } else if (byPassword!=null) {
                Boolean bol = accountService.update(account);
                return ResultMapUtils.ResultMap(bol, 0, null);
            }else {
                return ResultMapUtils.ResultMap(false,1,"密码错误");
            }
        }else {
            return ResultMapUtils.ResultMap(false,3,"验证码错误");
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
        log.info("添加关注：-----"+follow.toString());
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
    @DeleteMapping("/cancelFollow")
    @ApiOperation("取消关注")
    public Map<String, Object> cancelFollow(Follow follow) {
        log.info("取消关注：-----"+follow.toString());
        boolean bol = followService.cancelFollow(follow);
        if (bol) {
            //移除缓存
            redisTemplate.opsForList().remove(follow.getUsername(), 0, follow.getFollowUsername());
        }
        return ResultMapUtils.ResultMap(bol, 0, null);
    }

}
