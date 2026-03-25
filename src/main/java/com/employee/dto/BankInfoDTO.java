package com.employee.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for Bank Info Tab (Step 8) Maps to: BankDetails entity
 * Note: Can have two records - one for Personal Account, one for Salary Account
 * Uses: OrgBank entity for bank name dropdown
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BankInfoDTO {

    // General Bank Information
    private Integer paymentTypeId;

    // Salary Account Helper Fields
    private Integer bankId;
    private Integer bankBranchId;
    private String bankBranchName;

    // Personal Account Information
    private Boolean salaryLessThan40000;
    private PersonalAccountDTO personalAccount;

    // Salary Account Information
    private SalaryAccountDTO salaryAccount;

    private Integer createdBy;
    private Integer updatedBy;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PersonalAccountDTO {
        private String bankName;
        private String accountNo;
        private String accountHolderName;
        private String ifscCode;
        private String bankBranch; // Added bankBranch

        // --- New Fields (Personal Account) ---
        // private String bankManagerName;
        // private Long bankManagerContactNo;
        // private String bankManagerEmail;

        // private String customerRelationshipOfficerName;
        // private Long customerRelationshipOfficerContactNo;
        // private String customerRelationshipOfficerEmail;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SalaryAccountDTO {
        private Integer bankId;
        private String ifscCode;
        private String accountNo;
        private String accountHolderName;
        private String payableAt;

        // --- New Fields (Salary Account) ---
        private String bankManagerName;
        private Long bankManagerContactNo;
        private String bankManagerEmail;

        private String customerRelationshipOfficerName;
        private Long customerRelationshipOfficerContactNo;
        private String customerRelationshipOfficerEmail;
    }
}