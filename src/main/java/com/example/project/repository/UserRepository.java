/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.project.repository;

import com.example.project.model.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>{
    Optional<User> findByEmail(String email);
    Boolean existsByEmail(String email);
    
    User findUserByEmail(String email);
    
    @Query("SELECT u from User u where u.email=?1")
    List<User> getUserByEmail(String email);
    
    @Query("SELECT u.password from User u ")
    List getUserByPassword();
    
    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.password=?1 WHERE u.email=?2 ")
    void setPassword(String password,String email);
}