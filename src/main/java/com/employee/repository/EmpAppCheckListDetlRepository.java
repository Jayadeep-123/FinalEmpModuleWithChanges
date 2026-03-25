package com.employee.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.employee.entity.EmpAppCheckListDetl;
import com.employee.entity.EmployeeCheckListStatus;

@Repository
public interface EmpAppCheckListDetlRepository extends JpaRepository<EmpAppCheckListDetl, Integer> {

	// ⚠️ WARNING: Do NOT use default JpaRepository methods like findById(), findAll(), etc.
	// They will try to load the entity and fail because check_list_id column doesn't exist.
	// Use the native query methods below instead.
	
	// Find by ID and is_active = 1
	// Using native query with explicit column list to avoid Hibernate loading non-existent check_list_id
	@Query(value = "SELECT emp_app_check_list_detl_id, check_list_detl_name, short_name, is_active, check_list_name, created_on, created_by, created_date, updated_by, updated_date FROM sce_employee.sce_emp_app_check_list_detl WHERE emp_app_check_list_detl_id = :id AND is_active = :isActive", nativeQuery = true)
	Optional<Object[]> findByIdAndIsActiveNative(@Param("id") Integer id, @Param("isActive") Integer isActive);
	
	// Alternative: Simple existence check (doesn't load entity, avoids check_list_id issue)
	@Query(value = "SELECT COUNT(*) > 0 FROM sce_employee.sce_emp_app_check_list_detl WHERE emp_app_check_list_detl_id = :id AND is_active = :isActive", nativeQuery = true)
	boolean existsByIdAndIsActive(@Param("id") Integer id, @Param("isActive") Integer isActive);
	
	// Find all active checklist details (is_active = 1)
	// Using native query with explicit column list to avoid Hibernate loading non-existent check_list_id
	@Query(value = "SELECT emp_app_check_list_detl_id, check_list_detl_name, short_name, is_active, check_list_name, created_on, created_by, created_date, updated_by, updated_date FROM sce_employee.sce_emp_app_check_list_detl WHERE is_active = :isActive", nativeQuery = true)
	List<Object[]> findByIsActiveNative(@Param("isActive") Integer isActive);
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