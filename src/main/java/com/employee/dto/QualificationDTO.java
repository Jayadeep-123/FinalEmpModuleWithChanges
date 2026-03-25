package com.employee.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for Qualification Tab (Step 5)
 * Maps to: EmpQualification entity (multiple records - one per qualification)
 * Uses: Qualification, QualificationDegree entities for foreign keys
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QualificationDTO {
	
	private List<QualificationDetailsDTO> qualifications; // Can have multiple qualifications
	
	private Integer createdBy; // User ID who created the employee record
	private Integer updatedBy; // User ID who updated the employee record
	
	
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class QualificationDetailsDTO {
		private Integer qualificationId; // Qualification type ID - maps to Qualification.qualification_id
		private Integer qualificationDegreeId; // Degree ID - maps to QualificationDegree.qualification_degree_id
		private String specialization; // Maps to EmpQualification.specialization
		private String university; // Maps to EmpQualification.university
		private String institute; // Maps to EmpQualification.institute
		private Integer passedOutYear; // Maps to EmpQualification.passedout_year
		private String certificateFile; // Upload Certificate - can be base64 or file path (stored in EmpDocuments)
		// Note: isHighest flag removed - qualification_id is now set from BasicInfoDTO.qualificationId
	}
	
	
}

