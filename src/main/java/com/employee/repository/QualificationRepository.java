package com.employee.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.employee.entity.Qualification;

public interface QualificationRepository extends JpaRepository<Qualification, Integer> {
	
	List<Qualification> findByIsActive(int activeStatus);
}
