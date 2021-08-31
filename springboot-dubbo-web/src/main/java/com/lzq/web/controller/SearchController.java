package com.lzq.web.controller;

import com.lzq.api.dto.AccountResult;
import com.lzq.api.dto.ExampleAccount;
import com.lzq.api.service.AccountResultService;
import com.lzq.api.service.ExampleAccountService;
import com.lzq.api.service.ExampleService;
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

import java.util.Iterator;
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
@Api(value = "搜索接口",description = "搜索接口")
public class SearchController {

    @Reference
    private AccountResultService accountResultService;

    @Reference
    private ExampleAccountService exampleAccountService;

    @Autowired
    private RedisTemplate redisTemplate;


    /**
     * 获取关注列表
     * @param result
     * @return
     */
    @GetMapping("/getFollow")
    @ApiOperation("获取关注列表")
    public Map<String,Object> getFollowList(AccountResult result){
        List<AccountResult> list = accountResultService.getFollowList(result);
        return ResultMapUtils.ResultMap(true,0,list);
    }

    /**
     * 获取粉丝列表
     * @param result
     * @return
     */
    @GetMapping("/getFan")
    @ApiOperation("获取粉丝列表")
    public Map<String,Object> getFanList(AccountResult result){
        List<AccountResult> list = accountResultService.getFanList(result);
        return ResultMapUtils.ResultMap(true,0,list);
    }

    /**
     * 根据实例名查询实例
     * @param exampleName 实例名
     * @param username  查询者的用户名
     * @return
     */
    @GetMapping("/queryByExampleName")
    @ApiOperation("根据实例名查询实例")
    public Map<String,Object> queryByExampleName(String exampleName,String username){
        //先查询实例用户集合
        List<ExampleAccount> list = exampleAccountService.queryByExampleName(exampleName);
        List<String> followlist = redisTemplate.opsForList().range(username, 0, -1);
        List<String> favoriteslist = redisTemplate.opsForList().range(username+"fav", 0, -1);
        //遍历修改数组
        for (ExampleAccount exampleAccount : list) {
            //当该用户被当前用户关注时设置为true,否则为false
            if (followlist.contains(exampleAccount.getUsername())){
                exampleAccount.setMyFollow(true);
            }else {
                exampleAccount.setMyFollow(false);
            }
            //当该实例被当前用户所喜爱时设置为true,否则为false
            if (favoriteslist.contains(exampleAccount.getExampleId())){
                exampleAccount.setMyFavorites(true);
            }else {
                exampleAccount.setMyFavorites(false);
            }
        }
        log.info(list.toString());
        return ResultMapUtils.ResultMap(true,0,list);
    }

}
