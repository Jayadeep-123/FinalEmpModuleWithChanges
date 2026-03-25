package com.employee.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QualificationDetailsDto {
    private Integer empQualificationId;
    private Integer qualificationId;
    private String qualificationName;
    private Integer qualificationDegreeId;
    private String qualificationDegree;
    private String specialization;
    private String institute;
    private String university;
    private Integer passedoutYear;
    private Integer isActive;
    private String certificatePath;
}