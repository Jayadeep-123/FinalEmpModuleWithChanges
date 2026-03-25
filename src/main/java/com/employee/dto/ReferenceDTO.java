package com.employee.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReferenceDTO {
    private String name;
    private String email;
    private long contact;
    private String designation;
    private String payrollId;

}