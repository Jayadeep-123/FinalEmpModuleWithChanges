package com.employee.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Main DTO for Employee Onboarding - All Tabs Combined
 * This is the single DTO that will be sent in ONE POST request
 * Contains all data from all onboarding tabs
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeOnboardingDTO {
	
	// Tab 1: Basic Info
	private BasicInfoDTO basicInfo;
	
	// Tab 2: Address Info
	private AddressInfoDTO addressInfo;
	
	// Tab 3: Family Info
	private FamilyInfoDTO familyInfo;
	
	// Tab 4: Previous Employer Info
	private PreviousEmployerInfoDTO previousEmployerInfo;
	
	// Tab 5: Qualification
	private QualificationDTO qualification;
	
	// Tab 6: Upload Documents
	private DocumentDTO documents;
	
	// Tab 7: Category Info
	private CategoryInfoDTO categoryInfo;
	
	// Tab 8: Bank Info
	private BankInfoDTO bankInfo;
	
	private AgreementInfoDTO agreementInfo;
	
	// Audit fields - passed from frontend
	private Integer createdBy; // User ID who created the employee record
	private Integer updatedBy; // User ID who updated the employee record
}

