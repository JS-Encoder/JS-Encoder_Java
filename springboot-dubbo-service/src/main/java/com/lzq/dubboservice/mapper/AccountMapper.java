package com.lzq.dubboservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lzq.api.pojo.Account;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * @author ：LZQ
 * @description：TODO
 * @date ：2021/8/23 14:49
 */
@Repository
public interface AccountMapper extends BaseMapper<Account> {

    Integer addFavorites(@Param("username") String username);

    Integer updateFavorites(Account account);

    Integer addWorks(@Param("username") String username);

    Integer reduceWorks(@Param("username") String username);

    Integer increaseRecycle(@Param("username") String username);

    Integer reduceRecycle(@Param("username") String username);

    Integer bindGit(Account account);
}
