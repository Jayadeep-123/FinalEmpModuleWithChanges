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
@Table(name = "sce_emp_structure", schema = "sce_employee")
public class EmpStructure {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "emp_structure_id")
	private Integer empStructureId;
	
	@Column(name = "structure_name", nullable = false, length = 30) // This is correct
	private String structreName; // Note: DDL has typo "structre_name" instead of "structure_name"
	
	@Column(name = "is_active", nullable = false)
	private Integer isActive = 1; // Default 1
	
	// Audit fields - required NOT NULL columns
	@Column(name = "created_by", nullable = false)
	private Integer createdBy = 1; // Default to 1 if not provided
	
	@Column(name = "created_date", nullable = false)
	private LocalDateTime createdDate;
	
	@Column(name = "updated_by")
	private Integer updatedBy;
	
	@Column(name = "updated_date")
	private LocalDateTime updatedDate;
}