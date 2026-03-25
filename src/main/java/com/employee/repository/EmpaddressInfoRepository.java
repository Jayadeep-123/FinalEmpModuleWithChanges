package com.employee.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query; // 1. Add this import
import org.springframework.data.repository.query.Param; // 2. Add this import
import org.springframework.stereotype.Repository;

import com.employee.dto.AddressResponseDTO;
import com.employee.entity.EmpaddressInfo;
import com.employee.entity.Employee;

@Repository
public interface EmpaddressInfoRepository extends JpaRepository<EmpaddressInfo, Integer> {

	/**
	 * Finds addresses for an employee based on their payroll ID.
	 * CORRECTED: Added @Query because Spring was incorrectly looking for 'empId'
	 * instead of the correct 'emp_id' field.
	 */
	@Query("SELECT e FROM EmpaddressInfo e WHERE e.emp_id.payRollId = :payrollId") // 3. Add this @Query
	List<EmpaddressInfo> findByEmpId_PayrollId(@Param("payrollId") String payrollId); // 4. Add @Param

	@Query("SELECT a FROM EmpaddressInfo a WHERE a.emp_id = :employee")
	List<EmpaddressInfo> findByEmployeeEntity(@Param("employee") Employee employee);

	@Query("""
			    SELECT new com.employee.dto.AddressResponseDTO(
			        a.addrs_type,
			        a.house_no,
			        a.landmark,
			        a.postal_code,
			        c.cityName,
			        d.districtName,
			        s.stateName,
			        co.countryName,
			        a.emrg_contact_no,
			        a.is_per_and_curr
			    )
			    FROM EmpaddressInfo a
			    JOIN a.emp_id e
			    JOIN a.city_id c
			    LEFT JOIN a.district_id d
			    JOIN a.state_id s
			    JOIN a.country_id co
			    WHERE e.tempPayrollId = :tempPayrollId
			      AND a.is_active = 1
			""")
	List<AddressResponseDTO> findAddressesByTempPayrollId(
			@Param("tempPayrollId") String tempPayrollId);
}