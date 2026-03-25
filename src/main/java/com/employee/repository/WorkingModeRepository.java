package com.employee.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.employee.entity.WoringMode;

public interface WorkingModeRepository extends JpaRepository<WoringMode, Integer> {

	// Find by ID and is_active = 1
	@Query("SELECT wm FROM WoringMode wm WHERE wm.emp_work_mode_id = :id AND wm.isActive = :isActive")
	Optional<WoringMode> findByIdAndIsActive(@Param("id") Integer id, @Param("isActive") Integer isActive);
	
	
	List<WoringMode> findByIsActive(int activeStatus);
}
