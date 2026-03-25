package com.employee.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "sce_emp_profile", schema = "sce_employee")
public class EmpProfileView {
	@Id
	@Column(name = "emp_id")
	private Integer empId;

	@Column(name = "full_name")
	private String fullName;

	@Column(name = "cmps_id")
	private Integer campusId;

	@Column(name = "cmps_name")
	private String campusName;

	@Column(name = "date_of_birth")
	private String dateOfBirth;

	@Column(name = "gender_name")
	private String genderName;

	@Column(name = "marital_status_type")
	private String maritalStatusType;

	@Column(name = "temp_payroll_id")
	private String tempPayrollId;

	@Column(name = "payroll_id")
	private String payrollId;

	@Column(name = "payroll_company_id")
	private Integer payrollCompanyId;

	@Column(name = "payroll_company")
	private String payrollCompany;

	@Column(name = "department_name")
	private String departmentName;

	@Column(name = "designation_name")
	private String designationName;

	@Column(name = "primary_mobile_no")
	private String primaryMobileNo;

	@Column(name = "email")
	private String email;

	@Column(name = "reporting_manager_id")
	private Integer reportingManagerId;

	@Column(name = "reporting_manager_name")
	private String reportingManagerName;

	@Column(name = "blood_group_name")
	private String bloodGroupName;

	@Column(name = "contract_start_date")
	private String contractStartDate;

	@Column(name = "contract_end_date")
	private String contractEndDate;

	@Column(name = "contract_period")
	private Double contractPeriod;

	@Column(name = "emp_type")
	private String empType;

	@Column(name = "mode_of_hiring_id")
	private Integer modeOfHiringId;

	@Column(name = "mode_of_hiring_name")
	private String modeOfHiringName;

}