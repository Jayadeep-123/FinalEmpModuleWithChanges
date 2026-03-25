package com.employee.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.employee.entity.BloodGroup;

@Repository
public interface BloodGroupRepository extends JpaRepository<BloodGroup, Integer> {

	// Find by ID and is_active = 1
	@Query("SELECT bg FROM BloodGroup bg WHERE bg.bloodGroupId = :id AND bg.isActive = :isActive")
	Optional<BloodGroup> findByIdAndIsActive(@Param("id") Integer id, @Param("isActive") Integer isActive);

}

