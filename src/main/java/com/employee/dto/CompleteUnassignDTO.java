package com.employee.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for Complete Unassign request.
 * 
 * This endpoint will unassign ALL assignments from an employee:
 * - Manager
 * - Reporting Manager
 * - Primary Campus
 * - All Shared Campuses
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompleteUnassignDTO {

    /**
     * Employee payroll ID (required)
     */
    private String payrollId;

    /**
     * Optional: Reason for complete unassignment
     */
    private String remark;

    /**
     * User ID making the change (required for audit)
     */
    private Integer updatedBy;
}
