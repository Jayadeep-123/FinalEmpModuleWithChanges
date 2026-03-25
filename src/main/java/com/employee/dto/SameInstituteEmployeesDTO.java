package com.employee.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for employees with same institute and qualification
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SameInstituteEmployeesDTO {
    
    private String institute;
    private Integer qualificationId;
    private String qualificationName;
    private List<EmployeeInfo> employees;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EmployeeInfo {
        private Integer empId;
        private String firstName;
        private String lastName;
        private String payrollId;
        private String tempPayrollId;
        private String email;
        private Long primaryMobileNo;
        private Integer designationId;
        private String designationName;
    }
}

