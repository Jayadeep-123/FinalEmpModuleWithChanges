package com.employee.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.employee.entity.BankDetails;

@Repository
public interface BankDetailsRepository extends JpaRepository<BankDetails, Integer> {
	
    // This query is correct.
	@Query("SELECT bd FROM BankDetails bd WHERE bd.empId.emp_id = :empId")
	List<BankDetails> findByEmpId_Emp_id(@Param("empId") Integer empId);
	
	// This query is correct, it uses the 'isActive' field from BankDetails.
	@Query("SELECT bd FROM BankDetails bd WHERE bd.empId.emp_id = :empId AND bd.isActive = :isActive")
	List<BankDetails> findByEmpIdAndIsActive(@Param("empId") Integer empId, @Param("isActive") Integer isActive);
	
	
    /**
     * THIS IS THE QUERY THAT CAUSED THE LAST CRASH
     * CORRECTED: 
     * 1. Changed 'b.emp_payment_type_id' to 'b.empPaymentType' (to match your entity)
     * 2. 'b.empId.emp_id' is correct (empId from BankDetails, emp_id from Employee)
     * 3. 'b.isActive' is correct (isActive from BankDetails)
     */
	@Query("SELECT b FROM BankDetails b " +
	          
	           "WHERE b.empId.emp_id = :empId AND b.isActive = 1")
	    List<BankDetails> findActiveBankDetailsByEmpId(@Param("empId") int empId);

    /**
     * CORRECTED: 
     * 1. 'b.empId' is correct (empId from BankDetails)
     * 2. 'payRollId' is correct (payRollId from Employee)
     */
	    @Query("SELECT b FROM BankDetails b WHERE b.empId.payRollId = :payrollId")
	    List<BankDetails> findByEmployeePayrollId(String payrollId);

    /**
     * CORRECTED: 
     * 1. 'b.empId.emp_id' is correct.
     */
	    @Query("SELECT b FROM BankDetails b WHERE b.empId.emp_id = :empId")
	    List<BankDetails> findByEmpId_EmpId(@Param("empId") int empId);
}