package com.employee.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for Reject Employee request
 * Used to change employee status to "Reject" (ID 5)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RejectEmployeeDTO {

    private String tempPayrollId; // REQUIRED - To find employee by temp_payroll_id

    private String remarks; // REQUIRED - Reason for rejection

    private Integer updatedBy;
}
