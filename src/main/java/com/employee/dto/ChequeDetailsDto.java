package com.employee.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChequeDetailsDto {
    private Long chequeNo;
    private String chequeBank;
    private String ifscCode;
    private String chequePath;
}