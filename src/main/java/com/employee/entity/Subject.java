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
@Table(name = "sce_subject", schema = "sce_employee")
public class Subject {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "subject_id")
	private int subject_id;

	@Column(name = "subject_name", nullable = false)
	private String subject_name;

	@Column(name = "is_active", nullable = false)
	private Integer isActive = 1;

	@Column(name = "created_by", nullable = false)
	private Integer createdBy = 1;

	@Column(name = "created_date", nullable = false)
	private LocalDateTime createdDate = LocalDateTime.now();

	@Column(name = "updated_by")
	private Integer updatedBy;

	@Column(name = "updated_date")
	private LocalDateTime updatedDate;

	@Column(name = "class_id")
	private Integer classId;

	@Column(name = "emp_subject")
	private Integer empSubject;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "subject_category")
	private BusinessType subjectCategory;

}
