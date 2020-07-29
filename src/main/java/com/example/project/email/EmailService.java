/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.project.email;

import com.example.project.jwt.payload.request.SignupRequest;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

/**
 *
 * @author Adewole
 */
@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;
    
    @Autowired
    private Configuration configuration;
    
    public void sendEmail(SignupRequest sr, Map<String,Object> model){
        MimeMessage message = mailSender.createMimeMessage();
        try{
            MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());
            
            Template t = configuration.getTemplate("mail-template.ftl");
            String html = FreeMarkerTemplateUtils.processTemplateIntoString(t,model);
            
            helper.setTo(sr.getEmail());
            helper.setText(html, true);
            helper.setSubject("Aspiring Mothers");
            helper.setFrom("adeoluwadavid@gmail.com");
            mailSender.send(message);
            
        }catch(MessagingException | IOException | TemplateException e){
             System.out.println("The error is: " + e.getMessage()); 
        }
    }
}
