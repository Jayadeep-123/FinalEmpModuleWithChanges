package com.employee.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.employee.entity.EmpPaymentType;

@Repository
public interface EmpPaymentTypeRepository extends JpaRepository<EmpPaymentType, Integer> {

	// Find by ID and is_active = 1
	@Query("SELECT ept FROM EmpPaymentType ept WHERE ept.emp_payment_type_id = :id AND ept.is_active = :isActive")
	Optional<EmpPaymentType> findByIdAndIsActive(@Param("id") Integer id, @Param("isActive") Integer isActive);
	
	// Find by payment_type name
	@Query("SELECT ept FROM EmpPaymentType ept WHERE ept.payment_type = :paymentType")
	Optional<EmpPaymentType> findByPayment_type(@Param("paymentType") String paymentType);
	
	// Find by payment_type name and is_active = 1
	@Query("SELECT ept FROM EmpPaymentType ept WHERE ept.payment_type = :paymentType AND ept.is_active = :isActive")
	Optional<EmpPaymentType> findByPayment_typeAndIsActive(@Param("paymentType") String paymentType, @Param("isActive") Integer isActive);
	
    /**
     * Finds all payment types by their active status.
     * CORRECTED: Using @Query to map 'findByIsActive' to the 'is_active' field.
     */
	@Query("SELECT e FROM EmpPaymentType e WHERE e.is_active = :status")
	List<EmpPaymentType> findByIsActive(@Param("status") int status);

}