package com.employee.dto;

import java.sql.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for Manager Mapping operation.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ManagerMappingResponseDTO {
    
    private boolean success;
    private String message;
    private int employeesUpdated;
    private Date workStartingDate;
}

