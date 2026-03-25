package com.employee.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "sce_emp_doc", schema = "sce_employee")
public class EmpDocuments {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int emp_doc_id;

	private String doc_path;
	private int is_verified;
	private int is_active;

	@ManyToOne
	@JoinColumn(name = "emp_exp_detl_id", nullable = true)
	private EmpExperienceDetails emp_exp_detl_id; // Added to link documents to experience records

	@ManyToOne
	@JoinColumn(name = "emp_id", nullable = false)
	private Employee emp_id; // Required NOT NULL

	@ManyToOne
	@JoinColumn(name = "doc_type_id", nullable = false)
	private EmpDocType emp_doc_type_id; // Required NOT NULL

	// Audit fields - required NOT NULL columns
	@Column(name = "created_by", nullable = false)
	private Integer created_by = 1; // Default to 1 if not provided

	@Column(name = "created_date", nullable = false)
	private LocalDateTime created_date;

	@Column(name = "updated_by")
	private Integer updated_by;

	@Column(name = "updated_date")
	private LocalDateTime updated_date;

}