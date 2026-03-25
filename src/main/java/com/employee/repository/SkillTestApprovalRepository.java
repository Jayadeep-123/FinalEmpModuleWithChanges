package com.employee.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.employee.entity.SkillTestApproval;

@Repository
public interface SkillTestApprovalRepository extends JpaRepository<SkillTestApproval, String> {

    Optional<SkillTestApproval> findByTempEmployeeId(String tempEmployeeId);

}