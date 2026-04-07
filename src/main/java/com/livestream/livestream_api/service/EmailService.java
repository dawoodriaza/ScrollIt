package com.livestream.livestream_api.service;


import lombok.RequiredArgsConstructor;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendVerificationEmail(String toEmail, String token) {
        String body = "Hello!\n\n"
                + "Verify your email using Postman:\n"
                + "GET http://localhost:8080/api/auth/verify-email?token=" + token
                + "\n\nThis token expires in 24 hours.";
        send(toEmail, "Verify your LiveStream Email", body);
    }

    public void sendPasswordResetEmail(String toEmail, String token) {
        String body = "Hello!\n\n"
                + "Reset your password using Postman:\n"
                + "POST http://localhost:8080/api/auth/reset-password\n"
                + "Body: { \"token\": \"" + token + "\", \"newPassword\": \"yournewpassword\" }\n\n"
                + "This token expires in 1 hour.";
        send(toEmail, "Reset your LiveStream Password", body);
    }

    private void send(String to, String subject, String body) {
        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setTo(to);
            msg.setSubject(subject);
            msg.setText(body);
            mailSender.send(msg);

        } catch (Exception e) {

        }
    }
}