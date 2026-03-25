package com.employee.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SkillTestDashboardDto {
    private String employeeName;
    private String employeeNumber;
    private String tempPayrollId;
    private String previousChaitanyaId;
    private LocalDate joinDate;
    private String city;
    private String campus;
    private String gender;
    private String status;
}
