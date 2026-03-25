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
@Table(name="sce_emp_type", schema="sce_employee")
public class EmployeeType {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int emp_type_id;
	private String emp_type;
	@Column(name = "is_active")
    private Integer isActive;
	


}
