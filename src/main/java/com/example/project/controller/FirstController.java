package com.example.project.controller;

import com.example.project.email.EmailService;
import com.example.project.fileservice.CloudinaryService;
import com.example.project.jwt.Jwt;
import com.example.project.jwt.payload.request.LoginRequest;
import com.example.project.jwt.payload.request.SignupRequest;
import com.example.project.jwt.payload.response.JwtResponse;
import com.example.project.jwt.payload.response.MessageResponse;
import com.example.project.model.Complaint;
import com.example.project.model.ERole;
import com.example.project.model.MyFile;
import com.example.project.model.Role;
import com.example.project.model.User;
import com.example.project.password.ResetPassword;
import com.example.project.repository.ComplaintRepository;
import com.example.project.repository.MyFileRepository;
import com.example.project.repository.RoleRepository;
import com.example.project.repository.UserRepository;
import com.example.project.service.UserDetailsImpl;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import java.util.Map;
import java.util.Optional;

import java.util.Set;

import java.util.stream.Collectors;

import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;

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
import org.springframework.web.multipart.MultipartFile;

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
    ComplaintRepository complaintRepository;
    
    @Autowired
    MyFileRepository myFileRepository;

    @Autowired
    Jwt jwtUtil;

    @Autowired
    PasswordEncoder encoder;
    
    @Autowired
    private EmailService emailService;
    
    @Autowired
    private CloudinaryService cloudinaryService;  

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
        model.put("Name", "David" +" " + "Adewole");
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

     @PutMapping("/upload/{id}")
    public String myFile(@PathVariable(value="id") Long id, @RequestParam("file") MultipartFile file) {
        String url = cloudinaryService.uploadFile(file);
      
        MyFile ft = new MyFile();
        ft.setImageUrl(url);
        ft.setId(id);
        cloudinaryService.saveResponse(ft); 
        
        return "File uploaded successfully: File path :  " + url;
    }
    
    @GetMapping("/download/{id}")
    public Optional<MyFile> downloadFile(@PathVariable(value="id")Long id){
      //  MyFile ft = new MyFile();
        Boolean imageId = myFileRepository.existsById(id);
        if(imageId == true){
            return myFileRepository.findById(id);
        }
        return myFileRepository.findById(id);
    }
    
    @PostMapping("/complain")
    public String complain(@Valid @RequestBody Complaint complaint){
        complaintRepository.save(complaint);
        return "User complain saved successfully";
    }
    //Reset Password
    @PostMapping("/resetPassword")
    public String changePassword(@Valid @RequestBody ResetPassword resetPassword ){
        if(userRepository.existsByEmail(resetPassword.getEmail())){
      
         User u = new User();
        u.setPassword("666777");
            return "Password has been set";
        }else{
            return "Email not Found";
        }
    }
}   