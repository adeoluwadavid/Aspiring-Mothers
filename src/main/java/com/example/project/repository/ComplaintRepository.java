/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.project.repository;

import com.example.project.model.Complaint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Adewole
 */
@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
    
}
