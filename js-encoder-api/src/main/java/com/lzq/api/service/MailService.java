package com.lzq.api.service;


import com.lzq.api.pojo.Mail;

public interface MailService {

    /**
     * 发送邮箱
     * @param mail 实例对象
     * @return
     */
    boolean sendActiveMail(Mail mail);
}
