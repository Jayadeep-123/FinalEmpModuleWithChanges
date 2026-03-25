package com.employee.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for temp_payroll_id generation/validation
 * Used by: EmployeeController.generateOrValidateTempPayrollId()
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TempPayrollIdResponseDTO {
	private String tempPayrollId;
	private Integer employeeId;
	private String message;
	private BasicInfoDTO basicInfo; // Posted BasicInfoDTO object
}

