package com.employee.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FamilyMemberInOrgDTO {
    private String name;
    private String payrollId;
    private String email;
    private long contactNo;
    private String designation;
}