/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.project.passwordreset;

import com.example.project.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 *
 * @author Adewole
 */
@Service
public class PasswordMail {
    private JavaMailSender javaMailSender;
    
    User user = new User();
    
    @Autowired
    public PasswordMail(JavaMailSender javaMailSender){
        this.javaMailSender = javaMailSender;
    }
    
    public void sendMail(CheckPin checkPin){
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setTo(checkPin.getEmail());
        mail.setFrom("adeoluwadavid@gmail.com");
        mail.setSubject("Aspiring Mothers");
        mail.setText("Hi, kindly follow this link to reset your password: " +" http://localhost:8088/reset/" + user.getId() );
        
        javaMailSender.send(mail);
    }
}
