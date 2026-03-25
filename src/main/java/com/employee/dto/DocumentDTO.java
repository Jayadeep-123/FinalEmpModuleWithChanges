package com.employee.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for Upload Documents Tab (Step 6)
 * Maps to: EmpDocuments entity (multiple records - one per document)
 * Uses: EmpDocType entity for document type reference
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentDTO {
	
	private List<DocumentDetailsDTO> documents; // Can have multiple documents
	private Integer createdBy; // User ID who created the employee record
	private Integer updatedBy; // User ID who updated the employee record
	
	
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class DocumentDetailsDTO {
		private Integer docTypeId; // Document type ID - maps to EmpDocType.doc_type_id
		private String docPath; // File path or base64 string - maps to EmpDocuments.doc_path
		private Boolean isVerified; // Verification status - maps to EmpDocuments.is_verified (defaults to false)
	}
}

