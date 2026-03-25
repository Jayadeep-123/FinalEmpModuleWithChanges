package com.employee.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "sce_org_bank_branch" , schema = "sce_campus")
public class OrgBankBranch {
	
	@Id
	@Column(name = "org_bank_branch_id")
	private int org_bank_branch_id;
	
	@Column(name = "branch_name")
	private String branch_name;
	
	@Column(name = "is_active")
	private Integer isActive;

}