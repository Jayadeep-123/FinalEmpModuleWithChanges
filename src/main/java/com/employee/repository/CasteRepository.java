package com.employee.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.employee.entity.Caste;



@Repository
public interface CasteRepository extends JpaRepository<Caste, Integer>{

}
