package com.employee.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeRelationDTO {
    private String name;
    private String payrollId;
    private String email;
    private Long contactNo;
    private String designation;
}