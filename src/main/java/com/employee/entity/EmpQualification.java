package com.employee.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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
@Table(name = "sce_emp_qualification", schema = "sce_employee")
public class EmpQualification {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "emp_qualification_id")
	private Integer emp_qualification_id;
	
	@ManyToOne
	@JoinColumn(name = "emp_id", nullable = false)
	private Employee emp_id;
	
	@Column(name = "institute", length = 150)
	private String institute;
	
	@Column(name = "university", length = 250)
	private String university;
	
	@ManyToOne
	@JoinColumn(name = "qualification_id", nullable = false)
	private Qualification qualification_id;
	
	@Column(name = "passedout_year")
	private Integer passedout_year;
	
	@ManyToOne
	@JoinColumn(name = "qualification_degree_id", nullable = false)
	private QualificationDegree qualification_degree_id;
	
	@Column(name = "specialization", length = 50)
	private String specialization;
	
	@Column(name = "is_active", nullable = false)
	private Integer is_active = 1;
	
	// Audit fields - required NOT NULL columns
	@Column(name = "created_by", nullable = false)
	private Integer created_by = 1; // Default to 1 if not provided
	
	@Column(name = "created_date", nullable = false)
	private LocalDateTime created_date;
	
	@Column(name = "updated_by")
	private Integer updated_by;
	
	@Column(name = "updated_date")
	private LocalDateTime updated_date;
	
//	@ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "emp_doc_id") // This matches the new column in your database
//    private EmpDocuments empDocument;
	
}

