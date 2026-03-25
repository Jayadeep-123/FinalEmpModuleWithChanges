package com.employee.dto;

import java.sql.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SkillTestResultDTO {

    private String payrollId;
    private String fullName;
    private String subjectName;
    private Date examDate;
    private int noOfQuestions;
    private int attempted;
    private int unAttempted;
    private int wrong;
    private int totalMarks;
}