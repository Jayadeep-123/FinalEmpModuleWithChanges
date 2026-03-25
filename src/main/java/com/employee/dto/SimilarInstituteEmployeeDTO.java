package com.employee.dto;
 
import lombok.AllArgsConstructor;
import lombok.Data;
 
@Data
@AllArgsConstructor
public class SimilarInstituteEmployeeDTO {
 
    private String empPayrollId;
    private String fullName;
    private long primaryContactNo;
    private String email;
    private String designationName;
}