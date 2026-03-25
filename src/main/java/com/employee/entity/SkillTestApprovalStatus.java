package com.employee.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "sce_skill_test_approval_status", schema = "sce_employee")
public class SkillTestApprovalStatus {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "skill_test_approval_status_id")
	private Integer skillTestApprovalStatusId;

	@Column(name = "approval_status")
	private String statusName;

	@Column(name = "is_active", nullable = false)
	private Integer isActive = 1;
}
