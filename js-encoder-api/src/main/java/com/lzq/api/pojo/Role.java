package com.lzq.api.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author ：LZQ
 * @description：角色类
 * @date ：2021/8/19 15:44
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("role")
public class Role implements Serializable {

    private static final long serialVersionUID = -92833203186967344L;
    /**
     * 主键
     */
    @TableId(value = "id",type = IdType.AUTO)
    @JsonProperty("id")
    private Integer id;
    /**
     * 角色名
     */
    @TableId(value = "name")
    @JsonProperty("name")
    private String name;


}
