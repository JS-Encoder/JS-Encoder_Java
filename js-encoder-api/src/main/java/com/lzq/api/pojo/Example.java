package com.lzq.api.pojo;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * @author ：LZQ
 * @description：文档类
 * @date ：2021/8/19 15:44
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("example")
public class Example implements Serializable {

    private static final long serialVersionUID = -3914576538850966784L;
    /**
     * 主键
     */
    @TableId(value = "example_id",type =IdType.ID_WORKER_STR )
    @JsonProperty("exampleId")
    private String exampleId;
    /**
     * 实列名称
     */
    @TableField(value = "example_name")
    @JsonProperty("exampleName")
    private String exampleName;
    /**
     * 是否公开
     */
    @TableField(value = "ispublic")
    @JsonProperty("ispublic")
    private Boolean ispublic;
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
     * 编译后的文件名
     */
    @TableField(value = "file_name")
    @JsonProperty("fileName")
    private String fileName;
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
     * 用户名
     */
    @TableField(value = "username")
    @JsonProperty("username")
    private String username;
    /**
     * 创建时间
     */
    @JsonProperty("createTime")
    @TableField(value ="create_time",fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss",timezone = "GMT+8")
    private Date createTime;
    /**
     * 更新时间
     */
    @JsonProperty("updateTime")
    @TableField(value = "update_time",fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd hh:mm:ss",timezone = "GMT+8")
    private Date updateTime;
    /**
     * 乐观锁
     */
    @JsonIgnore
    @Version
    private Integer version;
    /**
     * 是否删除
     */
    @JsonIgnore
    @TableLogic
    private Integer deleted;



}
