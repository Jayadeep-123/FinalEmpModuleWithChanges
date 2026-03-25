package com.employee.dto;

import java.sql.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for Unmapping request.
 * 
 * Uses boolean flags to control what gets unmapped:
 * - unmapManager: If true, removes manager assignment
 * - unmapReportingManager: If true, removes reporting manager assignment
 * 
 * Campus unmapping:
 * - campusId + subjectId: Only unmap that specific subject, campus stays active
 * - campusId only (no subjectId): Unmap the entire campus and all its subjects
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UnmappingDTO {

    private Integer cityId;

    private String payrollId;

    /**
     * If true, remove manager assignment
     * If false or null, preserve current manager assignment
     */
    private Boolean unmapManager = false;

    /**
     * Manager ID to verify before unmapping (optional)
     * If provided, backend will check if this ID matches the current manager before
     * unmapping
     */
    private Integer managerId;

    /**
     * If true, remove reporting manager assignment
     * If false or null, preserve current reporting manager assignment
     */
    private Boolean unmapReportingManager = false;

    /**
     * Reporting Manager ID to verify before unmapping (optional)
     * If provided, backend will check if this ID matches the current reporting
     * manager before unmapping
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
