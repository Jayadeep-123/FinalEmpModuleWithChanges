package com.employee.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for Forward to Divisional Office Response
 * Contains employee information, status details, and salary information after
 * forwarding
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ForwardToDivisionalOfficeResponseDTO {

	// Employee and Status Information
	private String tempPayrollId; // Temp Payroll ID
	private Integer empId; // Employee ID
	private String previousStatus; // Previous status (e.g., "Incompleted" or "Back to Campus")
	private String newStatus; // New status (e.g., "Pending at DO")
	private String message; // Success message

	// Salary Information (from SalaryInfoDTO)
	private Double monthlyTakeHome; // Monthly Take Home (maps to monthly_take_home bytea in database)
	private String ctcWords; // CTC in words (varchar(250))
	private Double yearlyCtc; // Yearly CTC (float8)
	private Integer empStructureId; // Employee Structure ID
	private Integer gradeId; // Grade ID (FK to sce_emp_grade)
	private Integer costCenterId; // Cost Center ID (FK to sce_emp_costcenter)
	private Integer orgId; // Organization/Company ID (FK to sce_campus.sce_organization)
	private Boolean isPfEligible; // PF Eligible flag (boolean, converts to 1/0 in database)
	private Boolean isEsiEligible; // ESI Eligible flag (boolean, converts to 1/0 in database)

	// PF/ESI/UAN Information
	private String pfNo; // PF Number (only set if isPfEligible = 1)
	private java.sql.Date pfJoinDate; // PF Join Date (only set if isPfEligible = 1)
	private Long esiNo; // ESI Number (only set if isEsiEligible = 1)
	private Long uanNo; // UAN Number (always set if provided, no validation)

	// Audit Information
	private Integer createdBy;
	private java.time.LocalDateTime createdDate;
	private Integer updatedBy;
}
