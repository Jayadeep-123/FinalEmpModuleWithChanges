package com.employee.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CmpsOrientationsDTO {

    // Primary key of the mapping table (Useful if you need to select/edit this specific record later)
    private Integer cmpsOrientationId;

    // The ID from the main Orientation table
    private Integer orientationId;

    // The Name of the Orientation (e.g., "Java Full Stack", "DevOps")
    private String orientationName;

}