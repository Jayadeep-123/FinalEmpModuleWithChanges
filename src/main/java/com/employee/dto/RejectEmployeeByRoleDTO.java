package com.employee.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for Reject Employee by Role request
 * Used to change employee status based on the role provided ("DO" or "CO")
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RejectEmployeeByRoleDTO {

    private String tempPayrollId; // REQUIRED
    private String remarks; // REQUIRED
    private String role; // REQUIRED - "DO" or "CO"
    private Integer updatedBy;
}
