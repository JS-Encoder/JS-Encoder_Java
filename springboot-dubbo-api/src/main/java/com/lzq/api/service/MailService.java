package com.lzq.api.service;


import com.lzq.api.pojo.Mail;
import com.sun.istack.internal.NotNull;

public interface MailService {

    /**
     * 发送邮箱
     * @param mail 实例对象
     * @return
     */
    boolean sendActiveMail(@NotNull Mail mail);
}
