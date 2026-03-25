package com.employee.dto;
 
import java.sql.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
 
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkingInfoDTO {
 
    // Employee entity fields
    private String tempPayrollId; // Key to fetch data
 
    // Campus related fields
    private String campusName;
    private String campusCode;
    private String campusType; // Mapped from Campus.cmps_type
    private String location; // Mapped from Campus.city.cityName (assuming location is city, or use Campus city/state details)
 
    // Building related field (Assuming Building entity is used to get the building name)
    private String buildingName;
 
    // Manager/Replacement/Hired By/Mode of Hiring fields
    private String managerName; // Mapped from Employee.employee_manager_id.first_name/last_name
    private String replacementEmployeeName; // Mapped from Employee.employee_replaceby_id.first_name/last_name
    private String hiredByName; // Mapped from Employee.employee_hired.first_name/last_name
    private String modeOfHiringType; // Mapped from ModeOfHiring.mode_of_hiring_type (Assuming this entity exists)
 
    // Working Mode/Joining As/Joining Date fields
    private String workingModeType; // Mapped from WoringMode.work_mode_type
    private String joiningAsType; // Mapped from JoiningAs.join_type
    private Date joiningDate; // Mapped from Employee.date_of_join
}
 
 