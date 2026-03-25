package com.employee.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="sce_marital_status", schema="sce_employee")
public class MaritalStatus {
	
	
	@Id
	private int marital_status_id;
	
	private String marital_status_type;
	@Column(name = "is_active")
    private Integer isActive;
	

}
