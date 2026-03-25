package com.employee.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for Back to Campus request
 * Used when sending an employee back to campus for corrections
 * 
 * Required Fields:
 * - tempPayrollId: Must be a valid temp_payroll_id from Employee table
 * - remarks: Reason for sending back to campus (required, max 250 characters)
 * 
 * Optional Fields:
 * - checkListIds: Optional (comma-separated string like "1,2,3,4,5,6,7")
 * This captures the same details as forward to central office for tracking
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BackToCampusDTO {

	private String tempPayrollId; // REQUIRED - To find employee by temp_payroll_id

	private String remarks; // REQUIRED - Reason for sending back to campus (varchar(250))

	// Optional - Capture checklist details similar to forward to central office
	private String checkListIds; // Optional - Comma-separated checklist IDs

	private Integer UpdatedBy;

}
