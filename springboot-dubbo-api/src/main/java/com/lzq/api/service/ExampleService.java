package com.lzq.api.service;


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
     * 通过ID查询单条数据
     *
     * @param exampleId 主键
     * @return 实例对象
     */
    Example queryById(Integer exampleId);

    /**
     * 查询多条数据
     *
     * @param offset 查询起始位置
     * @param limit  查询条数
     * @return 对象列表
     */
    List<Example> queryAllByLimit(int offset, int limit);

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
     * @param username
     * @return
     */
    List<Example> queryByAccount(String username);


    /**
     * 查询用户公开的实例
     * @param username
     * @return
     */
    List<Example> queryByPublic(String username);

}
