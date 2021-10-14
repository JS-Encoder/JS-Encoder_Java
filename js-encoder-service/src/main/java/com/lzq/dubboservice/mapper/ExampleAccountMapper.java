package com.lzq.dubboservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lzq.api.dto.ExampleAccount;
import com.lzq.api.pojo.Content;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author ：LZQ
 * @description：实例用户信息
 * @date ：2021/8/30 14:21
 */
@Repository
public interface ExampleAccountMapper extends BaseMapper<ExampleAccount> {

    List<ExampleAccount> queryExample(@Param("queryContent") String queryContent, @Param("orderCondition") Integer orderCondition,@Param("content") Content content);

    List<ExampleAccount> queryPersonFavorites(@Param("username") String username,@Param("orderCondition") Integer orderCondition);
}
