package com.employee.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "sce_shared_employee", schema = "sce_employee")
public class SharedEmployee {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "shared_employee_id")
	private Integer sharedEmployeeId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "emp_id", nullable = false)
	private Employee empId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cmps_id", nullable = false)
	private Campus cmpsId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "subject_id", nullable = true)
	private Subject subjectId; // Optional - can be null

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "designation_id", nullable = false)
	private Designation designationId;

	@Column(name = "is_active", nullable = false)
	private Integer isActive = 1;

	// Audit fields - required NOT NULL columns
	@Column(name = "created_by", nullable = false)
	private Integer createdBy = 1; // Default to 1 if not provided

	@Column(name = "created_date", nullable = false)
	private LocalDateTime createdDate;

	@Column(name = "updated_by")
	private Integer updatedBy;

	@Column(name = "updated_date")
	private LocalDateTime updatedDate;
}

