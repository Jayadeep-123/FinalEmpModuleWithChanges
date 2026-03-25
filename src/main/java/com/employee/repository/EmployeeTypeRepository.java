package com.employee.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.employee.entity.EmployeeType;

@Repository
public interface EmployeeTypeRepository extends JpaRepository<EmployeeType, Integer> {

	// Find by ID and is_active = 1
	@Query("SELECT et FROM EmployeeType et WHERE et.emp_type_id = :id AND et.isActive = :isActive")
	Optional<EmployeeType> findByIdAndIsActive(@Param("id") Integer id, @Param("isActive") Integer isActive);
	
	
	 List<EmployeeType> findByIsActive(Integer isActive);
}

