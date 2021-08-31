package com.lzq.web.controller;

import com.github.pagehelper.PageInfo;
import com.lzq.api.dto.AccountResult;
import com.lzq.api.dto.ExampleAccount;
import com.lzq.api.service.AccountResultService;
import com.lzq.api.service.ExampleAccountService;
import com.lzq.web.utils.JWTUtils;
import com.lzq.web.utils.ResultMapUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * @author ：LZQ
 * @description：搜索接口
 * @date ：2021/8/25 15:40
 */
@Slf4j
@RestController
@RequestMapping("/search")
@Api(value = "搜索接口", description = "搜索接口")
public class SearchController {

    @Reference
    private AccountResultService accountResultService;

    @Reference
    private ExampleAccountService exampleAccountService;

    @Autowired
    private RedisTemplate redisTemplate;


    /**
     * 根据用户名查询用户信息
     *
     * @param username
     * @return
     */
    @GetMapping("/queryByUsername")
    @ApiOperation("根据用户名查询用户信息")
    public Map<String, Object> queryByUsername(String username) {
        AccountResult result = accountResultService.queryByUsername(username);
        return ResultMapUtils.ResultMap(true, 0, result);
    }


    /**
     * 获取关注列表
     *
     * @param result
     * @param currentPage 当前页
     * @return
     */
    @GetMapping("/getFollow")
    @ApiOperation("获取关注列表")
    public Map<String, Object> getFollowList(HttpServletRequest request, AccountResult result, Integer currentPage) {
        String username = null;
        PageInfo<AccountResult> list = null;
        //判断用户是否登录
        if (request.getHeader("token") != null) {
            username = JWTUtils.verify(request.getHeader("token"))
                    .getClaim("username").asString();
        }
        //登录的情况
        if (username != null) {
            //查看用户自己的关注
            if (username.equals(result.getUsername())) {
                list = accountResultService.getFollowList(result, currentPage);
                List<AccountResult> results = list.getList();
                for (AccountResult accountResult : results) {
                    accountResult.setMyFollow(true);
                }
                list.setList(results);
            } else {  //用户查看他人的关注
                //获取缓存中的所有关注的用户
                List<String> followList = redisTemplate.opsForList().range(username, 0, -1);
                list = accountResultService.getFollowList(result, currentPage);
                //通过遍历查找用户也关注的用户
                List<AccountResult> results = list.getList();
                for (AccountResult accountResult : results) {
                    //当查询到同样的用户时，修改其状态
                    if (followList.contains(accountResult.getUsername())) {
                        accountResult.setMyFollow(true);
                    } else if (username.equals(accountResult.getUsername())) {
                        //当用户查询到自己时不需要设置任何状态
                        accountResult.setMyFollow(null);
                    }
                }
                //添加回分页集合中
                list.setList(results);
            }
        } else { //未登录的情况
            list = accountResultService.getFollowList(result, currentPage);
        }
        return ResultMapUtils.ResultMap(true, 0, list);
    }

    /**
     * 获取粉丝列表
     *
     * @param request
     * @param result      实体类
     * @param currentPage 当前页
     * @return
     */
    @GetMapping("/getFan")
    @ApiOperation("获取粉丝列表")
    public Map<String, Object> getFanList(HttpServletRequest request, AccountResult result, Integer currentPage) {
        String username = null;
        PageInfo<AccountResult> list = null;
        //判断用户是否登录
        if (request.getHeader("token") != null) {
            username = JWTUtils.verify(request.getHeader("token"))
                    .getClaim("username").asString();
        }
        //登录的情况
        if (username != null) {
            //查看用户自己的粉丝
            if (username.equals(result.getUsername())) {
                list = accountResultService.getFanList(result, currentPage);
                List<AccountResult> results = list.getList();
                log.info("我到了 哈哈哈");
                //获取缓存中的所有关注的用户
                List<String> followList = redisTemplate.opsForList().range(username, 0, -1);
                for (AccountResult accountResult : results) {
                    //当互关时，修改状态
                    if (followList.contains(accountResult.getUsername())) {
                        accountResult.setMyFollow(true);
                    }
                }
                list.setList(results);
            } else {  //用户查看他人的粉丝
                //获取缓存中的所有关注的用户
                List<String> followList = redisTemplate.opsForList().range(username, 0, -1);
                list = accountResultService.getFanList(result, currentPage);
                //通过遍历查找当前用户也关注的用户
                List<AccountResult> results = list.getList();
                for (AccountResult accountResult : results) {
                    //当查询到同样的用户时，修改其状态
                    if (followList.contains(accountResult.getUsername())) {
                        accountResult.setMyFollow(true);
                    } else if (username.equals(accountResult.getUsername())) {
                        //当用户查询到自己时不需要设置任何状态
                        accountResult.setMyFollow(null);
                    }
                }
                //添加回分页集合中
                list.setList(results);
            }
        } else { //未登录的情况
            list = accountResultService.getFanList(result, currentPage);
        }
        return ResultMapUtils.ResultMap(true, 0, list);
    }


    /**
     * 根据实例名查询实例
     *
     * @param request
     * @param exampleName 实例名
     * @param currentPage 当前页
     * @return
     */
    @GetMapping("/queryByExampleName")
    @ApiOperation("根据实例名查询实例")
    public Map<String, Object> queryByExampleName(HttpServletRequest request, String exampleName, Integer currentPage) {
        String username = null;
        if (request.getHeader("token") != null) {
            //获取token中的用户名
            username = JWTUtils.verify(request.getHeader("token"))
                    .getClaim("username").toString();
        }
        //先查询实例用户集合
        PageInfo<ExampleAccount> pageInfo = exampleAccountService.queryByExampleName(exampleName, currentPage);
        List<ExampleAccount> list = pageInfo.getList();
        //当用户不登陆时不需要进行任何操作查询数据直接返回
        if (username != null) {
            List<String> followlist = redisTemplate.opsForList().range(username, 0, -1);
            List<String> favoriteslist = redisTemplate.opsForList().range(username + "fav", 0, -1);
            //遍历修改数组
            for (ExampleAccount exampleAccount : list) {
                //当该用户被当前用户关注时设置为true,否则为false
                if (followlist.contains(exampleAccount.getUsername())) {
                    exampleAccount.setMyFollow(true);
                }
                //当该实例被当前用户所喜爱时设置为true,否则为false
                if (favoriteslist.contains(exampleAccount.getExampleId())) {
                    exampleAccount.setMyFavorites(true);
                }
            }
            //把修改好的数据重新放入分页中
            pageInfo.setList(list);
        }
        log.info(list.toString());
        return ResultMapUtils.ResultMap(true, 0, pageInfo);
    }

}
