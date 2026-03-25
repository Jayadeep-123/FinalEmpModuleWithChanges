package com.employee.dto;

import java.util.List;

import lombok.Data;

@Data
public class ExamResultDTO {
    private String payrollId;
    private String subject;
    private String examDate;
    private String totalMarks;
    private String totalQuestions;
    private String correct;
    private String wrong;
    private String attempted;
    private String unAttempted;

    // Audit fields
    private Integer createdBy;
    private Integer updatedBy;

    // Nested List for "levels"
    private List<ExamLevelDTO> levels;

    // Integer fields based on your JSON
    private Integer level1;
    private Integer level2;
    private Integer level3;
    private Integer level4;

    // Error fields from JSON
    private String error;
    private String error_description;
}