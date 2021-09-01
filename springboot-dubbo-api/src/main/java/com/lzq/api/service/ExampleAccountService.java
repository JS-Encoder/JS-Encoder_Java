package com.lzq.api.service;

import com.github.pagehelper.PageInfo;
import com.lzq.api.dto.ExampleAccount;


/**
 * @author ：LZQ
 * @description：实例用户信息
 * @date ：2021/8/30 14:21
 */
public interface ExampleAccountService {


    /**
     * 通过实例名查询实例
     * @param exampleName 实例名
     * @param currentPage 当前页
     * @return
     */
    PageInfo<ExampleAccount> queryByExampleName(String exampleName, Integer currentPage);

    /**
     * 查询个人喜爱实例
     * @param username 用户名
     * @param currentPage 当前页
     * @return
     */
    PageInfo<ExampleAccount> queryPersonFavorites(String username, Integer currentPage);
}
