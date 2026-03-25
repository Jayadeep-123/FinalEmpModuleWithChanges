package com.employee.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.employee.entity.EmployeeBasicInfoView;

@Repository
public interface EmpBasicInfoViewRepo extends JpaRepository<EmployeeBasicInfoView, Integer>{

	Optional<EmployeeBasicInfoView> findByPayrollId(String payrollId);

}