package com.employee.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Lightweight DTO for bank contact details (Bank Manager / CRO) fetched by payrollId.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BankContactDTO {
    private String accType; // PERSONAL / SALARY
    private String bankHolderName;

    private String bankManagerName;
    private Long bankManagerContactNo;
    private String bankManagerEmail;

    private String customerRelationshipOfficerName;
    private Long customerRelationshipOfficerContactNo;
    private String customerRelationshipOfficerEmail;
}


