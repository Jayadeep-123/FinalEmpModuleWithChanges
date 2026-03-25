package com.employee.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for Employee Search Request
 * Supports flexible search with optional filters:
 * - cityId + payrollId
 * - cityId + employeeTypeId + payrollId
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeSearchRequestDTO {
    private Integer cityId; // Optional - filter by city ID
    private Integer employeeTypeId; // Optional - filter by employee type ID
    private Integer campusId; // Optional - filter by campus ID
    private String payrollId; // Optional - filter by payroll ID
    private String cmpsCategory; // Optional - filter by campus category (case-insensitive)
}
