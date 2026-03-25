package com.employee.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.employee.entity.EmpExperienceDetails;

@Repository
public interface EmpExperienceDetailsRepository extends JpaRepository<EmpExperienceDetails, Integer> {
	
	
    /**
     * Finds experience by temp payroll ID.
     * CORRECTED: Changed 'e.employee' to 'e.employee_id' to match your entity.
     */
	@Query("SELECT e FROM EmpExperienceDetails e JOIN e.employee_id emp WHERE emp.tempPayrollId = :tempPayrollId")
	List<EmpExperienceDetails> findExperienceByTempPayrollId(@Param("tempPayrollId") String tempPayrollId);
	

    /**
     * Finds active experience for an employee.
     * CORRECTED: 
     * 1. Changed 'e.employee.emp_id' to 'e.employee_id.emp_id' (to match your entity).
     * 2. Changed 'e.is_active = 1' (which was already correct).
     */
	@Query("SELECT e FROM EmpExperienceDetails e WHERE e.employee_id.emp_id = :empId AND e.is_active = 1")
    List<EmpExperienceDetails> findActiveByEmployeeId(@Param("empId") int empId);
	
	
	@Query("SELECT e FROM EmpExperienceDetails e JOIN e.employee_id emp WHERE emp.payRollId = :payrollId")
	List<EmpExperienceDetails> findExperienceByPayrollId(@Param("payrollId") String payrollId);
}