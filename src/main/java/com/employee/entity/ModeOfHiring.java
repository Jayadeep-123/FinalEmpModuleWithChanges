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
@Table(name="sce_mode_of_hiring", schema="sce_employee")
public class ModeOfHiring {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int mode_of_hiring_id;
	private String mode_of_hiring_name;
	
	@Column(name = "is_active")
    private Integer isActive;

	

}
