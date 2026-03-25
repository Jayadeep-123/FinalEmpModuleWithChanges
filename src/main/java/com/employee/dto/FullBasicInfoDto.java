package com.employee.dto;

import java.sql.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FullBasicInfoDto {

    // ===== Employee (sce_emp) =====
    private Integer empId;
    private String tempPayrollId;
    private String payrollId;

    private String firstName;
    private String lastName;
    private Date dateOfJoining;
    private Integer age;

    private Long primaryMobileNo;
    private Long secondaryMobileNo;
    private String email; // office email

    // IDs + Names
    private Integer genderId;
    private String genderName;

    private Integer empTypeId;
    private String empTypeName;

    private Integer departmentId;
    private String departmentName;

    private Integer designationId;
    private String designationName;

    private Integer categoryId;
    private String categoryName;

    private Integer qualificationId;
    private String qualificationName;

    private Integer workingModeId;
    private String workingModeName;

    private Integer campusId;
    private String campusName;
    private String campusCode;
    private String campusType;

    private Integer buildingId;
    private String buildingName;

    private Integer managerId;
    private String managerName;

    // ===== EmpDetails (sce_emp_detl) =====
    private String adhaarName;
    private Long adhaarNo;
    private String pancardNo;

    private Date dateOfBirth;

    private String personalEmail;
    private String emergencyPhoneNo;

    private String fatherName;
    private Long uanNo;

    private Integer bloodGroupId;
    private String bloodGroupName;

    private Integer casteId;
    private String casteName;

    private Integer religionId;
    private String religionName;

    private Integer maritalStatusId;
    private String maritalStatusName;

    private Integer relationId;
    private String relationName;

    private Integer modeOfHiringId;
    private String modeOfHiringName;

    private Integer joiningAsTypeId;
    private String joinType;

    // --- Newly Added Fields ---
    private String aadhaarEnrolmentNo;
    private Long sscNo;

    // Reference Employee
    private Integer referenceEmployeeId;
    private String referenceEmployeeName;

    // Hired By Employee (Recruiter/HR)
    private Integer hiredByEmployeeId;
    private String hiredByEmployeeName;

    // Reporting Manager
    private Integer reportingManagerId;
    private String reportingManagerName;

    // Replacement Employee
    private Integer replacementEmployeeId;
    private String replacementEmployeeName;

    private Double totalExperience;
    private Long preEsiNo;

    private Date contractStartDate;
    private Date contractEndDate;

    // Hiring Type Info
    private Integer empTypeHiringId;
    private String empTypeHiringName;
}