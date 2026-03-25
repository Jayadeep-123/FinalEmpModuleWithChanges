package com.employee.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "sce_blood_group", schema = "sce_student")
public class BloodGroup {
	
	@Id
	@Column(name = "blood_group_id")
	private int bloodGroupId;
	
	@Column(name = "blood_group_name", length = 20)
	private String bloodGroupName;
	
	@Column(name = "is_active", nullable = false)
	private int isActive = 1;
	
	@Column(name = "created_by", nullable = false)
	private Integer createdBy = 1;
	
	@Column(name = "created_date", nullable = false)
	private LocalDateTime createdDate;
	
	@Column(name = "updated_by")
	private Integer updatedBy;
	
	@Column(name = "updated_date")
	private LocalDateTime updatedDate;
}
