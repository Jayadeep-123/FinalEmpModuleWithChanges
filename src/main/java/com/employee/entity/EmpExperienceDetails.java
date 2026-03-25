package com.employee.entity;

import java.sql.Date;
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
@Table(name = "sce_emp_exp_detl", schema = "sce_employee")
public class EmpExperienceDetails {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int emp_exp_detl_id;

	@Column(name = "pre_organization_name", nullable = false)
	private String pre_organigation_name; // Required NOT NULL

	@Column(name = "date_of_join", nullable = false)
	private Date date_of_join; // Required NOT NULL

	@Column(name = "date_of_leave", nullable = false)
	private Date date_of_leave; // Required NOT NULL

	@Column(name = "designation", nullable = false)
	private String designation; // Required NOT NULL

	@Column(name = "leaving_reason", nullable = false)
	private String leaving_reason; // Required NOT NULL

	@Column(name = "nature_of_duties", nullable = false)
	private String nature_of_duties; // Required NOT NULL

	@Column(name = "company_addr", nullable = false)
	private String company_addr; // Required NOT NULL

	@Column(name = "gross_salary", nullable = false)
	private int gross_salary; // Required NOT NULL

	private int is_active; // Default 1

	// private Integer pre_chaitanya_id;

	@ManyToOne
	@JoinColumn(name = "emp_id", nullable = false)
	private Employee employee_id; // Required NOT NULL

	// Audit fields - required NOT NULL columns
	@Column(name = "created_by", nullable = false)
	private Integer created_by = 1; // Default to 1 if not provided

	@Column(name = "created_date", nullable = false)
	private LocalDateTime created_date;

	@Column(name = "updated_by")
	private Integer updated_by;

	@Column(name = "updated_date")
	private LocalDateTime updated_date;

	@Column(name = "ctc")
	private Double ctc;

}
