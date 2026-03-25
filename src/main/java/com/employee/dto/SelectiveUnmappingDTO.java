package com.employee.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for Selective Unmapping request.
 * 
 * Uses boolean flags to control what gets unmapped:
 * - unmapManager: If true, removes manager assignment
 * - unmapReportingManager: If true, removes reporting manager assignment
 * 
 * Frontend sends checkboxes as boolean values to indicate what should be
 * unmapped.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SelectiveUnmappingDTO {

    /**
     * Employee payroll ID (required)
     */
    private String payrollId;

    /**
     * If true, remove manager assignment (employee_manager_id = null)
     * If false, preserve current manager assignment
     */
    private Boolean unmapManager = false;

    /**
     * Manager ID to verify before unmapping (optional)
     * If provided, backend will check if this ID matches the current manager before
     * unmapping
     */
    private Integer managerId;

    /**
     * If true, remove reporting manager assignment (employee_reporting_id = null)
     * If false, preserve current reporting manager assignment
     */
    private Boolean unmapReportingManager = false;

    /**
     * Reporting Manager ID to verify before unmapping (optional)
     * If provided, backend will check if this ID matches the current reporting
     * manager before unmapping
     */
    private Integer reportingManagerId;

    /**
     * City ID (optional, for frontend use)
     */
    private Integer cityId;

    /**
     * List of campus IDs to unmap (optional)
     */
    private java.util.List<Integer> unmappedCampusIds;

    /**
     * Last working date (optional)
     */
    private java.time.LocalDate lastDate;

    /**
     * Optional: Reason for unmapping
     */
    private String remark;

    /**
     * User ID making the change (required for audit)
     */
    private Integer updatedBy;
}
