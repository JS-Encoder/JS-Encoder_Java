package com.lzq.api.pojo;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * @author ：LZQ
 * @description：TODO
 * @date ：2021/8/27 9:56
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("content")
public class Content implements Serializable {

    private static final long serialVersionUID = -7724715365676117052L;
    /**
     * 用例主键
     */
    @TableId(value = "example_id")
    @JsonProperty("exampleId")
    private String exampleId;
    /**
     * 实列名称
     */
    @TableField(exist = false)
    @JsonProperty("exampleName")
    private String exampleName;
    /**
     * 昵称
     */
    @TableField(exist = false)
    @JsonProperty("name")
    private String name;
    /**
     * 标签
     */
    @TableField(exist = false)
    @JsonProperty("label")
    private String label;
    /**
     * codeContent
     */
    @TableField(value = "code_content")
    @JsonProperty("codeContent")
    private String codeContent;
    /**
     * html预处理语言
     */
    @TableField(value = "html_style")
    @JsonProperty("htmlStyle")
    private String htmlStyle;
    /**
     * css预处理语言
     */
    @TableField(value = "css_style")
    @JsonProperty("cssStyle")
    private String cssStyle;
    /**
     * js预处理语言
     */
    @TableField(value = "js_style")
    @JsonProperty("jsStyle")
    private String jsStyle;
    /**
     * 是否喜爱
     */
    @TableField(exist = false)
    @JsonProperty("myFavorites")
    private Boolean myFavorites=false;
    /**
     * 是否公开
     */
    @TableField(exist = false)
    @JsonProperty("ispublic")
    private Boolean ispublic;
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
