package com.employee.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.employee.entity.EmpDocuments;

@Repository
public interface EmpDocumentsRepository extends JpaRepository<EmpDocuments, Integer> {

        @Query("SELECT ed FROM EmpDocuments ed WHERE ed.emp_id.emp_id = :empId AND ed.is_active = 1")
        List<EmpDocuments> findByEmpIdAndIsActive(@Param("empId") Integer empId);

        // Find documents by emp_id and doc_type_id
        @Query("SELECT ed FROM EmpDocuments ed WHERE ed.emp_id.emp_id = :empId AND ed.emp_doc_type_id.doc_type_id = :docTypeId AND ed.is_active = 1")
        List<EmpDocuments> findByEmpIdAndDocTypeId(@Param("empId") Integer empId,
                        @Param("docTypeId") Integer docTypeId);

        // Find documents by emp_id and list of doc_type_ids
        @Query("SELECT ed FROM EmpDocuments ed WHERE ed.emp_id.emp_id = :empId AND ed.emp_doc_type_id.doc_type_id IN :docTypeIds AND ed.is_active = 1")
        List<EmpDocuments> findByEmpIdAndDocTypeIds(@Param("empId") Integer empId,
                        @Param("docTypeIds") List<Integer> docTypeIds);

        @Query("SELECT ed FROM EmpDocuments ed WHERE ed.emp_exp_detl_id.emp_exp_detl_id = :expId AND ed.emp_doc_type_id.doc_type_id = :docTypeId AND ed.doc_path = :docPath AND ed.is_active = 1")
        java.util.Optional<EmpDocuments> findExistingExperienceDoc(@Param("expId") Integer expId,
                        @Param("docTypeId") Integer docTypeId, @Param("docPath") String docPath);

        @Query("SELECT ed FROM EmpDocuments ed WHERE ed.emp_exp_detl_id.emp_exp_detl_id = :expId AND ed.is_active = 1")
        List<EmpDocuments> findByExperienceId(@Param("expId") int expId);

        @Query("SELECT ed FROM EmpDocuments ed WHERE ed.emp_id.emp_id = :empId AND ed.doc_path LIKE :pattern AND ed.is_active = 1")
        java.util.Optional<EmpDocuments> findByEmpIdAndPathPattern(@Param("empId") Integer empId,
                        @Param("pattern") String pattern);

        @Query("SELECT ed FROM EmpDocuments ed WHERE ed.emp_id.emp_id = :empId AND ed.emp_doc_type_id.doc_name = :docName AND ed.is_active = 1 ORDER BY ed.created_date DESC")
        List<EmpDocuments> findByEmpIdAndDocName(@Param("empId") Integer empId,
                        @Param("docName") String docName);

        @Query("SELECT ed FROM EmpDocuments ed WHERE ed.emp_id.emp_id = :empId AND ed.is_active = 1 AND ed.emp_exp_detl_id IS NULL AND ed.doc_path NOT LIKE 'CHEQUE_LINK_%' AND ed.doc_path NOT LIKE 'QUAL_LINK_%'")
        List<EmpDocuments> findGeneralDocumentsByEmpId(@Param("empId") Integer empId);
        
        
        @Query("SELECT ed FROM EmpDocuments ed WHERE ed.emp_id.emp_id = :empId AND ed.doc_path LIKE :pattern AND ed.is_active = 1")
        List<EmpDocuments> findMultipleByEmpIdAndPathPattern(@Param("empId") Integer empId,
                        @Param("pattern") String pattern);
}
