package com.employee.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for Forward to Central Office Response
 * Contains employee information, status details, and salary information after
 * forwarding
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ForwardToCentralOfficeResponseDTO {

    // Employee and Status Information
    private String tempPayrollId; // Temp Payroll ID
    private Integer empId; // Employee ID
    private String previousStatus; // Previous status (e.g., "Pending at DO")
    private String newStatus; // New status (e.g., "Pending at CO")
    private String message; // Success message

    // Salary Information (from SalaryInfoDTO)
    private Double monthlyTakeHome; // Monthly Take Home
    private String ctcWords; // CTC in words
    private Double yearlyCtc; // Yearly CTC
    private Integer empStructureId; // Employee Structure ID
    private Integer gradeId; // Grade ID
    private Integer costCenterId; // Cost Center ID
    private Integer orgId; // Organization/Company ID
    private Boolean isPfEligible; // PF Eligible flag
    private Boolean isEsiEligible; // ESI Eligible flag

    // PF/ESI/UAN Information
    private String pfNo; // PF Number
    private java.sql.Date pfJoinDate; // PF Join Date
    private Long esiNo; // ESI Number
    private Long uanNo; // UAN Number

    // Audit Information
    private Integer createdBy;
    private java.time.LocalDateTime createdDate;
    private Integer updatedBy;
}
