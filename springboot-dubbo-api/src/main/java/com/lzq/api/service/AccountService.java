package com.lzq.api.service;


import com.github.pagehelper.PageInfo;
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
     * @return
     */
    Boolean update(Account account);


    /**
     * 绑定第三方id
     */
    Boolean bindGit(Account account);

    /**
     * 增加作品数
     * @param username
     * @return
     */
    Boolean addWorks(String username);

    /**
     * 减少作品数
     * @param username
     * @return
     */
    Boolean reduceWorks(String username);

    /**
     * 添加喜爱数
     * @param username
     * @return
     */
    Boolean addFavorites(String username);

    /**
     * 更新喜爱数（校正喜爱数）
     * @param account
     * @return
     */
    Boolean updateFavorites(Account account);

    /**
     * 根据第三方id查询用户
     * @param githubId
     * @param giteeId
     * @return
     */
    Account queryByGitId(String githubId, String giteeId);

    /**
     * 获取关注列表
     * @param result
     * @param currentPage 当前页
     * @return
     */
    PageInfo<Account> getFollowList(Account result, Integer currentPage);

    /**
     * 获取粉丝列表
     * @param result
     * @return
     */
    PageInfo<Account> getFanList(Account result,Integer currentPage);

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

    /**
     * 增加回收站作品数
     * @param username
     * @return
     */
    Boolean increaseRecycle(String username);

    /**
     * 删除回收站作品
     * @param username
     * @return
     */
    Boolean reduceRecycle(String username);


}
