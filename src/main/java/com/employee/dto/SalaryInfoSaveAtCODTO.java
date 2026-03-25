package com.employee.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for Saving Salary Info at CO level (PUT request)
 * This specifically EXCLUDES:
 * - payrollId
 * - checkListIds
 * - orgId
 * 
 * This is used for "Save Draft" functionality at CO level.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SalaryInfoSaveAtCODTO {

    private String tempPayrollId; // REQUIRED
    private Integer orgId; // Optional - Organization/Company ID

    // Salary Information
    private Double monthlyTakeHome; // REQUIRED
    private String ctcWords; // Optional
    private Double yearlyCtc; // REQUIRED
    private Integer empStructureId; // REQUIRED
    private Integer gradeId; // Optional
    private Integer costCenterId; // Optional

    // Eligibility flags
    private Boolean isPfEligible; // REQUIRED
    private Boolean isEsiEligible; // REQUIRED

    // PF/ESI/UAN Information
    private String pfNo; // Optional
    private java.sql.Date pfJoinDate; // Optional
    private Long esiNo; // Optional
    private Long uanNo; // Optional

    private Integer updatedBy;
}
