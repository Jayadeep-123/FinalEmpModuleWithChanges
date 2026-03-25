package com.employee.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.employee.entity.EmpProfileView;

@Repository

public interface EmployeeProfileViewRepository extends JpaRepository<EmpProfileView, Integer>{
	
//	 Optional<EmpProfileView> findByTempPayrollId(String tempPayrollId);
	Optional<EmpProfileView> findByPayrollId(String payrollId);
}