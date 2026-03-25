package com.employee.dto;

import java.sql.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for Bulk Unmapping request.
 * 
 * Always use campusIds array:
 * - Single Campus: campusIds with 1 item
 * - Multiple Campuses: campusIds with 2+ items
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BulkUnmappingDTO {

    private Integer cityId;

    // Always use this array - even for single campus
    // Single campus: [ campusId1 ]
    // Multiple campuses: [ campusId1, campusId2, ... ]
    private List<Integer> campusIds;

    private List<String> payrollIds;
    private Integer managerId;
    private Integer reportingManagerId;
    private Date lastDate;
    private String remark;
    private Integer updatedBy;
}
