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
@Table(name = "sce_orientation", schema = "sce_course")
public class Orientation {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "orientation_id")
	private Integer orientationId;
	
	@Column(name = "orientation_code")
	private String orientationCode;
	
	@Column(name = "orientation_name")
	private String orientationName;
	
	@Column(name = "acdc_id")
	private Integer acdcId;
	
	@Column(name = "class_id")
	private Integer classId;
	
	@Column(name = "group_id")
	private Integer groupId;
	
	@Column(name = "study_type_id")
	private Integer studyTypeId;
	
	@Column(name = "track_type_id")
	private Integer trackTypeId;
	
	@Column(name = "is_active")
	private Integer isActive;
	
	@Column(name = "created_date")
	private LocalDateTime createdDate;
	
	@Column(name = "created_by")
	private Integer createdBy;
	
	@Column(name = "updated_date")
	private LocalDateTime updatedDate;

}

