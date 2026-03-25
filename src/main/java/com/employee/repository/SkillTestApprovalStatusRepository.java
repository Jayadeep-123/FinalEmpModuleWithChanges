package com.employee.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.employee.entity.SkillTestApprovalStatus;

@Repository
public interface SkillTestApprovalStatusRepository extends JpaRepository<SkillTestApprovalStatus, Integer> {

    java.util.Optional<SkillTestApprovalStatus> findByStatusName(String statusName);

}
