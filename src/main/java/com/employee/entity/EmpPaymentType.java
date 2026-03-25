package com.employee.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "sce_emp_payment_type", schema = "sce_employee")
public class EmpPaymentType {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "emp_payment_type_id")
	private Integer emp_payment_type_id;
	
	@Column(name = "payment_type", nullable = false, length = 50)
	private String payment_type;
	
	@Column(name = "is_active", nullable = false)
	private Integer is_active = 1;
	
	// Audit fields
	@Column(name = "created_by", nullable = false)
	private Integer created_by = 1; // Default to 1 if not provided
	
	@Column(name = "created_date")
	private LocalDateTime created_date;
	
	@Column(name = "updated_by")
	private Integer updated_by;
	
	@Column(name = "updated_date")
	private LocalDateTime updated_date;

	
}

