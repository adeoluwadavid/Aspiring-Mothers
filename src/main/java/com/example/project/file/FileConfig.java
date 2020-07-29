/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.project.file;

import com.cloudinary.Cloudinary;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 *
 * @author Adewole
 */
@Component
public class FileConfig {
     
    @Value("${cloudinary.cloud_name}")
    private String cloudName;
    
    @Value("${cloudinary.api_secret}")
    private String apiSecret;
    
    @Value("${cloudinary.api_key}")
    private String apiKey;
    
    @Bean
    public Cloudinary cloudinaryconfig(){
        Cloudinary cloudinary = null;
        Map config = new HashMap();
        config.put("cloud_name", cloudName);
        config.put("api_key", apiKey);
        config.put("api_secret", apiSecret);
        
        cloudinary = new Cloudinary(config);
        return cloudinary;
        
    }
}
