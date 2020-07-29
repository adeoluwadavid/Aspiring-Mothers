/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.project.jwt;


import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 *
 * @author Adewole
 */
public class TokenFilter extends OncePerRequestFilter {
    
    @Autowired
    private Jwt jwtUtil;
    
    @Autowired
    private UserDetailsService userDetailsService;
    
    private static final Logger logger = LoggerFactory.getLogger(TokenFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain fc) throws ServletException, IOException {
        

        try {
            String jwt = parseJwt(request);
            if (jwt != null && jwtUtil.validateJwtToken(jwt)){
            String email = jwtUtil.getEmailFromJwtToken(jwt);
                UserDetails uD = userDetailsService.loadUserByUsername(email);
                 UsernamePasswordAuthenticationToken authe = new UsernamePasswordAuthenticationToken(
                    uD, null, uD.getAuthorities());
                authe.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authe);
            }
        }catch(Exception e){
            logger.error("Cannot set user authentication: {}", e);
        }
        fc.doFilter(request, response);
    }
    
    private String parseJwt(HttpServletRequest request){
        String headerAuth = request.getHeader("Authorization");
        if(StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")){
          return headerAuth.substring(7, headerAuth.length());  

        }
            return null;
    }
    
}
