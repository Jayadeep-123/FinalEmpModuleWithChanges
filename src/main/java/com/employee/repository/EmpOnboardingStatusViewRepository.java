package com.employee.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.employee.entity.EmpOnboardingStatusView;

@Repository
public interface EmpOnboardingStatusViewRepository extends JpaRepository<EmpOnboardingStatusView, String> {

    @Query("SELECT DISTINCT e FROM EmpOnboardingStatusView e")
    List<EmpOnboardingStatusView> findAllDistinct();

    @Query("SELECT DISTINCT e FROM EmpOnboardingStatusView e WHERE UPPER(e.category_name) = UPPER(:categoryName)")
    List<EmpOnboardingStatusView> findByCategoryName(@Param("categoryName") String categoryName);
}