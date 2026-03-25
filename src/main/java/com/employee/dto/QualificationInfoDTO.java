package com.employee.dto;
 
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
 
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QualificationInfoDTO {
    
    // Qualification fields
    private String qualification; // Mapped from Employee.qualification_id.qualification_name
    private String degree;        // Mapped from EmpQualification (requires another join, see service)
    private String specialisation; // Mapped from EmpQualification (requires another join, see service)
    private Integer passedOutYear; // Mapped from EmpQualification.passedout_year (requires another join, see service)
 
    // Academic Details - Sourced from the corresponding EmpQualification record
    private String university;    // Mapped from EmpQualification.university
    private String institute;     // Mapped from EmpQualification.institute
    
    // Other details
    private String certificateStatus; // Placeholder for certificate document presence
}