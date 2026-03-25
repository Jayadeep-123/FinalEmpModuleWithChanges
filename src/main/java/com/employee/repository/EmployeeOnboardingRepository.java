package com.employee.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.employee.entity.EmployeeOnboardingView;

@Repository
public interface EmployeeOnboardingRepository extends JpaRepository<EmployeeOnboardingView, Integer> {

    Optional<EmployeeOnboardingView> findByTempPayrollId(String tempPayrollId);
}