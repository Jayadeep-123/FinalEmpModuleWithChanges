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
import jakarta.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "sce_department", schema = "sce_employee")
public class Department {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int department_id;
	private String department_name;
	@Column(name = "is_active")
	private Integer isActive;

	@ManyToOne
	@JoinColumn(name = "emp_type_id")
	private EmployeeType empTypeId;

	@ManyToOne
	@JoinColumn(name = "department_category_id")
	private BusinessType departmentCategory;

}
