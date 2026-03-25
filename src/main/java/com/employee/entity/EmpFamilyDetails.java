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
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "sce_emp_family_detl", schema = "sce_employee")
public class EmpFamilyDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int emp_family_detl_id;

	@ManyToOne
	@JoinColumn(name = "emp_id", nullable = false)
	private Employee emp_id; // Required NOT NULL

	private Integer is_dependent; // Nullable

	private Date date_of_birth; // Nullable

	// @Column(name = "first_name", nullable = false)
	// private String first_name; // Required NOT NULL
	//
	// @Column(name = "last_name", nullable = false)
	// private String last_name; // Required NOT NULL

	@Column(name = "full_name", length = 100, nullable = false)
	private String fullName;

	@Column(name = "adhaar_no") // Note: Matches your SQL spelling "adhaar"
	private Long adhaarNo;

	@Column(name = "occupation", nullable = false)
	private String occupation; // Required NOT NULL

	@ManyToOne
	@JoinColumn(name = "gender_id", nullable = false)
	private Gender gender_id; // Required NOT NULL

	@ManyToOne
	@JoinColumn(name = "blood_group_id", nullable = true)
	private BloodGroup blood_group_id; // Nullable as per DDL

	@Column(name = "nationality", nullable = false)
	private String nationality; // Required NOT NULL

	@Column(name = "is_late", nullable = false, length = 20)
	private String is_late; // Required NOT NULL

	@ManyToOne
	@JoinColumn(name = "relation_id", nullable = false)
	private Relation relation_id; // Required NOT NULL

	@Column(name = "is_sri_chaitanya_emp", nullable = false)
	private Integer is_sri_chaitanya_emp = 0; // Required NOT NULL, default 0 (No)

	@Column(name = "email")
	private String email; // Optional - nullable

	@Column(name = "contact_no")
	private Long contact_no; // Optional - nullable (int8 in database - bigint)

	@ManyToOne
	@JoinColumn(name = "parent_emp_id", referencedColumnName = "payroll_id")
	private Employee parent_emp_id; // Optional - links to sce_emp(payroll_id)

	private int is_active; // Default 1

	// Audit fields - required NOT NULL columns
	@Column(name = "created_by", nullable = false)
	private Integer created_by; // Default to 1 if not provided

	@Column(name = "created_date", nullable = false)
	private LocalDateTime created_date;

	@Column(name = "updated_by")
	private Integer updated_by;

	@Column(name = "updated_date")
	private LocalDateTime updated_date;

}