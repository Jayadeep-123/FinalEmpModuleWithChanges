package com.employee.entity;

import java.sql.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "emp_onboarding_status", schema = "sce_employee")
public class EmpOnboardingStatusView {

	private Integer emp_id;
	private String employee_name;
	private Integer cmps_id;
	private String cmps_name;
	private Integer category_id;
	private String category_name;
	private String payroll_id;
	@Id
	private String temp_payroll_id;
	private Date date_of_join;
	private Date leaving_date;
	private Integer gender_id;
	private String gender_name;
	private String city_name;
	private String remarks;
	private Integer join_type_id;
	private String join_type;
	private Integer replaced_by_emp_id;
	private String replaced_by_emp_payroll;
	private Integer verify_kyc;
	private String kyc_status;
	private String check_app_status_name;

}
