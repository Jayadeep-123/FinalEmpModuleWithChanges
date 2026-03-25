package com.employee.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryInfoDTO1 {

	private String employeeType;
	private Integer employeeTypeId;
	private String department;
	private Integer departmentId;
	private String designation;
	private Integer designationId;

	// These fields from the sce_emp_subject table.
	private String subject;
	private Integer subjectId;
	private Integer orientationId;
	private String orientationName;
	private Integer agreedPeriodsPerWeek;
}
