package com.lzq.api.service;

import com.github.pagehelper.PageInfo;
import com.lzq.api.dto.ExampleAccount;


/**
 * @author ：LZQ
 * @description：实例用户信息
 * @date ：2021/8/30 14:21
 */
public interface ExampleAccountService {

    PageInfo<ExampleAccount> queryByExampleName(String exampleName, Integer currentPage);
}
