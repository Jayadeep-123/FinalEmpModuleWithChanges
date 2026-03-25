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
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="sce_emp_level", schema="sce_employee")
public class EmployeeLevel {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int emp_level_id;
	private String level_name;
	@Column(name = "is_active")
    private Integer isActive; 


}
