package com.employee.dto;

import java.util.List;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for Educational Document Status Response
 * Shows which educational documents are uploaded and which are missing
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EducationalDocumentStatusDTO {

	private Integer empId;
	private String payrollId;
	private String tempPayrollId;
	private Integer qualificationId;
	private String qualificationName;

	// Required documents based on qualification
	private List<DocumentStatusDTO> requiredDocuments;

	// Documents that are uploaded
	private List<DocumentStatusDTO> uploadedDocuments;

	// Documents that are missing
	private List<DocumentStatusDTO> missingDocuments;

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class DocumentStatusDTO {
		private Integer docTypeId;
		private String docName;
		private String docType;
		private Boolean isUploaded;
		private String docPath; // If uploaded, show the path
		private Integer createdBy;
		private LocalDateTime createdDate;
		private Integer updatedBy;
		private LocalDateTime updatedDate;
	}
}
