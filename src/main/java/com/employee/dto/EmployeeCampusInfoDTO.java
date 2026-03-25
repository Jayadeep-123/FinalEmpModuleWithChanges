package com.employee.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeCampusInfoDTO {

    // --- Top Card: Current Campus Info ---
    private String campusName;
    private String campusCode;
    private String campusType;
    private String designationName; // From Employee.designation
    private String workMode;
    private String joiningAs;
    private String replacementEmployeeName; // Can be null

    // --- Middle Card: Principal Info (Replaces Manager) ---
    private CampusPrincipalDTO principalInfo;

    // --- Bottom Card: Campus Address ---
//    private CampusAddressDTO addressInfo;
}