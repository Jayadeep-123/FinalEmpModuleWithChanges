package com.employee.dto;

import java.sql.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for Selective Bulk Unmapping request.
 * 
 * Uses boolean flags to control what gets unmapped:
 * - unmapManager: If true, removes manager assignment
 * - unmapReportingManager: If true, removes reporting manager assignment
 * 
 * Always use campusIds array:
 * - Single Campus: campusIds with 1 item
 * - Multiple Campuses: campusIds with 2+ items
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SelectiveBulkUnmappingDTO {

    private Integer cityId;

    private List<String> payrollIds;

    /**
     * If true, remove manager assignment
     */
    private Boolean unmapManager = false;

    /**
     * Manager ID to verify before unmapping (optional)
     */
    private Integer managerId;

    /**
     * If true, remove reporting manager assignment
     */
    private Boolean unmapReportingManager = false;

    /**
     * Reporting Manager ID to verify before unmapping (optional)
     */
    private Integer reportingManagerId;

    private Date lastDate;
    private String remark;
    private Integer updatedBy;

    // Fields for both input (what to unmap) and output (what was successfully
    // unmapped)
    private List<Integer> unmappedCampusIds;
    private List<Integer> unmappedSubjectIds;
}
