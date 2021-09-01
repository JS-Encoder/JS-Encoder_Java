package com.lzq.api.service;


import com.github.pagehelper.PageInfo;
import com.lzq.api.pojo.Example;

import java.util.List;

/**
 * (Example)表服务接口
 *
 * @author makejava
 * @since 2021-05-24 18:15:39
 */
public interface ExampleService {


    /**
     * 新增数据
     *
     * @param example 实例对象
     * @return 实例对象
     */
    Boolean insert(Example example);

    /**
     * 修改数据
     *
     * @param example 实例对象
     * @return 实例对象
     */
    Boolean update(Example example);

    /**
     * 通过主键删除数据
     *
     * @param exampleId 主键
     * @return 是否成功
     */
    boolean deleteById(Integer exampleId);



    /**
     * 通过用户名查询实例
     * @param username 用户名
     * @param currentPage 当前页
     * @return
     */
    PageInfo<Example> queryByAccount(String username, Integer currentPage);


    /**
     * 查询用户公开的实例
     * @param username 用户名
     * @param currentPage 当前页
     * @return
     */
    PageInfo<Example> queryByPublic(String username,Integer currentPage);



}
