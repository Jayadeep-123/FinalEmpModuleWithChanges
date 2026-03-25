package com.employee.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for Advanced Employee Search Request
 * Supports flexible search with multiple filters:
 * - stateId, cityId, campusId, employeeTypeId, departmentId, payrollId
 * 
 * IMPORTANT: payrollId is REQUIRED for all searches. It must be combined with
 * at least one other filter.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdvancedEmployeeSearchRequestDTO {
    private Integer stateId; // Optional - filter by state ID (through Campus -> State)
    private Integer cityId; // Optional - filter by city ID (through Campus -> City)
    private Integer campusId; // Optional - filter by campus ID
    private Integer employeeTypeId; // Optional - filter by employee type ID
    private Integer departmentId; // Optional - filter by department ID
    private String payrollId; // REQUIRED - filter by payroll ID
    private String cmpsCategory; // Optional - filter by campus category (case-insensitive)
}
