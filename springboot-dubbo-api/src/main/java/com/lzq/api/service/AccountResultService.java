package com.lzq.api.service;


import com.github.pagehelper.PageInfo;
import com.lzq.api.dto.AccountResult;
import com.lzq.api.pojo.Account;

/**
 * @author ：LZQ
 * @description：(Account)表服务接口
 * @since 2021-03-24 11:20
 */
public interface AccountResultService {


    /**
     * 根据用户名查询用户
     * @param username
     * @return
     */
    AccountResult queryByUsername(String username);

    /**
     * 更新喜爱数（校正喜爱数）
     * @param result
     * @return
     */
    Boolean updateFavorites(AccountResult result);

    /**
     * 获取关注列表
     * @param result
     * @param currentPage 当前页
     * @return
     */
    PageInfo<AccountResult> getFollowList(Account result, Integer currentPage);

    /**
     * 获取粉丝列表
     * @param result
     * @return
     */
    PageInfo<AccountResult> getFanList(Account result,Integer currentPage);

}
