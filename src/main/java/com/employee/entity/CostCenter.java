package com.employee.entity;

import java.time.LocalDateTime;

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
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "sce_emp_costcenter", schema = "sce_employee")
public class CostCenter {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "cost_center_id")
	private Integer costCenterId;
	
	@Column(name = "cost_center_name", length = 50)
	private String costCenterName; // Optional - nullable (varchar(50))
	
	@Column(name = "cost_center_code", length = 20)
	private String costCenterCode; // Optional - nullable (varchar(20))
	
	@Column(name = "created_by", nullable = false)
	private Integer createdBy = 1; // Required NOT NULL
	
	@Column(name = "created_on")
	private LocalDateTime createdOn; // Optional - nullable (timestamp)
	
	@Column(name = "updated_by")
	private Integer updatedBy; // Optional - nullable
	
	@Column(name = "updated_date")
	private LocalDateTime updatedDate; // Optional - nullable
}
