package com.lzq.dubboservice.service;


import com.lzq.api.pojo.Mail;
import com.lzq.api.service.MailService;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

/**
 * @author ：LZQ
 * @description：MailService实现类
 * @date ：2021/8/23 10:48
 */
@Component
@Service(interfaceClass = MailService.class,timeout = 60000,retries = 0)
public class MailServiceImpl implements MailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Override
    public boolean sendActiveMail(Mail mail) {
        MimeMessage message = this.javaMailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(mail.getFrom());
            helper.setTo(mail.getTo());
            helper.setSubject(mail.getSubject());
            helper.setText(mail.getMailContent(), true);
            javaMailSender.send(message);
            System.out.println("发送成功");
            return true;
        } catch (MessagingException var4) {
            System.out.println("发送失败");
            return false;
        }
    }
}
