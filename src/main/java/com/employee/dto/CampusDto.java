package com.employee.dto;
 
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
 
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CampusDto {
    private int campusId;
    private String campusName;
    private String campusType;
    private String campusCode;
    
 
    private int cityId;
    private String cityName;
 
    // New fields for main building
    private Integer buildingId;
    private String buildingName;
 
    public CampusDto(int campusId, String campusName, String campusType, String campusCode, int cityId, String cityName) {
        this.campusId = campusId;
        this.campusName = campusName;
        this.campusType = campusType;
        this.campusCode = campusCode;
        this.cityId = cityId;
        this.cityName = cityName;
    }
}
 