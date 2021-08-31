package com.lzq.api.dto;


import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;


import java.io.Serializable;

import java.util.Date;


/**
 * @author ：LZQ
 * @description：用户类
 * @date ：2021/8/19 15:44
 */
@Data
@TableName("account")
public class AccountResult implements Serializable{

    private static final long serialVersionUID = -39692943284343525L;
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
     * 邮箱
     */
    @TableField(value = "email")
    @JsonProperty("email")
    private String email;
    /**
     * 联系邮箱
     */
    @TableField(value = "contact_email")
    @JsonProperty("contactEmail")
    private String contactEmail;
    /**
     * 密码
     */
    @TableField(value = "password")
    @JsonIgnore
    private String password;
    /**
     * 头像
     */
    @TableField(value = "user_picture")
    @JsonProperty("userPicture")
    private String userPicture;
    /**
     * github主键id
     */
    @TableField(value = "github_id")
    @JsonIgnore
    private String githubId;
    /**
     * gitee主键id
     */
    @TableField(value = "gitee_id")
    @JsonIgnore
    private String giteeId;
    /**
     * 角色id
     */
    @TableField(value = "role_id")
    @JsonIgnore
    private int roleId=1;
    /**
     * 描述
     */
    @TableField("description")
    @JsonProperty("description")
    private String description;
    /**
     * 粉丝
     */
    @TableField("fan")
    @JsonProperty("fan")
    private Integer fan;
    /**
     * 关注
     */
    @TableField("follow")
    @JsonProperty("follow")
    private Integer follow;
    @TableField(exist = false)
    /**
     * 我的关注
     */
    @JsonProperty("myFollow")
    private Boolean myFollow=false;
    /**
     * 创建时间
     */
    @JsonIgnore
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
    /**
     * 乐观锁
     */
    @JsonIgnore
    @Version
    private Integer version;
    /**
     * 更新时间
     */
    @JsonIgnore
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
    /**
     * 是否删除
     */
    @JsonIgnore
    @TableLogic
    private Integer deleted;

}
