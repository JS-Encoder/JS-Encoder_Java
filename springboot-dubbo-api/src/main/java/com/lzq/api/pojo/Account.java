package com.lzq.api.pojo;


import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * @author ：LZQ
 * @description：用户类
 * @date ：2021/8/19 15:44
 */
@Data
@TableName("account")
public class Account implements Serializable, UserDetails {

    private static final long serialVersionUID = -39692943284343525L;

    /**
     * 用户名
     */
    @TableField(value = "username")
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
    @JsonIgnore
    private String email;
    /**
     * 联系邮箱
     */
    @TableField(value = "contact_email")
    @JsonIgnore
    private String contactEmail;
    /**
     * 密码
     */
    @JsonIgnore
    private String password;
    /**
     * 头像
     */
    @TableField(value = "user_picture")
    @JsonProperty("userPicture")
    private String userPicture;
    /**
     * 背景颜色
     */
    @TableField(value = "backgroud_color")
    @JsonIgnore
    private String backgroudColor;
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
     * 角色类
     */
    @JsonIgnore
    @TableField(exist = false)
    private Role role;
    /**
     * 描述
     */
    @TableField("description")
    @JsonIgnore
    private String description;
    /**
     * 作品数
     */
    @TableField("works")
    @JsonIgnore
    private Integer works;
    /**
     * 喜爱人数
     */
    @TableField("favorites")
    @JsonIgnore
    private Integer favorites;
    /**
     * 粉丝
     */
    @TableField("fan")
    @JsonIgnore
    private Integer fan;
    /**
     * 关注
     */
    @TableField("following")
    @JsonIgnore
    private Integer following;
    /**
     * 回收站
     */
    @TableField("recycle")
    @JsonIgnore
    private Integer recycle;
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
    @Version //乐观锁注解
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



    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> list = new ArrayList();
        list.add(new SimpleGrantedAuthority(role.getName()));
        return list;
    }



    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
