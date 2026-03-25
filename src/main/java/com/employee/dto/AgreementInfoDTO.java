package com.employee.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for Agreements Tab (Step 9)
 * Maps to: Employee entity (agreement_org_id, agreement_type, is_check_submit)
 * EmpChequeDetails entity (multiple records - one per cheque)
 * 
 * Business Logic:
 * - Agreement info (agreement_org_id, agreement_type, is_check_submit) is
 * stored in Employee table
 * - Cheque details are stored in EmpChequeDetails table ONLY if isCheckSubmit =
 * 1 (true)
 * - If isCheckSubmit = 0 (false) or null, no cheque details are saved
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AgreementInfoDTO {

	// Agreement Information (stored in Employee table)
	private Integer agreementOrgId; // Agreement Company - FK to sce_campus.sce_organization
	private String agreementType; // Agreement Type - stored in Employee.agreement_type
	private String agreementPath; // New field: Path of the uploaded agreement document

	// Category passed by user (e.g., "school", "college") - used to determine
	// status change logic
	// If "school": status will NOT be changed
	// If "college" or any other: status will change from "Incompleted" to "Pending
	// at DO"
	private String category; // Category name (case-insensitive comparison)
	private String role; // Role/Designation name (case-insensitive comparison)

	// Is Check Submit flag (stored in Employee.is_check_submit)
	// If true: cheque details will be saved in EmpChequeDetails table and
	// is_check_submit = 1
	// If false or null: no cheque details are saved and is_check_submit = null
	private Boolean isCheckSubmit; // Checkbox value: true = checked, false = unchecked, null = not provided

	// Cheque Details (stored in EmpChequeDetails table - only if isCheckSubmit = 1)
	// Can have multiple cheques (1st Cheque, 2nd Cheque, etc.)
	private List<ChequeDetailDTO> chequeDetails;

	private Integer createdBy; // User ID who created the employee record
	private Integer updatedBy; // User ID who updated the employee record

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class ChequeDetailDTO {
		private Long chequeNo; // Cheque Number - int8 (bigint)
		private String chequeBankName; // Bank Name - varchar(50)
		private String chequeBankIfscCode; // IFSC Code - varchar(20)
		private String chequePath; // New field: Path of the uploaded cheque image/document
	}
}