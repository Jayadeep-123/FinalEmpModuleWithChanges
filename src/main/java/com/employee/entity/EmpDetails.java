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
import jakarta.persistence.Transient;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "sce_emp_detl", schema = "sce_employee")
public class EmpDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int emp_detl_id;

	private String adhaar_name;

	private Date date_of_birth;

	@Column(name = "emergency_ph_no", nullable = false)
	private String emergency_ph_no; // Required NOT NULL

	@Column(name = "personal_email")
	private String personal_email; // Database column is personal_email (not email)

	@Column(name = "adhaar_no")
	private Long adhaar_no; // Database column is adhaar_no (not aadhar_num)

	@Column(name = "pancard_no")
	private String pancard_no; // Database column is pancard_no (not pancard_num)

	@Column(name = "adhaar_enrolment_no")
	private String adhaar_enrolment_no; // Database column is adhaar_enrolment_no (not adhaar_enrolment_num)

	// Note: specialization column does NOT exist in sce_emp_detl table
	@Transient
	private String specialization;

	private int is_active;
	private String status;

	// Note: passout_year column does NOT exist in sce_emp_detl table
	@Transient
	private int passout_year;

	// Audit fields - required NOT NULL columns
	@Column(name = "created_by", nullable = false)
	private Integer created_by; // Default to 1 if not provided

	@Column(name = "created_date", nullable = false)
	private LocalDateTime created_date;

	@Column(name = "updated_by")
	private Integer updated_by;

	@Column(name = "updated_date")
	private LocalDateTime updated_date;

	@ManyToOne
	@JoinColumn(name = "emp_id")
	private Employee employee_id;

	@ManyToOne
	@JoinColumn(name = "blood_group_id")
	private BloodGroup bloodGroup_id;

	@ManyToOne
	@JoinColumn(name = "caste_id")
	private Caste caste_id;

	@ManyToOne
	@JoinColumn(name = "religion_id")
	private Religion religion_id;

	@ManyToOne
	@JoinColumn(name = "marital_status_id")
	private MaritalStatus marital_status_id;

	@ManyToOne
	@JoinColumn(name = "relation_id")
	private Relation relation_id;

	@Column(name = "father_name", length = 100)
	@NotNull
	private String fatherName;

	@NotNull(message = "Value cannot be null")
	@Min(value = 100000000000L, message = "UAN must be at least 12 digits")
	@Max(value = 999999999999L, message = "UAN cannot be more than 12 digits")
	@Column(name = "uan_no")
	private Long uanNo;

}
