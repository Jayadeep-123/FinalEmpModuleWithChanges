package com.employee.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for Bulk Complete Unassignment request.
 * Clears all assignments (manager, reporting manager, primary campus, shared
 * campuses)
 * for multiple employees.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BulkCompleteUnassignDTO {

    /**
     * List of employee payroll IDs (required)
     */
    private List<String> payrollIds;

    /**
     * Optional: Reason for unassigning
     */
    private String remark;

    /**
     * User ID making the change (required for audit)
     */
    private Integer updatedBy;
}
