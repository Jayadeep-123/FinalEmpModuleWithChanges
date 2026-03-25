package com.employee.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for creating/updating Skill Test Details
 * Contains only the fields that need to be sent from the frontend
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SkillTestDetailsRequestDto {

    // Personal Info
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
    private Integer groupId;
    private Integer cmpsId;
    private Integer cityId;
    private Integer buildingId;
    private Integer orientationId;

    // Audit fields
    private Integer createdBy; // User ID who created the skill test details record

}
