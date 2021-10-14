package com.lzq.api.dto;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author ：LZQ
 * @description：TODO
 * @date ：2021/8/30 14:15
 */
@Data
public class ExampleAccount implements Serializable {

    private static final long serialVersionUID = 2555481963217920717L;
    /**
     * 用户名
     */
    @TableId(value = "username")
    @JsonProperty("username")
    private String username;
    /**
     * 昵称
     */
    @TableField(value = "name")
    @JsonProperty("name")
    private String name;
    /**
     * 头像
     */
    @TableField(value = "user_picture")
    @JsonProperty("userPicture")
    private String userPicture;
    /**
     * 描述
     */
    @TableField("description")
    @JsonProperty("description")
    private String description;
    /**
     * 我的关注
     */
    @TableField(exist = false)
    @JsonProperty("myFollow")
    private Boolean myFollow=false;
    /**
     * 主键
     */
    @TableId(value = "example_id")
    @JsonProperty("exampleId")
    private String exampleId;
    /**
     * 实列名称
     */
    @TableField(value = "example_name")
    @JsonProperty("exampleName")
    private String exampleName;
    /**
     * 标签
     */
    @TableField(value = "label")
    @JsonProperty("label")
    private String label;
    /**
     * 封面图片
     */
    @TableField(value = "img")
    @JsonProperty("img")
    private String img;
    /**
     * 喜爱
     */
    @TableField(value = "favorites")
    @JsonProperty("favorites")
    private Integer favorites;


    @TableField(exist = false)
    @JsonProperty("myFavorites")
    private Boolean myFavorites=false;

    /**
     * 创建时间
     */

    @JsonProperty("createTime")
    @TableField("create_time")
    @JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss",timezone = "GMT+8")
    private Date createTime;
    /**
     * 更新时间
     */
    @JsonProperty("updateTime")
    @TableField("update_time")
    @JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss",timezone = "GMT+8")
    private Date updateTime;
}
