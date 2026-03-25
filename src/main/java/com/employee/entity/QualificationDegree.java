package com.employee.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "sce_qualification_degree", schema = "sce_employee")
public class QualificationDegree {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "qualification_degree_id")
	private int qualification_degree_id;
	
	@Column(name = "degree_name", length = 150, nullable = false)
	private String degree_name;
	
	@ManyToOne
	@JoinColumn(name = "qualification_id", nullable = false)
	private Qualification qualification_id;
	
	@Column(name = "is_active", nullable = false)
	private Integer is_active;

	
	
	
}

