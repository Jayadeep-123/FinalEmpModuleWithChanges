package com.employee.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeFullDetailsDTO {
    private int empId;
    private String empName; // first_name + last_name
    private String payrollId; // Added payroll ID
    private long mobileNo;
    private String email;

    // Details from related entities
    private String designationName;
    private String genderName;

    // Calculated/Secondary details
    private Integer age; // Calculated from EmpDetails
    private List<String> subjectsTaught; // From EmpSubject -> Subject
}