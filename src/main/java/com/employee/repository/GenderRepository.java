package com.employee.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.employee.entity.Gender;

@Repository
public interface GenderRepository extends JpaRepository<Gender, Integer>{

	// Find by ID and is_active = 1
	@Query("SELECT g FROM Gender g WHERE g.gender_id = :id AND g.isActive = :isActive")
	Optional<Gender> findByIdAndIsActive(@Param("id") Integer id, @Param("isActive") int isActive);

}
