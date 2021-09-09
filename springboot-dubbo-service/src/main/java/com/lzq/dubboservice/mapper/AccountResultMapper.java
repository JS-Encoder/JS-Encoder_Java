package com.lzq.dubboservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lzq.api.dto.AccountResult;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author ：LZQ
 * @description：TODO
 * @date ：2021/8/23 14:49
 */
@Repository
public interface AccountResultMapper extends BaseMapper<AccountResult> {

    Integer updateFavorites(AccountResult result);
}
