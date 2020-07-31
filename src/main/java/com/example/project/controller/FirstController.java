/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.project.controller;

import com.example.project.email.EmailService;
import com.example.project.jwt.Jwt;
import com.example.project.jwt.payload.request.LoginRequest;
import com.example.project.jwt.payload.request.SignupRequest;
import com.example.project.jwt.payload.response.JwtResponse;
import com.example.project.jwt.payload.response.MessageResponse;
import com.example.project.model.Complaint;
import com.example.project.model.ERole;
import com.example.project.model.Role;
import com.example.project.model.User;
import com.example.project.passwordreset.CheckPin;
import com.example.project.passwordreset.PasswordMail;
import com.example.project.repository.ComplaintRepository;
import com.example.project.repository.RoleRepository;
import com.example.project.repository.UserRepository;
import com.example.project.service.UserDetailsImpl;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Adewole
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/authe")
public class FirstController {

    private Logger logger = LoggerFactory.getLogger(FirstController.class);
    
    @Autowired
    AuthenticationManager authe;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;
    
    @Autowired
    ComplaintRepository complaintRepository;

    @Autowired
    Jwt jwtUtil;

    @Autowired
    PasswordEncoder encoder;
    
    @Autowired
    private EmailService emailService;
    
//    @Autowired
//    private PasswordMail passwordMail;

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signupRequest) {
        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already taken!"));
        }

        // Create new user's account
        User user = new User(
                signupRequest.getFirstName(),
                signupRequest.getLastName(),
                signupRequest.getEmail(),
                encoder.encode(signupRequest.getPassword()),
                signupRequest.getAddress());
                
              

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
        model.put("Name", signupRequest.getFirstName() +" " + signupRequest.getLastName());
        model.put("Location","Ibadan, Nigeria");
        emailService.sendEmail(signupRequest, model);
        System.out.println("Email Sent Successfully");
        
        
        user.setRoles(roles);
        userRepository.save(user);
        return ResponseEntity.ok(new MessageResponse("User registered successfully and Mail Sent Successfully"));
    }
    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authen = authe.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authen);
        String jwt = jwtUtil.generateJwtToken(authen);

        UserDetailsImpl userDetails = (UserDetailsImpl) authen.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        return ResponseEntity.ok(new JwtResponse(jwt,
                userDetails.getId(),
                userDetails.getEmail(),
                userDetails.getFirstName(),
                userDetails.getLastName(),
                roles));       
    }
//    @PostMapping("/checkpin")
//    public void checkpin(@Valid @RequestBody CheckPin checkPin) {
//        if (userRepository.existsByEmail(checkPin.getEmail())) {
//            try {
//                passwordMail.sendMail(checkPin);
//            } catch (MailException e) {
//                logger.info("Error sending mail: " + e.getMessage());
//            }
//
//        }else{
//            System.out.println("Enter a valid email");
//        }
//    }
//   @PutMapping("/reset")
//   public String reset(@RequestParam Long user.getId(), @Valid @RequestBody User user){
//       userRepository.findById(user.getId()).
//   }
    @PostMapping("/complain")
    public String complain(@Valid @RequestBody Complaint complaint){
        complaintRepository.save(complaint);
        return "User complain saved successfully";
    }
}
