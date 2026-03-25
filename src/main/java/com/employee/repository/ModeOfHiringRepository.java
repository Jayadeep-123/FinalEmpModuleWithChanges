package com.employee.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.employee.entity.ModeOfHiring;

@Repository
public interface ModeOfHiringRepository extends JpaRepository<ModeOfHiring, Integer> {

	// Find by ID and is_active = 1
	@Query("SELECT moh FROM ModeOfHiring moh WHERE moh.mode_of_hiring_id = :id AND moh.isActive = :isActive")
	Optional<ModeOfHiring> findByIdAndIsActive(@Param("id") Integer id, @Param("isActive") Integer isActive);
	
	
	List<ModeOfHiring> findByIsActive(int activeStatus);
}

