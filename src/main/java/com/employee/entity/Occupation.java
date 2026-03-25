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
@Table(name="sce_occupation", schema="sce_student")
public class Occupation {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int occupation_id;
	private String occupation_name;
	private int occupation_category_id;
	private int occupation_sector_id;	
	@Column(name = "is_active")
    private Integer isActive; 

}
