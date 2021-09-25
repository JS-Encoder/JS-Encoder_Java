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
    Example insert(Example example);

    /**
     * 修改数据
     *
     * @param example 实例对象
     * @return 实例对象
     */
    Boolean update(Example example);

    /**
     * 通过主键逻辑删除数据（放入回收站）
     *
     * @param exampleId 主键
     * @return 是否成功
     */
    boolean deleteById(String exampleId);


    /**
     * 通过用户名查询实例
     *
     * @param username    用户名
     * @param currentPage 当前页
     * @return
     */
    PageInfo<Example> queryByAccount(String username, Integer currentPage, Integer orderCondition, Integer ispublic);



    /**
     * 删除实例（物理删除实例）
     *
     * @param exampleId 实例id
     * @return
     */
    Boolean deleteExample(String exampleId);

    /**
     * 通过实例id查询实例信息
     *
     * @param exampleId
     * @return
     */
    Example queryById(String exampleId);

    /**
     * 通过实例id和用户名查询实例
     *
     * @param example
     * @return
     */
    Example queryByIdUsername(Example example);


    /**
     * 查询用户的回收站
     *
     * @param username
     * @return
     */
    List<Example> queryRecycle(String username);

    /**
     * 恢复回收站实例
     *
     * @param exampleId
     * @return
     */
    Boolean resumeExample(String exampleId);

    /**
     * 查询回收站是否有该实例
     *
     * @param example
     * @return
     */
    Example getExampleByDeleted(Example example);

}
