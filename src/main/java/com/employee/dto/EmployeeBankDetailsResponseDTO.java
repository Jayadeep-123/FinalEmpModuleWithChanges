package com.employee.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeBankDetailsResponseDTO {

    private BankInfoGetDTO personalBankInfo;
    private BankInfoGetDTO salaryAccountInfo;
}