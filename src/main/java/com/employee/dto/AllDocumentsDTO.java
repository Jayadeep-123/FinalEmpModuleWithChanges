package com.employee.dto;
 
import java.util.List;
 
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
 
/**
 * DTO for All Documents Response
 * Returns all documents from EmpDocuments table for an employee
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AllDocumentsDTO {
   
    private Integer empId;
    private String payrollId;
    private String tempPayrollId;
   
    // All documents from EmpDocuments table
    private List<DocumentDetailsDTO> documents;
   
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DocumentDetailsDTO {
        private Integer empDocId; // emp_doc_id from EmpDocuments table
        private Integer docTypeId; // doc_type_id
        private String docName; // Document name from EmpDocType
        private String docType; // Document type/category from EmpDocType
        private String docPath; // Document path
        private Integer isVerified; // Verification status (0 or 1)
        private Integer isActive; // Active status (0 or 1)
    }
}
 