package com.employee.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.employee.entity.CostCenter;

@Repository
public interface CostCenterRepository extends JpaRepository<CostCenter, Integer> {
	
	// Basic CRUD operations available from JpaRepository
	// CostCenter doesn't have is_active field, so no need for findByIdAndIsActive
}