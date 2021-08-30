package com.lzq.api.service;


import com.lzq.api.pojo.Account;

/**
 * @author ：LZQ
 * @description：(Account)表服务接口
 * @since 2021-03-24 11:20
 */
public interface AccountService {

    /**
     * 通过邮箱查询用户
     * @param email
     * @return
     */
    Account queryByEmail(String email);


    /**
     * 通过实体作为筛选条件查询
     *
     * @param account 实例对象
     * @return 对象列表
     */
    Account queryAccount(Account account);

    /**
     * 新增数据
     *
     * @param account 实例对象
     * @return 影响行数
     */
    void insert(Account account);

    /**
     * 修改数据
     *
     * @param account 实例对象
     */
    void update(Account account) throws Exception;

    /**
     * 根据第三方id查询用户
     * @param githubId
     * @param giteeId
     * @return
     */
    Account queryByGitId(String githubId, String giteeId);


    /**
     * 根据用户名查询用户信息
     * @param username
     * @return
     */
    Account queryByUsername(String username);


    /**
     * 删除用户
     * @param account
     */
    void deleteAccount(Account account);





}
