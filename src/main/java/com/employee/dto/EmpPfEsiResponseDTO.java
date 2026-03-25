package com.employee.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmpPfEsiResponseDTO {
    private Long uanNo;
    private Long esiNo;
    private Long preEsiNo;
    private String pfNo;
}
