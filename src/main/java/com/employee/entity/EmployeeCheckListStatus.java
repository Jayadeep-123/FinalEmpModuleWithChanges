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
@Table(name="sce_emp_app_status",schema="sce_employee")
public class EmployeeCheckListStatus{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "emp_app_status_id")
	private int emp_app_status_id;
	
	@Column(name = "check_app_status_name", nullable = false, length = 30)
	private String check_app_status_name;
	
	@Column(name = "is_active", nullable = false)
	private int is_active = 1;
	
	@Column(name = "created_by", nullable = false)
	private Integer created_by = 1;
	
	@Column(name = "created_date")
	private LocalDateTime created_date;
	
	@Column(name = "updated_by")
	private Integer updated_by;
	
	@Column(name = "updated_date")
	private LocalDateTime updated_date;

}
