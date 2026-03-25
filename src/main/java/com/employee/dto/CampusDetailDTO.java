package com.employee.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CampusDetailDTO {
    private Integer campusId;
    private String campusName;
    private Integer cityId;
    private String city;
    private String fullAddress;
    private String buildingMobileNo;

    private Integer subjectId;
    private String subjectName;
    private Integer designationId;
    private String designationName;
    private Integer roleId;
    private String roleName;

    private String campusContact;
    private String campusEmail;
}
