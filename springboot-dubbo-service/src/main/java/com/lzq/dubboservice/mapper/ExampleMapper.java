package com.lzq.dubboservice.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lzq.api.dto.ExampleAccount;
import com.lzq.api.pojo.Account;
import com.lzq.api.pojo.Example;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author ：LZQ
 * @description：TODO
 * @date ：2021/8/26 15:07
 */
@Repository
public interface ExampleMapper extends BaseMapper<Example> {


}
