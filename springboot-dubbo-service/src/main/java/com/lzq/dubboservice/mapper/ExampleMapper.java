package com.lzq.dubboservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lzq.api.pojo.Example;
import org.apache.ibatis.annotations.Param;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author ：LZQ
 * @description：TODO
 * @date ：2021/8/26 15:07
 */
@Repository
public interface ExampleMapper extends BaseMapper<Example> {

    Integer deleteExample(@Param("exampleId")String exampleId);

    List<Example> queryDeleted(@Param("username") String username);

    Integer resumeExample(@Param("exmapleId") String exampleId);

    Example getExampleByDeleted(Example example);
}
