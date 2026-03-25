package com.employee.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for Category Info Tab (Step 7)
 * Maps to: Employee entity fields (employeeTypeId, subjectId, departmentId, designationId)
 * Entities: EmployeeType, Subject, Department, Designation
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoryInfoDTO {
	
	// These map to existing Employee entity foreign keys
	// Note: Some fields may already be in BasicInfoDTO, but this tab re-emphasizes them
	private Integer employeeTypeId; // From "Select Employment Type" - maps to EmployeeType
	private Integer subjectId; // From "Select Subject" - maps to Subject entity
	private Integer departmentId; // From "Select Department" - maps to Department (already in Employee)
	private Integer designationId; // From "Select Designation" - maps to Designation (already in Employee)
	private Integer orientationId; // From "Select Orientation" - maps to Orientation (stored in EmpSubject.orientation_id)

	private Integer agreedPeriodsPerWeek; // From "Enter Agreed Periods per week" - may need to add to Employee entity if not exists
	
	// Audit fields - passed from frontend
	private Integer createdBy; // User ID who created the employee record
	private Integer updatedBy; // User ID who updated the employee record
}

