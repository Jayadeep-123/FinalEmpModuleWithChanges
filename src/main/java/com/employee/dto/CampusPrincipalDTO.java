package com.employee.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CampusPrincipalDTO {

    private String principalName; // from CampusContact.empName
    private String designation;   // from CampusContact.designation
    private long contactNo;
    private String email;
    // Note: The image shows an EMP ID, but your CampusContact entity doesn't have it.
}