package com.employee.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for Salary Info POST request
 * Maps to: EmpSalaryInfo entity
 * 
 * Required Fields:
 * - tempPayrollId: Must be a valid temp_payroll_id from Employee table
 * - monthlyTakeHome: Required (NOT NULL) - Maps to monthly_take_home (bytea) in
 * database
 * - yearlyCtc: Required (NOT NULL)
 * - empStructureId: Required (NOT NULL)
 * 
 * Optional Fields:
 * - ctcWords: Optional
 * - gradeId: Optional (FK to sce_emp_grade)
 * - costCenterId: Optional (FK to sce_emp_costcenter)
 * - orgId: Optional (FK to sce_campus.sce_organization) - updates
 * Employee.org_id when forwarding to Central Office
 * - checkListIds: Optional (comma-separated string like "1,2,3,4,5,6,7")
 * 
 * Note: payroll_id will be automatically taken from Employee table based on
 * tempPayrollId
 * Note: emp_id will be automatically fetched from Employee table based on
 * tempPayrollId
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SalaryInfoDTO {

	private String tempPayrollId; // REQUIRED - To find employee by temp_payroll_id (emp_id will be fetched in
									// backend)
	private String payrollId; // Added to support retrieval by payroll_id

	// Salary Information
	// Note: payroll_id will be taken from Employee table, not needed in DTO
	private Double monthlyTakeHome; // REQUIRED - Monthly Take Home (maps to monthly_take_home bytea in database)
	private String ctcWords; // Optional - CTC in words (varchar(250))
	private Double yearlyCtc; // REQUIRED - Yearly CTC (float8, NOT NULL)
	private Integer empStructureId; // REQUIRED - Employee Structure ID (NOT NULL)
	private Integer gradeId; // Optional - Grade ID (FK to sce_emp_grade)

	// New fields from DDL
	private Integer costCenterId; // Optional - Cost Center ID (FK to sce_emp_costcenter)
	private Integer orgId; // Optional - Organization/Company ID (FK to sce_campus.sce_organization) -
							// updates Employee.org_id
	private Boolean isPfEligible; // REQUIRED - PF Eligible flag (boolean, converts to 1/0 in database)
	private Boolean isEsiEligible; // REQUIRED - ESI Eligible flag (boolean, converts to 1/0 in database)

	// PF/ESI/UAN Information (from salary service)
	private String pfNo; // Optional - PF Number (only set if isPfEligible = 1)
	private java.sql.Date pfJoinDate; // Optional - PF Join Date (only set if isPfEligible = 1)
	private Long esiNo; // Optional - ESI Number (only set if isEsiEligible = 1)
	private Long uanNo; // Optional - UAN Number (always set if provided, no validation)

	// Checklist IDs - comma-separated string like "1,2,3,4,5,6,7"
	private String checkListIds; // Optional - Comma-separated checklist IDs

	// Name fields for UI display
	private String employeeName;
	private String empStructureName;
	private String gradeName;
	private String costCenterName;
	private String orgName;

	// Audit field - passed from frontend
	private Integer updatedBy;
}
