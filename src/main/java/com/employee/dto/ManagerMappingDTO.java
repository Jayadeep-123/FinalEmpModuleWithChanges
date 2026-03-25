package com.employee.dto;

import java.sql.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for Manager Mapping request.
 * 
 * Always use campusMappings array:
 * - Single Campus: campusMappings with 1 item
 * - Multiple Campuses: campusMappings with 2+ items
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ManagerMappingDTO {
    
    private Integer cityId;
    
    // Always use this array - even for single campus
    // Single campus: [ { campusId: X, departmentId: D, designationId: Y } ]
    // Multiple campuses: [ { campusId: X1, departmentId: D1, designationId: Y1 }, { campusId: X2, departmentId: D2, designationId: Y2 }, ... ]
    private List<CampusMappingDTO> campusMappings;
    
    private String payrollId;
    private Integer managerId;
    private Integer reportingManagerId;
    private Date workStartingDate;
    private String remark;
    private Integer updatedBy;
}

