package com.employee.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeDropdownDTO {
    private int empId;
    private String empName; // Combination of first_name and last_name
    private String payrollId; // Added payroll ID
    private long mobileNo;
    private String email;
    private int designationId;
    private String designationName;
}