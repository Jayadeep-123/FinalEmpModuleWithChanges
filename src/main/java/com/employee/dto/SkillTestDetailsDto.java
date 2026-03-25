package com.employee.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SkillTestDetailsDto {

    // Response fields (populated after save)
    private Integer skillTestDetlId;
    private String tempPayrollId;
    private LocalDateTime createdDate;

    // Personal Info
    // 💡 Replaced @Column with standard validation annotations
    private Long aadhaarNo; // Matches entity: aadhaar_no
    private String previousChaitanyaId; // Matches entity: previous_chaitanya_id
    private String firstName; // Corrected from 'name'
    private String lastName; // Corrected from 'surname'
    private LocalDate dob; // Corrected from 'dateOfBirth'

    private Long contactNumber;
    private String email;

    private Double totalExperience; // Changed from Long to Double to match entity (float8 in DDL)

    // --- Foreign Key IDs (These were already correct) ---
    private Integer genderId;
    private Integer qualificationId;
    private Integer joiningAsId;
    private Integer streamId;
    private Integer subjectId;
    private Integer emp_level_id;
    private Integer emp_structure_id;
    private Integer emp_grade_id;
    private Integer empTypeId;

    // --- Foreign Key Names (for display purposes) ---
    private String genderName;
    private String qualificationName;
    private String joiningAsName;
    private String streamName;
    private String subjectName;
    private String empLevelName;
    private String empStructureName;
    private String empGradeName;
    private String empTypeName;
    private Integer groupId;
    private Integer campusId;
    private Integer cityId;
    private Integer buildingId;
    private Integer orientationId;
    private String orientationName;

    // Audit fields
    private Integer createdBy; // User ID who created the skill test details record

    private String campusCategory;

}
