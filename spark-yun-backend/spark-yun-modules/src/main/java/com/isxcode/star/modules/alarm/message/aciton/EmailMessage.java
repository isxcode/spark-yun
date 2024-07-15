package com.isxcode.star.modules.alarm.message.aciton;

import com.isxcode.star.api.alarm.constants.MessageType;
import com.isxcode.star.modules.alarm.message.MessageContext;
import com.isxcode.star.modules.alarm.message.MessageRunner;
import com.isxcode.star.modules.alarm.repository.AlarmInstanceRepository;
import org.apache.logging.log4j.util.Strings;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

import java.util.Properties;

@Service
public class EmailMessage extends MessageRunner {

    protected EmailMessage(AlarmInstanceRepository alarmInstanceRepository) {
        super(alarmInstanceRepository);
    }

    @Override
    public String getActionName() {
        return MessageType.EMAIL;
    }

    @Override
    public Object sendMessage(MessageContext messageContext) {

        if (Strings.isEmpty(messageContext.getEmail())) {
            throw new RuntimeException("用户邮箱为空");
        }

        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(messageContext.getMessageConfig().getHost());
        mailSender.setPort(messageContext.getMessageConfig().getPort());
        mailSender.setUsername(messageContext.getMessageConfig().getUsername());
        mailSender.setPassword(messageContext.getMessageConfig().getPassword());
        mailSender.setProtocol(JavaMailSenderImpl.DEFAULT_PROTOCOL);
        mailSender.setDefaultEncoding("UTF-8");

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        mailSender.setJavaMailProperties(props);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(messageContext.getMessageConfig().getUsername());
        message.setTo(messageContext.getEmail());
        message.setSubject(messageContext.getMessageConfig().getSubject());
        message.setText(messageContext.getContent());
        mailSender.send(message);

        return "邮件发送成功";
    }
}
