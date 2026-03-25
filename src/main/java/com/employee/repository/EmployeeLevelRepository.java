package com.employee.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.employee.dto.GenericDropdownDTO;
import com.employee.entity.EmployeeLevel;


@Repository
public interface EmployeeLevelRepository extends JpaRepository<EmployeeLevel, Integer>{
	
	
    /**
     * Finds all active employee levels as DTOs.
     * CORRECTED: 
     * 1. Changed 'e.emp_level_name' to 'e.level_name' to match the entity.
     * 2. Changed 'e.is_active' to 'e.isActive' to match the entity.
     */
	@Query("SELECT new com.employee.dto.GenericDropdownDTO(e.emp_level_id, e.level_name) " +
	           "FROM EmployeeLevel e WHERE e.isActive = 1") // <-- Corrected this line
	    List<GenericDropdownDTO> findAllActiveEmpLevels();
}