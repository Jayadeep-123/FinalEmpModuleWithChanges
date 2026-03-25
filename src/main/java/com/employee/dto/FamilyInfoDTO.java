
package com.employee.dto;

import java.sql.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for Family Info Tab (Step 3)
 * Maps to: EmpFamilyDetails entity (multiple records)
 * 
 * This DTO supports dynamic addition of family members.
 * - You can add any number of family members (Father, Mother, Spouse, Son,
 * Daughter, etc.)
 * - Each member is identified by their relationId
 * - All members use the same structure through FamilyMemberDTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FamilyInfoDTO {

	// Dynamic list of family members - can add any number (Father, Mother, Spouse,
	// Son, Daughter, etc.)
	// Each member must have a relationId to identify their relationship
	private List<FamilyMemberDTO> familyMembers;

	// Family Group Photo - uploaded file path/URL
	// This will be saved as a document with doc_type_id = 48 (Family Group Photo)
	private String familyGroupPhotoPath; // Optional - file path/URL after upload

	// Audit fields - passed from frontend
	private Integer createdBy; // User ID who created the employee record
	private Integer updatedBy; // User ID who updated the employee record

	// Single reusable DTO for all family members (since they have same fields)
	// The relationId distinguishes: Father, Mother, Spouse, Son, Daughter, etc.
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class FamilyMemberDTO {
		private String fullName;
		private Long adhaarNo;
		private Boolean isLate; // Late checkbox
		private Integer occupationId; // Optional - if provided, will look up occupation_name from Occupation table
		private String occupation; // Occupation name - used if occupationId is not provided or not found
		private Integer genderId; // NOT USED for Father/Mother (auto-set by backend: Father=Male, Mother=Female).
									// REQUIRED for other relations - foreign key to sce_gender
		private Integer bloodGroupId; // Required NOT NULL
		private String email;
		private String nationality; // Required NOT NULL
		private String phoneNumber;
		private Integer relationId; // Required NOT NULL - Identifies relationship: Father, Mother, Spouse, Son,
									// Daughter, etc.
		private Date dateOfBirth; // Optional - date of birth
		private Boolean isDependent; // Optional - is dependent (true/false)
		private Boolean isSriChaitanyaEmp; // Optional - is Sri Chaitanya employee (true/false), defaults to false
		private String parentEmpId; // Optional - parent payroll ID, REQUIRED if isSriChaitanyaEmp = true
	}
}
