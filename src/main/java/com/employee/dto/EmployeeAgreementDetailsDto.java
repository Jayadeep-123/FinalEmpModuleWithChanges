package com.employee.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeAgreementDetailsDto {

    // Agreement Info
    private String agreementCompany;
    private String agreementType;
    private String agreementPath;
    private Integer agreementDocId; // New field: ID of the agreement document record

    // Cheque Info
    private int noOfCheques;
    private List<ChequeDetailsDto> cheques;
}
