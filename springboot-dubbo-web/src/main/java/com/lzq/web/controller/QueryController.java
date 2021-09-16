package com.lzq.web.controller;

import com.github.pagehelper.PageInfo;
import com.lzq.api.dto.AccountResult;
import com.lzq.api.dto.ExampleAccount;
import com.lzq.api.pojo.Account;
import com.lzq.api.pojo.Content;
import com.lzq.api.pojo.Example;
import com.lzq.api.service.*;
import com.lzq.web.utils.JWTUtils;
import com.lzq.web.utils.ResultMapUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

/**
 * @author ：LZQ
 * @description：搜索接口
 * @date ：2021/8/25 15:40
 */
@Slf4j
@RestController
@RequestMapping("/query")
@Api(value = "搜索接口", description = "搜索接口")
public class QueryController {

    @Reference
    private AccountResultService accountResultService;

    @Reference
    private ExampleAccountService exampleAccountService;

    @Reference
    private ExampleService exampleService;

    @Reference
    private FavoritesService favoritesService;

    @Reference
    private AccountService accountService;

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
    public Map<String, Object> queryByUsername(HttpServletRequest request,String username) {
        String token = request.getHeader("token");
        log.info("根据用户名查询用户信息接口：" + username);
        //获取用户信息
        AccountResult result = accountResultService.queryByUsername(username);
        //获取喜爱人数
        Integer count = favoritesService.getCount(username);
        if (result != null) {
            //当喜爱数不匹配时进行更新
            if (!result.getFavorites().equals(count)) {
                result.setFavorites(count);
                //更新数据
                Boolean aBoolean = accountResultService.updateFavorites(result);
                log.info("校正用户喜爱数成功:" + aBoolean.toString());
            }
        }
        if (StringUtils.isNotBlank(token)){
            String s = JWTUtils.verify(token).getClaim("username").asString();
            if (!s.equals(username)){
                //获取缓存中的关注用户
                List<String> followList = redisTemplate.opsForList().range(s, 0, -1);
                if(followList.contains(username)){
                    result.setMyFollow(true);
                }
            }
        }
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
    public Map<String, Object> getFollowList(HttpServletRequest request, Account result,
                                             @RequestParam(defaultValue = "1") Integer currentPage) {
        String username = null;
        PageInfo<AccountResult> list = null;
        String token = request.getHeader("token");
        //判断用户是否登录
        if (StringUtils.isNotBlank(token)) {
            username = JWTUtils.verify(token)
                    .getClaim("username").asString();
        }
        //登录的情况
        if (username != null) {
            //查看用户自己的关注
            if (username.equals(result.getUsername())) {
                list = accountResultService.getFollowList(result, currentPage);
                List<AccountResult> results = list.getList();
                for (AccountResult account : results) {
                    account.setMyFollow(true);
                }
                list.setList(results);
            } else {  //用户查看他人的关注
                //获取缓存中的所有关注的用户
                List<String> followList = redisTemplate.opsForList().range(username, 0, -1);
                list = accountResultService.getFollowList(result, currentPage);
                //通过遍历查找用户也关注的用户
                List<AccountResult> results = list.getList();
                for (AccountResult account : results) {
                    //当查询到同样的用户时，修改其状态
                    if (followList.contains(account.getUsername())) {
                        account.setMyFollow(true);
                    } else if (username.equals(account.getUsername())) {
                        //当用户查询到自己时不需要设置任何状态
                        account.setMyFollow(null);
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
    public Map<String, Object> getFanList(HttpServletRequest request, Account result,
                                          @RequestParam(defaultValue = "1") Integer currentPage) {
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
                //获取缓存中的所有关注的用户
                List<String> followList = redisTemplate.opsForList().range(username, 0, -1);
                for (AccountResult account : results) {
                    //当互关时，修改状态
                    if (followList.contains(account.getUsername())) {
                        account.setMyFollow(true);
                    }
                }
                list.setList(results);
            } else {  //用户查看他人的粉丝
                //获取缓存中的所有关注的用户
                List<String> followList = redisTemplate.opsForList().range(username, 0, -1);
                list = accountResultService.getFanList(result, currentPage);
                //通过遍历查找当前用户也关注的用户
                List<AccountResult> results = list.getList();
                for (AccountResult account : results) {
                    //当查询到同样的用户时，修改其状态
                    if (followList.contains(account.getUsername())) {
                        account.setMyFollow(true);
                    } else if (username.equals(account.getUsername())) {
                        //当用户查询到自己时不需要设置任何状态
                        account.setMyFollow(null);
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
     * @param queryContent 实例名获取实例名
     * @param currentPage  当前页
     * @param content      content对象
     * @return
     */
    @GetMapping("/queryExample")
    @ApiOperation("搜索框查询")
    public Map<String, Object> queryByExampleName(HttpServletRequest request, String queryContent, Content content,
                                                  @RequestParam(defaultValue = "1") Integer currentPage,
                                                  @RequestParam(defaultValue = "0") Integer orderCondition) {
        log.info("搜索框查询");
        String username = null;
        if (request.getHeader("token") != null) {
            //获取token中的用户名
            username = JWTUtils.verify(request.getHeader("token"))
                    .getClaim("username").asString();
        }
        //先查询实例用户集合
        PageInfo<ExampleAccount> pageInfo = exampleAccountService.queryExample(queryContent, currentPage, orderCondition, content);
        List<ExampleAccount> list = pageInfo.getList();
        //当用户不登陆时不需要进行任何操作查询数据直接返回
        if (username != null) {
            //获取redis缓存中所关注的用户名列表
            List<String> followlist = redisTemplate.opsForList().range(username, 0, -1);
            //获取redis缓存中所喜欢的实例id列表
            List<Integer> favoriteslist = redisTemplate.opsForList().range(username + "fav", 0, -1);
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
        return ResultMapUtils.ResultMap(true, 0, pageInfo);
    }

    /**
     * 根据用户名查询全部实例
     *
     * @return
     */
    @GetMapping("/getExample")
    @ApiOperation("查询个人全部实例")
    public Map<String, Object> getExample(HttpServletRequest request, Account account,
                                          @RequestParam(defaultValue = "1") Integer currentPage,
                                          @RequestParam(defaultValue = "0") Integer orderCondition) {
        log.info("查询个人实例"+account.getUsername());
        String username = null;
        PageInfo<Example> list;
        if (request.getHeader("token") != null) {
            //获取token中的用户名
            username = JWTUtils.verify(request.getHeader("token"))
                    .getClaim("username").asString();
        }
        //用户登录的状态下
        if (username != null) {
            //用户名相同则查询自己的实例，不同则查询他人的公开实例
            if (username.equals(account.getUsername())) {
                list = exampleService.queryByAccount(account.getUsername(), currentPage, orderCondition);
            } else {
                //获取redis缓存中所喜欢的实例id列表
                List<String> favoriteslist = redisTemplate.opsForList().range(username + "fav", 0, -1);
                list = exampleService.queryByPublic(account.getUsername(), currentPage, orderCondition);
                //获取实例集合
                List<Example> exampleList = list.getList();
                for (Example example : exampleList) {
                    if (favoriteslist.contains(example.getExampleId())) {
                        example.setMyFavorites(true);
                    }
                }
            }
        } else {
            list = exampleService.queryByPublic(account.getUsername(), currentPage, orderCondition);
        }

        return ResultMapUtils.ResultMap(true, 0, list);
    }

    /**
     * 获取喜爱实例列表
     *
     * @param request
     * @param account
     * @param currentPage    当前页数
     * @param orderCondition 排序条件
     * @return
     */
    @GetMapping("/getFavorites")
    @ApiOperation("获取喜爱实例列表")
    public Map<String, Object> getFavorites(HttpServletRequest request, Account account,
                                            @RequestParam(defaultValue = "1") Integer currentPage,
                                            @RequestParam(defaultValue = "0") Integer orderCondition) {
        String username = null;
        PageInfo<ExampleAccount> list;
        if (request.getHeader("token") != null) {
            //获取token中的用户名
            username = JWTUtils.verify(request.getHeader("token"))
                    .getClaim("username").asString();
        }
        //用户登录的状态下
        if (username != null) {
            //判断用户是否查看他人的喜爱实例
            if (username.equals(account.getUsername())) {
                list = exampleAccountService.queryPersonFavorites(account.getUsername(), currentPage, orderCondition);
                //关注列表
                List<String> followList = redisTemplate.opsForList().range(username, 0, -1);
                List<ExampleAccount> exampleList = list.getList();
                for (ExampleAccount exampleAccount : exampleList) {
                    //判断该用户是否被关注
                    if (followList.contains(exampleAccount.getUsername())) {
                        exampleAccount.setMyFollow(true);
                    }
                    exampleAccount.setMyFavorites(true);
                }
                list.setList(exampleList);
            } else {
                //获取缓存中用户的喜爱实例id
                List<String> favoritesList = redisTemplate.opsForList().range(username + "fav", 0, -1);
                log.info("查询到的个人喜爱列表" + favoritesList);
                //获取缓存中用户的关注用户名
                List<String> followList = redisTemplate.opsForList().range(username, 0, -1);
                list = exampleAccountService.queryPersonFavorites(account.getUsername(), currentPage, orderCondition);
                List<ExampleAccount> exampleList = list.getList();
                for (ExampleAccount exampleAccount : exampleList) {
                    log.info("遍历实例id:" + exampleAccount.getExampleId());
                    //判断该用户是否被关注，该用户是否是自己
                    if (followList.contains(exampleAccount.getUsername())) {
                        exampleAccount.setMyFollow(true);
                    } else if (username.equals(exampleAccount.getUsername())) {
                        exampleAccount.setMyFollow(null);
                    }
                    if (favoritesList.contains(exampleAccount.getExampleId())) {
                        log.info("我匹配正确了" + exampleAccount.getExampleId());
                        exampleAccount.setMyFavorites(true);
                    }
                }
                list.setList(exampleList);
            }
        } else {
            list = exampleAccountService.queryPersonFavorites(account.getUsername(), currentPage, orderCondition);
        }
        return ResultMapUtils.ResultMap(true, 0, list);
    }

    /**
     * 获取回收站列表
     *
     * @param request
     * @return
     */
    @GetMapping("/getRecycle")
    @ApiOperation("获取回收站列表")
    public Map<String, Object> getRecycle(HttpServletRequest request) {
        String token = request.getHeader("token");
        if (StringUtils.isNotBlank(token)) {
            //获取登录的用户名
            String username = JWTUtils.verify(token).getClaim("username").asString();
            log.info("获取回收站"+username);
            List<Example> examples = exampleService.queryRecycle(username);
            return ResultMapUtils.ResultMap(true, 0, examples);
        } else {
            return ResultMapUtils.ResultMap(false, 0, null);
        }
    }
}
