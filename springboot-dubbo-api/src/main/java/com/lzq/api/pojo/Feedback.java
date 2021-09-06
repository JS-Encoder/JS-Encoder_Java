package com.lzq.api.pojo;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author ：LZQ
 * @description：TODO
 * @date ：2021/9/6 13:59
 */
@Data
@TableName("feedback")
public class Feedback implements Serializable {

    private static final long serialVersionUID = 4213365005220161295L;
    /**
     * 主键
     */
    @TableId(value = "id",type = IdType.AUTO)
    @JsonIgnore
    private Integer id;

    /**
     * 标题
     */
    @TableField("title")
    @JsonProperty("title")
    private String title;

    /**
     * 反馈内容
     */
    @TableField("content")
    @JsonProperty("content")
    private String content;

    /**
     * 创建时间
     */
    @JsonIgnore
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
    /**
     * 更新时间
     */
    @JsonIgnore
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
}
