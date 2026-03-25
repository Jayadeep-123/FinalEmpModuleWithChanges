package com.employee.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Campus assignment details for multiple campuses mapping.
 * Used in campusMappings array.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CampusMappingDTO {

	private Integer campusId;
	private Integer departmentId;
	private Integer subjectId;
	private Integer designationId;
}
