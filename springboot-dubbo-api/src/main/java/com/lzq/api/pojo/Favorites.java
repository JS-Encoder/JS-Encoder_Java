package com.lzq.api.pojo;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author ：LZQ
 * @description：喜爱实体类
 * @date ：2021/8/29 10:50
 */
@Data
@TableName("favorites")
public class Favorites {

    /**
     * 用户名
     */
    @TableField("username")
    @JsonProperty("username")
    private String username;
    /**
     * 实例id
     */
    @TableField("example_id")
    @JsonProperty("exampleId")
    private Integer exampleId;
    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    @JsonIgnore
    private Date createTime;
}
