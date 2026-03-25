package com.employee.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.employee.entity.Orientation;

@Repository
public interface OrientationRepository extends JpaRepository<Orientation, Integer> {

	// Find all active orientations (is_active = 1)
	@Query("SELECT o FROM Orientation o WHERE o.isActive = :isActive")
	List<Orientation> findByIsActive(@Param("isActive") Integer isActive);

}

