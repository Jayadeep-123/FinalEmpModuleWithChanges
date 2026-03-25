package com.employee.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.employee.entity.EmpDocType;

@Repository
public interface EmpDocTypeRepository extends JpaRepository<EmpDocType, Integer> {
	
	
	// Find by document name
		@Query("SELECT edt FROM EmpDocType edt WHERE edt.doc_name = :docName")
		Optional<EmpDocType> findByDocName(@Param("docName") String docName);
		
		// Find by document name and is_active = 1
		@Query("SELECT edt FROM EmpDocType edt WHERE edt.doc_name = :docName AND edt.is_active = :isActive")
		Optional<EmpDocType> findByDocNameAndIsActive(@Param("docName") String docName, @Param("isActive") Integer isActive);
		
		@Query("SELECT edt FROM EmpDocType edt WHERE edt.doc_type = :docType AND edt.is_active = 1")
	    List<EmpDocType> findByDocTypeAndIsActive(@Param("docType") String docType);

		
		@Query("SELECT edt FROM EmpDocType edt WHERE edt.doc_type = :docType")
		List<EmpDocType> findByDocType(@Param("docType") String docType);
	
		// Find all active document types (is_active = 1)
		@Query("SELECT edt FROM EmpDocType edt WHERE edt.is_active = :isActive")
		List<EmpDocType> findByIsActive(@Param("isActive") Integer isActive);

}

