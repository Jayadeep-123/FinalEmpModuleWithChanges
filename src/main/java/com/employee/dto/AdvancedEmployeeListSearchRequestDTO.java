package com.employee.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for Advanced Employee List Search Request
 * Supports flexible search with multiple filters and multiple payroll IDs
 *
 * NOTE: payrollIds is optional. If provided, filters by these IDs.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdvancedEmployeeListSearchRequestDTO {
    private Integer stateId; // Optional
    private Integer cityId; // Optional
    private String campusId; // Optional - can be ID or Name
    private Integer employeeTypeId; // Optional
    private Integer departmentId; // Optional
    private String payrollId; // Optional - can be a single ID or comma-separated IDs
    private String cmpsCategory; // Optional - filter by campus category (case-insensitive)
}
