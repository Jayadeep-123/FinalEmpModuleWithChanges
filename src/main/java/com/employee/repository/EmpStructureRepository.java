
package com.employee.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.employee.entity.EmpStructure;

@Repository
public interface EmpStructureRepository extends JpaRepository<EmpStructure, Integer> {

	// Find by ID and is_active = 1
	@Query("SELECT es FROM EmpStructure es WHERE es.empStructureId = :id AND es.isActive = :isActive")
	Optional<EmpStructure> findByIdAndIsActive(@Param("id") Integer id, @Param("isActive") Integer isActive);

}
