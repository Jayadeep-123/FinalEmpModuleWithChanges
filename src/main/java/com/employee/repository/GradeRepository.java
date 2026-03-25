package com.employee.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.employee.entity.Grade;

@Repository
public interface GradeRepository extends JpaRepository<Grade, Integer> {

	  List<Grade> findByIsActive(int isActive);
	  
}