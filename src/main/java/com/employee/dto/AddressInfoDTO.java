package com.employee.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for Address Info Tab (Step 2)
 * Maps to: EmpaddressInfo entity (2 records - current & permanent)
 * 
 * Address Behavior:
 * - If permanentAddressSameAsCurrent = true:
 *   → Only currentAddress is required
 *   → permanentAddress (if provided) will be IGNORED
 *   → Both database records will use currentAddress data
 * 
 * - If permanentAddressSameAsCurrent = false or null:
 *   → Both currentAddress and permanentAddress can be provided
 *   → Each will be saved separately
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressInfoDTO {
	
	/**
	 * If true: permanent address = current address
	 * When true, permanentAddress field is IGNORED even if provided
	 */
	private Boolean permanentAddressSameAsCurrent;
	
	/**
	 * Current Address - Required
	 * Will be saved with addrs_type = "CURRENT"
	 */
	private AddressDTO currentAddress;
	
	/**
	 * Permanent Address - Optional
	 * 
	 * IMPORTANT:
	 * - If permanentAddressSameAsCurrent = true: This field is IGNORED
	 * - If permanentAddressSameAsCurrent = false/null: This field is used for permanent address
	 * - Can be omitted when permanentAddressSameAsCurrent = true
	 */
	private AddressDTO permanentAddress;
	
	// Audit fields - passed from frontend
	private Integer createdBy; // User ID who created the employee record
	private Integer updatedBy; // User ID who updated the employee record
	
	// Single reusable DTO for both addresses (since they have same fields)
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class AddressDTO {
		private String name;
		private String addressLine1;
		private String addressLine2;
		private String addressLine3;
		private String pin;
		private Integer cityId;
		private Integer stateId;
		private Integer countryId;
		private Integer districtId;
		private String phoneNumber; // Emergency contact number
	}
}

