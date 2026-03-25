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
@Table(name="sce_join_type", schema="sce_employee")

public class JoiningAs {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int join_type_id;
	private String join_type;
	@Column(name = "is_active")
    private Integer isActive;
	


}
