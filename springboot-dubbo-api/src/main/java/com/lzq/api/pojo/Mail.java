package com.lzq.api.pojo;

import lombok.Data;
;

import java.io.Serializable;

/**
 * @author ：LZQ
 * @description：邮箱
 * @date ：2021/8/19 15:44
 */
@Data
public class Mail implements Serializable{

    private static final long serialVersionUID = -9157975066766675204L;
    /**
     *发送方
     */
    private final String from = "jsencoder@163.com";
    /**
     * 接收方
     */
    private String to;
    /**
     * 主题
     */
    private String subject;
    /**
     * 内容
     */
    private String mailContent;
}
