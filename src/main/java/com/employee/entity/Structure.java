package com.employee.entity;

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
@Table(name="sce_emp_structure",schema="sce_employee")
public class Structure {
	@Id
	@Column(name="emp_structure_id")
	private int empStructureId;
	
	@Column(name="structure_name")
	private String structureName;
	
	@Column(name="is_active")
	private int isActive;
}