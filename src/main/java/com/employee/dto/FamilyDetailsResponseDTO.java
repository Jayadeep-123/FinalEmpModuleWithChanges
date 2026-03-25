package com.employee.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FamilyDetailsResponseDTO {

    // Family Member Details (from EmpFamilyDetails and related entities)
    private String name; // e.g., "Name of Father"
    private String fullName; // Added for POST consistency
    private String relation; // e.g., "Father", "Mother"
    private String bloodGroup; // e.g., "A-"
    private String nationality; // e.g., "Indian"
    private String occupation; // e.g., "IT Job"
    private String emailId; // e.g., "Design@varsitymgmt.com"
    private String email; // Added for POST consistency
    private Long phoneNumber; // e.g., +919876543210
    private Long adhaarNo;
    // Address Details (derived from EmpaddressInfo)
    private String state; // e.g., "Telangana"
    private Integer stateId;
    private String country; // e.g., "India"
    private Integer countryId;

    private Integer relationId;
    private Integer bloodGroupId;
    private Integer genderId; // Added

    // Additional fields
    private java.sql.Date dateOfBirth;
    private Boolean isLate; // Added
    private Boolean isDependent; // Added
    private Boolean isSriChaitanyaEmp; // Changed from Integer to Boolean
    private String parentEmpId; // Links to payroll_id
    private String parentEmpPayrollId;
}
