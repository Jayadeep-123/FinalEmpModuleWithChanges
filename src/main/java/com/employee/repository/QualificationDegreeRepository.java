package com.employee.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.employee.entity.QualificationDegree;

@Repository
public interface QualificationDegreeRepository extends JpaRepository<QualificationDegree, Integer> {
	
	@Query("SELECT qd FROM QualificationDegree qd " +
	           "WHERE qd.qualification_id.qualification_id = :qualificationId " +
	           "AND qd.is_active = :isActiveStatus")
	    List<QualificationDegree> findByQualification_QualificationIdAndIsActive(
	        @Param("qualificationId") Integer qualificationId, 
	        @Param("isActiveStatus") Integer isActiveStatus
	    );
}

