package com.employee.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for Incompleted Status request
 * Used to change employee status from "Reject" to "Incompleted"
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IncompletedStatusDTO {

    private String tempPayrollId; // REQUIRED - To find employee by temp_payroll_id

    private String remarks; // REQUIRED - Reason for marking as incompleted

    private Integer updatedBy;
}
