package com.employee.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO for save-remaining-tabs endpoint
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RemainingTabsResponseDTO {
	private String message;
	private Integer employeeId;
	private String tempPayrollId;
	private Integer addressCount;
	private Integer familyCount;
	private Integer experienceCount;
	private Integer qualificationCount;
	private Integer documentCount;
	private Integer bankCount;
	private RemainingTabsDTO remainingTabs; // Posted RemainingTabsDTO object
}

