package com.employee.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for Reject Back to DO request
 * Used when Central Office rejects an employee and sends back to Demand Officer
 * (DO)
 * 
 * Required Fields:
 * - tempPayrollId: Must be a valid temp_payroll_id from Employee table
 * - remarks: Reason for rejecting and sending back to DO (required, max 250
 * characters)
 * If remarks already exist, they will be updated (not replaced)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RejectBackToDODTO {

	private String tempPayrollId; // REQUIRED - To find employee by temp_payroll_id

	private String remarks; // REQUIRED - Reason for rejecting and sending back to DO (varchar(250))

	private Integer updatedBy;
}
