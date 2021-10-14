package com.lzq.api.service;


import com.lzq.api.pojo.Role;

/**
 * @author ：LZQ
 * @description：(Role)表服务接口
 * @since 2021-03-24 11:20
 */
public interface RoleService {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    Role queryById(Integer id);



}
