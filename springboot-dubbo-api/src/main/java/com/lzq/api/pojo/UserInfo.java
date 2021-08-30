package com.lzq.api.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/**
 *
 * @author ：LZQ
 * @description：第三方类
 * @date ：2021/8/19 15:44
 */
@Data
public class UserInfo implements Serializable {

    private static final long serialVersionUID = 8560308483184278887L;

    //用户id
    private Integer id;
}
