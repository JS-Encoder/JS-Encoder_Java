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
     *
     * @param content    搜索框内容
     * @param currentPage    当前页
     * @param orderCondition 排序条件
     * @return
     */
    PageInfo<ExampleAccount> queryByExampleName(String content, Integer currentPage, Integer orderCondition);

    /**
     * 查询个人喜爱实例
     *
     * @param username    用户名
     * @param currentPage 当前页
     * @param orderCondition
     * @return
     */
    PageInfo<ExampleAccount> queryPersonFavorites(String username, Integer currentPage,Integer orderCondition);
}
