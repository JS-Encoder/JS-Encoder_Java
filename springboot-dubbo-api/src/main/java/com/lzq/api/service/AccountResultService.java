package com.lzq.api.service;


import com.lzq.api.dto.AccountResult;

import java.util.List;

/**
 * @author ：LZQ
 * @description：(Account)表服务接口
 * @since 2021-03-24 11:20
 */
public interface AccountResultService {

    /**
     * 查询用户
     * @param result
     * @return
     */
    public List<AccountResult> searchUserByName(AccountResult result);

    /**
     * 获取关注列表
     */
    List<AccountResult> getFollowList(AccountResult result);

    /**
     * 获取粉丝列表
     * @param result
     * @return
     */
    List<AccountResult> getFanList(AccountResult result);


}
