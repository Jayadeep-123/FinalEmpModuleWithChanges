package com.employee.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.employee.entity.Occupation;

@Repository
public interface OccupationRepository extends JpaRepository<Occupation, Integer> {

	// Find all active occupations (is_active = 1)
	@Query("SELECT o FROM Occupation o WHERE o.isActive = :isActive")
	List<Occupation> findByIsActive(@Param("isActive") Integer isActive);

}

