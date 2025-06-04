package com.enifl33fi.emailsender.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String fromMail;

    public void sendEmail(String emailAddr, String text, String subject) {
        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(emailAddr);
        email.setFrom(fromMail);
        email.setSubject(subject);
        email.setText(text);

        sendMessage(email);
    }

    protected void sendMessage(SimpleMailMessage message) {
        javaMailSender.send(message);
    }
}