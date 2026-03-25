package com.employee.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SkillTestListDto {
    private String employeeName;
    private String tempPayrollId;
    private String previousChaitanyaId;
    private String employeeNumber;
    private LocalDateTime joinDate;
    private String city;
    private String campus;
    private String gender;
    private Integer subjectId;
    private String subjectName;
    private String campusCategory;
}
