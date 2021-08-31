package com.lzq.api.service;

import com.lzq.api.dto.ExampleAccount;

import java.util.List;

/**
 * @author ：LZQ
 * @description：实例用户信息
 * @date ：2021/8/30 14:21
 */
public interface ExampleAccountService {

    public List<ExampleAccount> queryByExampleName(String exampleName);
}
