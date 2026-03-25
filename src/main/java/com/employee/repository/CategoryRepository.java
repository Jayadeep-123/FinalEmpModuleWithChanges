package com.employee.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.employee.entity.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {

	// Find by ID and is_active = 1
	@Query("SELECT c FROM Category c WHERE c.category_id = :id AND c.isActive = :isActive")
	Optional<Category> findByIdAndIsActive(@Param("id") Integer id, @Param("isActive") Integer isActive);

	// Find all active categories (is_active = 1)
	@Query("SELECT c FROM Category c WHERE c.isActive = :isActive")
	List<Category> findByIsActive(@Param("isActive") Integer isActive);

}

