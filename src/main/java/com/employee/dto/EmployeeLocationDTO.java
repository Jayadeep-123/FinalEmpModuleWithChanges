package com.employee.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeLocationDTO {
    @com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
    private Integer campusId;
    private String campusName;
    private Integer stateId;
    private String stateName;
    private Integer cityId;
    private String cityName;
    private Integer buildingId;
    private String buildingName;
    private List<CampusInfoDTO> campusDetails;
}
