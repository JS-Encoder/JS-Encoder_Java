package com.lzq.web.controller;

import com.lzq.api.dto.AccountResult;
import com.lzq.api.service.AccountResultService;
import com.lzq.web.utils.ResultMapUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @author ：LZQ
 * @description：搜索接口
 * @date ：2021/8/25 15:40
 */
@RestController
@RequestMapping("/search")
@Api(value = "搜索接口",description = "搜索接口")
public class SearchController {

    @Reference
    private AccountResultService accountResultService;

    /**
     * 根据昵称查询用户
     * @param result
     * @return
     */
    @GetMapping("/searchUser")
    @ApiOperation("根据昵称查询用户")
    public Map<String,Object> searchUserByName(AccountResult result){
        List<AccountResult> list = accountResultService.searchUserByName(result);
        return ResultMapUtils.ResultMap(true,0,list);
    }

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
}
