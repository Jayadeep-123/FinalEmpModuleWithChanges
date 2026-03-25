package com.employee.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.employee.entity.OrgBank;

@Repository
public interface OrgBankRepository extends JpaRepository<OrgBank, Integer> {
	
	List<OrgBank> findByIsActive(Integer isActive);
}

