//package com.employee.repository;
//
//import java.util.List;
//import java.util.Optional;
//
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
//import org.springframework.stereotype.Repository;
//
//import com.employee.entity.EmpChequeDetails;
//
//@Repository
//public interface EmpChequeDetailsRepository extends JpaRepository<EmpChequeDetails, Integer> {
//
//	Optional<EmpChequeDetails> findByEmpChequeDetailsIdAndIsActive(Integer empChequeDetailsId, int isActive);
//
//	/**
//	 * FIX: Corrected the capitalization.
//	 * 'findByEmpId_Emp_id' -> 'findByEmpId_emp_id'
//	 * This now perfectly matches the field 'empId' in this entity
//	 * and the nested field 'emp_id' in the Employee entity.
//	 */
//	List<EmpChequeDetails> findByEmpId_emp_id(int empId);
//	
//	/**
//	 * This query was already correct.
//	 */
//	@Query("SELECT e FROM EmpChequeDetails e WHERE e.empId.emp_id = :empId AND e.isActive = 1")
//    List<EmpChequeDetails> findActiveChequesByEmpId(@Param("empId") Integer empId);
//}

package com.employee.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.employee.entity.EmpChequeDetails;

@Repository
public interface EmpChequeDetailsRepository extends JpaRepository<EmpChequeDetails, Integer> {

	Optional<EmpChequeDetails> findByEmpChequeDetailsIdAndIsActive(Integer empChequeDetailsId, int isActive);

	/**
	 * FIX: The derived query name 'findByEmpId_emp_id' was confusing the JPA parser.
	 *
	 * This explicit @Query replaces it and is confirmed to be correct
	 * by your Employee entity (which has 'emp_id') and your
	 * EmpChequeDetails entity (which has 'empId').
	 */
	@Query("SELECT e FROM EmpChequeDetails e WHERE e.empId.emp_id = :empId")
	List<EmpChequeDetails> findByEmpId_emp_id(@Param("empId") int empId);
	
	/**
	 * This query for finding only active cheques is also correct.
	 */
	@Query("SELECT e FROM EmpChequeDetails e WHERE e.empId.emp_id = :empId AND e.isActive = 1")
    List<EmpChequeDetails> findActiveChequesByEmpId(@Param("empId") Integer empId);

}