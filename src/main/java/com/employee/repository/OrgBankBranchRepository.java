package com.employee.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.employee.entity.OrgBankBranch;

@Repository
public interface OrgBankBranchRepository extends JpaRepository<OrgBankBranch, Integer> {

	// Find all active bank branches (is_active = 1)
	@Query("SELECT b FROM OrgBankBranch b WHERE b.isActive = :isActive")
	List<OrgBankBranch> findByIsActive(@Param("isActive") Integer isActive);

	// Find all active bank branches for a specific bank_id through sce_org_bank_details
	@Query(value = "SELECT DISTINCT b.* FROM sce_campus.sce_org_bank_branch b " +
			"INNER JOIN sce_campus.sce_org_bank_details d ON b.org_bank_branch_id = d.org_bank_branch_id " +
			"WHERE d.org_bank_id = :bankId AND b.is_active = 1 AND d.is_active = 1", 
			nativeQuery = true)
	List<OrgBankBranch> findActiveBranchesByBankId(@Param("bankId") Integer bankId);

}

