package com.employee.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for transferring campus contact details.
 * Contains only the fields needed for the contact list.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CampusContactDTO {

    private String empName;
    private String designation;
    private long contactNo;
    private String email;

}