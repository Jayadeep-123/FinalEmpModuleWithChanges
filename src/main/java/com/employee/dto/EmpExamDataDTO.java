package com.employee.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data; // usage of Lombok is recommended for getters/setters

@Data
public class EmpExamDataDTO {
    private String tempId; // Changed from admNo
    private Long campusId;
    private String campusName;
    private Long cityId;
    private String cityName;
    private String dob;
    private String password;
    private String passwordDecrypt; // New Field
    private Long mobileNo;
    private String userName; // New Field
    private String name;
    private String surname;
    // buildingName removed as per new payload
    private String email;
    private String group;
    private String gender;
    private String status;
    private String studentType;
    private String subject;
    private String empLevel; // Changed from program
}