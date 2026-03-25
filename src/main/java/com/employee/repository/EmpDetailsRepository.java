package com.employee.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.employee.entity.EmpDetails;
import com.employee.entity.Employee;

@Repository
public interface EmpDetailsRepository extends JpaRepository<EmpDetails, Integer> {
	@Query("SELECT ed FROM EmpDetails ed WHERE ed.personal_email = :personalEmail")
	Optional<EmpDetails> findByPersonal_email(@Param("personalEmail") String personalEmail);
	
	@Query("SELECT ed FROM EmpDetails ed "
	         + "WHERE ed.employee_id IN :employeeIds AND ed.is_active = 1") // <--- ADDED isActive = 1 CHECK
	    List<EmpDetails> findByEmployee_idIn(List<Employee> employeeIds);

	@Query("SELECT ed FROM EmpDetails ed WHERE ed.adhaar_no = :adhaarNo")
	Optional<EmpDetails> findByAdhaar_no(@Param("adhaarNo") Long aadharNum);
	
	@Query("SELECT ed FROM EmpDetails ed WHERE ed.adhaar_no = :adhaarNo AND ed.employee_id.emp_id != :empId")
	Optional<EmpDetails> findByAdhaar_noExcludingEmpId(@Param("adhaarNo") String adhaarNo, @Param("empId") Integer empId);
	
	@Query("SELECT ed FROM EmpDetails ed WHERE ed.personal_email = :personalEmail AND ed.employee_id.emp_id != :empId")
	Optional<EmpDetails> findByPersonal_emailExcludingEmpId(@Param("personalEmail") String personalEmail, @Param("empId") Integer empId);

	@Query("SELECT ed FROM EmpDetails ed WHERE ed.employee_id.emp_id = :empId")
	Optional<EmpDetails> findByEmployeeId(@Param("empId") Integer empId);
	
	
	@Query("SELECT COUNT(ed) > 0 FROM EmpDetails ed WHERE ed.adhaar_no = :adhaarNo")
    boolean existsByAdhaar_no(@Param("adhaarNo") Long adhaarNo);
 
    @Query("SELECT COUNT(ed) > 0 FROM EmpDetails ed WHERE ed.pancard_no = :pan")
    boolean existsByPancard_no(@Param("pan") String pan);
 
    @Query("SELECT COUNT(ed) > 0 FROM EmpDetails ed WHERE ed.personal_email = :email")
    boolean existsByPersonal_email(@Param("email") String email);
 
    @Query("SELECT COUNT(ed) > 0 FROM EmpDetails ed WHERE ed.uanNo = :uan")
    boolean existsByUanNo(@Param("uan") Long uan);
 
}

