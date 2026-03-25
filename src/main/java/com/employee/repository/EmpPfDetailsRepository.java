package com.employee.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.employee.entity.EmpPfDetails;

@Repository
public interface EmpPfDetailsRepository extends JpaRepository<EmpPfDetails, Integer> {
	
	// Find by employee ID
	@Query("SELECT epf FROM EmpPfDetails epf WHERE epf.employee_id.emp_id = :empId")
	Optional<EmpPfDetails> findByEmployeeId(@Param("empId") Integer empId);
	
	// Find by employee ID and is_active = 1
	@Query("SELECT epf FROM EmpPfDetails epf WHERE epf.employee_id.emp_id = :empId AND epf.is_active = :isActive")
	Optional<EmpPfDetails> findByEmployeeIdAndIsActive(@Param("empId") Integer empId, @Param("isActive") Integer isActive);
	
	
    @Query("SELECT COUNT(epf) > 0 FROM EmpPfDetails epf WHERE epf.esi_no = :esi")
    boolean existsByEsi_no(@Param("esi") Long esi);
 
    @Query("SELECT COUNT(epf) > 0 FROM EmpPfDetails epf WHERE epf.uan_no = :uan")
    boolean existsByUan_no(@Param("uan") Long uan);

}

