package com.employee.repository;

import java.util.List;

import com.employee.entity.EmployeeTypeHiring;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeTypeHiringRepository extends JpaRepository<EmployeeTypeHiring, Integer> {

    @Query("SELECT e FROM EmployeeTypeHiring e WHERE e.is_active = :isActive")
    List<EmployeeTypeHiring> findByIsActive(@Param("isActive") int isActive);
}
