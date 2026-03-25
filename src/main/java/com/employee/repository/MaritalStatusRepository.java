package com.employee.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.employee.entity.MaritalStatus;

@Repository
public interface MaritalStatusRepository extends JpaRepository<MaritalStatus, Integer> {

	// Find by ID and is_active = 1
	// Using native query to ensure exact type matching
	@Query(value = "SELECT * FROM sce_employee.sce_marital_status WHERE marital_status_id = :id AND is_active = :isActive", nativeQuery = true)
	Optional<MaritalStatus> findByIdAndIsActive(@Param("id") Integer id, @Param("isActive") Integer isActive);
	
	List<MaritalStatus> findByIsActive(int activeStatus);
}

