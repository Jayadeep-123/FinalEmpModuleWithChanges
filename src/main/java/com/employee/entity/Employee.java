package com.employee.entity;

import java.sql.Date;
import java.time.LocalDateTime;

import jakarta.validation.constraints.Min;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "sce_emp", schema = "sce_employee")
public class Employee {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int emp_id;
	private String first_name;
	private String last_name;
	private Date date_of_join;
	private long primary_mobile_no;
	@Column(name = "secondary_mobile_no")
	private Long secondary_mobile_no;
	private String email;
	private String user_name; // Optional - nullable
	private String password; // Optional - nullable
	private int is_active;

	// --- THIS IS THE FIX ---
	// The conflicting 'private int cmps_id;' has been removed.
	// The @ManyToOne mapping below is the only one needed.
	// --- END OF FIX ---

	@Column(name = "temp_payroll_id") // Make sure this is your database column name
	private String tempPayrollId;

	@Column(name = "payroll_id")
	private String payRollId;

	// Note: passout_year column does NOT exist in sce_emp table
	// Marked as @Transient to prevent Hibernate from trying to map it to database
	@Transient
	private int passout_year;

	private Double total_experience; // Total years of experience

	private Date contract_start_date;
	private Date contract_end_date;

	@Column(name = "is_check_submit")
	private Integer is_check_submit;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "gender_id")
	private Gender gender;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "reference_emp_id")
	private Employee employee_reference;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "hired_by_emp_id")
	private Employee employee_hired;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "designation_id")
	private Designation designation;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "department_id")
	private Department department;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "manager_id")
	private Employee employee_manager_id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "category_id")
	private Category category;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "reporting_manager_id")
	private Employee employee_reporting_id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "emp_type_id")
	private EmployeeType employee_type_id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "highest_qualification_id")
	private Qualification qualification_id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "emp_work_mode_id")
	private WoringMode workingMode_id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "replaced_by_emp_id", nullable = true)
	private Employee employee_replaceby_id; // Optional - can be null

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "join_type_id")
	private JoiningAs join_type_id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "mode_of_hiring_id")
	private ModeOfHiring modeOfHiring_id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "emp_app_status_id", nullable = false)
	private EmployeeCheckListStatus emp_check_list_status_id; // Required NOT NULL

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "emp_status_id", nullable = false)
	private EmployeeStatus emp_status_id; // Required NOT NULL - FK to sce_emp_status

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cmps_id", nullable = true)
	private Campus campus_id; // Optional - can be null (maps to cmps_id in database)

	@Column(name = "pre_chaitanya_id")
	private String pre_chaitanya_id; // Changed from ManyToOne to String (varchar) as per DDL

	// Audit fields - required NOT NULL columns
	@Column(name = "created_by", nullable = false)
	private Integer created_by;

	@Column(name = "created_date", nullable = false)
	private LocalDateTime created_date;

	@Column(name = "updated_by")
	private Integer updated_by;

	@Column(name = "updated_date")
	private LocalDateTime updated_date;

	@Column(name = "notice_period")
	private String notice_period; // Optional - nullable

	@Column(name = "emp_app_check_list_detl_id", length = 100)
	private String emp_app_check_list_detl_id; // Optional - nullable

	@Column(name = "remarks")
	private String remarks; // Optional - nullable, for storing rejection/back to campus remarks

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "agreement_org_id")
	private Organization agreement_org_id; // Optional - FK to sce_campus.sce_organization

	@Column(name = "aagreement_type")
	private String agreement_type;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "org_id")
	private Organization org_id; // Optional - FK to sce_campus.sce_organization

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "building_id", nullable = true)
	private Building building_id; // Optional - can be null

	@Min(value = 18, message = "Age must be at least 18")
	@Column(name = "age")
	private Integer age;// Optional - nullable

	@Column(name = "ssc_no")
	private Long ssc_no; // SSC number - moved from EmpDocuments to Employee table

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "emp_type_hiring_id")
	private EmployeeTypeHiring employee_type_hiring_id;

}