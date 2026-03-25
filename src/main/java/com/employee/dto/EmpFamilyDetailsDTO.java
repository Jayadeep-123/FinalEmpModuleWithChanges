
package com.employee.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmpFamilyDetailsDTO {
    private int empFamilyDetlId;
    private String fullName;
    private Long adhaarNo;
    private String occupation;
    private String gender;
    private Integer genderId;
    private String bloodGroup;
    private Integer bloodGroupId;
    private String nationality;
    private String relation;
    private Integer relationId;
    private Integer isDependent;
    private String isLate;
    private String email;
    private Long contactNumber;

    // Additional fields from entity
    private java.sql.Date dateOfBirth;
    private Integer isSriChaitanyaEmp;
    private String parentEmpPayrollId;
    private String familyPhotoPath;
}
