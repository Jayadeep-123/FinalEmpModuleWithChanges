package com.employee.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for Remaining Tabs (After Basic Info is saved)
 * Contains all tabs EXCEPT basicInfo (which is already saved)
 * 
 * Used in Scenario 2: When tempPayrollId is generated in Employee table
 * After saving BasicInfoDTO, use this DTO to save remaining tabs
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RemainingTabsDTO {
	
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
	
	// Tab 9: Agreement Info
	private AgreementInfoDTO agreementInfo;
	
	// Audit fields - passed from frontend
	private Integer createdBy; // User ID who created the employee record
	private Integer updatedBy; // User ID who updated the employee record
	
	// NOTE: basicInfo is NOT included - it's already saved in Step 1
}

