package com.employee.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmpRecentSearchDTO {
    private Integer empRecentSearchId;
    private Integer logInEmpId;
    private Integer empId;
    private String empName;
    private String payrollId;
    private String tempPayrollId;
    private String departmentName;
    private String joinType;
    private String levelName;
    private LocalDateTime logIn;
    private LocalDateTime logOut;
    private String photoPath;
    private Integer createdBy;
    private LocalDateTime createdDate;
    private Integer updatedBy;
    private LocalDateTime updatedDate;
    private Integer cmpsId;
    private String cmpsName;
    private Integer businessName;
}
