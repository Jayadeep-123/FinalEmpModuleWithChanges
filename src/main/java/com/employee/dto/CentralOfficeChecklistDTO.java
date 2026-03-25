package com.employee.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for Central Office Level Checklist Update
 * Used to update checklist IDs and notice period for an employee at Central
 * Office level
 *
 * Required Fields:
 * - tempPayrollId: Must be a valid temp_payroll_id from Employee table
 * - checkListIds: Comma-separated string of checklist IDs (e.g.,
 * "1,2,3,4,5,6,7")
 * All IDs will be validated against EmpAppCheckListDetl table before updating
 *
 * Optional Fields:
 * - noticePeriod: Notice period for the employee (nullable)
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CentralOfficeChecklistDTO {

    private String tempPayrollId; // REQUIRED - To find employee by temp_payroll_id

    private String checkListIds; // REQUIRED - Comma-separated checklist IDs (e.g., "1,2,3,4,5,6,7")

    private String noticePeriod; // Optional - Notice period (nullable)

    private Integer updatedBy;

}