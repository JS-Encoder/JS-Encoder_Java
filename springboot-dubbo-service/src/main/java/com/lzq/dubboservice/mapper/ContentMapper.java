package com.lzq.dubboservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lzq.api.pojo.Content;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * @author ：LZQ
 * @description：TODO
 * @date ：2021/8/27 14:09
 */
@Repository
public interface ContentMapper extends BaseMapper<Content> {
    void deleteContent(@Param("exampleId") String exampleId);
}
