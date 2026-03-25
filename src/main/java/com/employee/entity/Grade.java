package com.employee.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="sce_emp_grade",schema="sce_employee")
public class Grade {
	
	@Id
	@Column(name="emp_grade_id")
	private int empGradeId;
	
	@Column(name="grade_name")
	private String gradeName;
	
	@Column(name="is_active")
	private int isActive;
	
	

}