package com.employee.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeCampusAddressDTO {
    private String payrollId;
    private String fullAddress;
    private String city;
    private String buildingMobileNo;
    private String employeeMobileNo;
    private String managerName;
    private String managerMobileNo;
    private String ReportingManagerName;
    private String reportingManagerMobileNo;
    private String campusName;
    private Integer cityId;

    private Integer managerId;
    private Integer campusId;
    private Integer reportingManagerId;

    private Integer departmentId;
    private String departmentName;
    private Integer designationId;
    private String designationName;
    private String employeeName;
    private String role; // Existing field, kept for backward compatibility if used elsewhere

    // New fields for internal processing / detailed response
    private Integer subjectId;
    private String subjectName;
    private Integer roleId;
    private String roleName;

    private Integer employeeTypeId;
    private String employeeTypeName;

    private String campusContact;
    private String campusEmail;
}
