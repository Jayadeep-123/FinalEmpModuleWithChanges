package com.employee.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.employee.entity.JoiningAs;

@Repository
public interface JoiningAsRepository extends JpaRepository<JoiningAs, Integer> {

	// Find by ID and is_active = 1
	@Query("SELECT ja FROM JoiningAs ja WHERE ja.join_type_id = :id AND ja.isActive = :isActive")
	Optional<JoiningAs> findByIdAndIsActive(@Param("id") Integer id, @Param("isActive") Integer isActive);
	
	
	List<JoiningAs> findByIsActive(int activeStatus);
}

