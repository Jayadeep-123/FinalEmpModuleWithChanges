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
@Table(name = "sce_organization", schema = "sce_campus")
public class Organization {

	@Id
	@Column(name = "org_id")
	private int organizationId;

	@Column(name = "org_name")
	private String organizationName;

	@Column(name = "org_type")
	private String organizationType;

	@Column(name = "addrs")
	private String organizationAddress;

	@Column(name = "head")
	private String organizationHead;

	@Column(name = "code")
	private String organizationCode;

	@Column(name = "org_display")
	private String organizationDisplay;

	@Column(name = "payroll_code")
	private Long payrollCode;

	@Column(name = "payroll_max_no")
	private Long payrollMaxNo;

	@Column(name = "is_active")
	private int isActive;

	@Column(name = "cons_payroll_max_no")
	private Long conPayrollMaxNo;

}