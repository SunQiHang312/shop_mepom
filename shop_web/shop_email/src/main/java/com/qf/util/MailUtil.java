package com.qf.util;

import com.qf.entity.Email;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.Date;

@Component
public class MailUtil {

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String from;
    /*
    * 发送邮件
    * */
    public void sendMail(Email email){
        //创建邮件
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        //创建一个邮件的包装对象
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);


        try {
            //设置发送方法
            mimeMessageHelper.setFrom(from,"xxx官方");

            //设置接收方
            mimeMessageHelper.setTo(email.getTo());//普通对象

            //设置标题
            mimeMessageHelper.setSubject(email.getSubject());
            //设置内容
            mimeMessageHelper.setText(email.getContent(),true);
            mimeMessageHelper.setSentDate(new Date());

        } catch (Exception e) {
            e.printStackTrace();
        }

        //发送邮件
        javaMailSender.send(mimeMessage);
    }
}
