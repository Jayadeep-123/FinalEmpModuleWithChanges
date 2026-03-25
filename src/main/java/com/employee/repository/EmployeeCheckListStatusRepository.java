package com.employee.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.employee.entity.Employee;
import com.employee.entity.EmployeeCheckListStatus;

@Repository
public interface EmployeeCheckListStatusRepository extends JpaRepository<EmployeeCheckListStatus, Integer> {
	
	/**
     * Find EmployeeCheckListStatus by status name
     * @param statusName The status name (e.g., "Forward to CO", "Back to Campus", "Confirm")
     * @return Optional EmployeeCheckListStatus
     */
    @Query("SELECT e FROM EmployeeCheckListStatus e WHERE e.check_app_status_name = :statusName")
    Optional<EmployeeCheckListStatus> findByCheck_app_status_name(@Param("statusName") String statusName);
   
    /**
     * Find EmployeeCheckListStatus by status name and is_active
     * @param statusName The status name
     * @param isActive Active status (1 for active, 0 for inactive)
     * @return Optional EmployeeCheckListStatus
     */
    @Query("SELECT e FROM EmployeeCheckListStatus e WHERE e.check_app_status_name = :statusName AND e.is_active = :isActive")
    Optional<EmployeeCheckListStatus> findByCheck_app_status_nameAndIs_active(@Param("statusName") String statusName, @Param("isActive") Integer isActive);

	

}

