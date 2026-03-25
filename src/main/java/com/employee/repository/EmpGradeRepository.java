
package com.employee.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.employee.entity.EmpGrade;

@Repository
public interface EmpGradeRepository extends JpaRepository<EmpGrade, Integer> {

	// Find by ID and is_active = 1
	@Query("SELECT eg FROM EmpGrade eg WHERE eg.empGradeId = :id AND eg.isActive = :isActive")
	Optional<EmpGrade> findByIdAndIsActive(@Param("id") Integer id, @Param("isActive") Integer isActive);

}
