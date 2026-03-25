package com.employee.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for updating only employee checklists
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CheckListUpdateDTO {
    private String tempPayrollId; // To identify the employee
    private String checkListIds; // Comma-separated checklist IDs (e.g., "1,2,3,4,5,6,7")

    private Integer updatedBy;
}
