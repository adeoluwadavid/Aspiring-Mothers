/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.project.controller;

import com.example.project.email.EmailService;
import com.example.project.jwt.Jwt;
import com.example.project.jwt.payload.request.SignupRequest;
import com.example.project.jwt.payload.response.MessageResponse;
import com.example.project.model.ERole;
import com.example.project.model.Role;
import com.example.project.model.User;
import com.example.project.repository.RoleRepository;
import com.example.project.repository.UserRepository;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Adewole
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/authe")
public class FirstController {

    @Autowired
    AuthenticationManager authe;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    Jwt jwt;

    @Autowired
    PasswordEncoder encoder;
    
    @Autowired
    private EmailService emailService;

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signupRequest) {
        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already taken!"));
        }

        // Create new user's account
        User user = new User(
                signupRequest.getFirstName(),
                signupRequest.getLastName(),
                signupRequest.getAddress(),
                signupRequest.getEmail(),
                encoder.encode(signupRequest.getPassword()));

        Set<String> strRoles = signupRequest.getRole();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null) {
            Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(adminRole);
                        break;

                    default:
                        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found"));
                        roles.add(userRole);
                }
            });
        }
        
        
        Map<String,Object> model = new HashMap<>();
        model.put("Name", signupRequest.getFirstName() + signupRequest.getLastName());
        model.put("Location","Ibadan, Nigeria");
        emailService.sendEmail(signupRequest, model);
        System.out.println("Email Sent Successfully");
        
        
        user.setRoles(roles);
        userRepository.save(user);
        return ResponseEntity.ok(new MessageResponse("User registered successfully and Mail Sent Successfully"));
    }
}
