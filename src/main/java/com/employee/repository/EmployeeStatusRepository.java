package com.employee.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.employee.entity.EmployeeStatus;

@Repository
public interface EmployeeStatusRepository extends JpaRepository<EmployeeStatus, Integer> {

	// Find by ID and is_active = 1
	@Query("SELECT es FROM EmployeeStatus es WHERE es.empStatusId = :id AND es.isActive = :isActive")
	Optional<EmployeeStatus> findByIdAndIsActive(@Param("id") Integer id, @Param("isActive") Integer isActive);

	// Find by status name and is_active = 1
	@Query("SELECT es FROM EmployeeStatus es WHERE es.empStatusName = :statusName AND es.isActive = :isActive")
	Optional<EmployeeStatus> findByStatusNameAndIsActive(@Param("statusName") String statusName, @Param("isActive") Integer isActive);

}

