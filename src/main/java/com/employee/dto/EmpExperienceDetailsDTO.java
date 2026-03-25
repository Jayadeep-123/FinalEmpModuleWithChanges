package com.employee.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class EmpExperienceDetailsDTO {

    private String companyName; // Mapped from preOrzanigationName
    private String designation;
    private LocalDate fromDate; // Mapped from dateOfJoin
    private LocalDate toDate; // Mapped from dateOfLeave
    private String leavingReason;
    private String companyAddressLine1; // Mapped from company_addr to match POST DTO
    private String companyAddress; // Mapped from companyAddr
    private String natureOfDuties;

    // Map monthly salary directly or calculate from gross_salary
    private BigDecimal grossSalaryPerMonth;

    // Total CTC mapped from grossSalary
    private BigDecimal ctc;

    private java.util.List<PreviousEmployerInfoDTO.ExperienceDocumentDTO> documents; // Associated documents
}