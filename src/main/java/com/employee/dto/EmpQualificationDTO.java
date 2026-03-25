package com.employee.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data 
@NoArgsConstructor
@AllArgsConstructor
public class EmpQualificationDTO {

    // These fields match the clean, flat JSON we want to send
    private String qualification;
    private String degree;
    private String specialisation; // 'specialization' in your entity
    private String university;
    private String institute;
    private Integer passedOutYear;
}
