package com.lzq.api.pojo;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author ：LZQ
 * @description：关注实体类
 * @date ：2021/8/25 9:45
 */
@Data
@TableName("follow")
public class Follow implements Serializable {

    private static final long serialVersionUID = -1821624642648196244L;
    /**
     * 用户名
     */
    @TableId(value = "username")
    @JsonProperty("username")
    private String username;
    /**
     * 关注的用户冥
     */
    @TableId(value = "follow_username")
    @JsonProperty("followUsername")
    private String followUsername;
    /**
     * 创建时间
     */
    @JsonIgnore
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
}
