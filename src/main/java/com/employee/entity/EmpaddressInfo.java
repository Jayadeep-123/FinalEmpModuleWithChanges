package com.employee.entity;

import java.time.LocalDateTime;

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
@Table(name = "sce_emp_addrs", schema = "sce_employee")
public class EmpaddressInfo {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int emp_addrs_id;

	private String addrs_type;
	private String house_no;
	private String landmark;
	private String postal_code;
	private Long emrg_contact_no;
	private int is_active;

	@ManyToOne
	@JoinColumn(name = "emp_id", nullable = false)
	private Employee emp_id; // Required NOT NULL - foreign key to sce_emp

	@ManyToOne
	@JoinColumn(name = "country_id", nullable = false)
	private Country country_id;

	@ManyToOne
	@JoinColumn(name = "state_id", nullable = false)
	private State state_id;

	@ManyToOne
	@JoinColumn(name = "city_id", nullable = false)
	private City city_id;

	@ManyToOne
	@JoinColumn(name = "district_id")
	private District district_id;

	// Audit fields - required NOT NULL columns
	@Column(name = "created_by", nullable = false)
	private Integer created_by = 1; // Default to 1 if not provided

	@Column(name = "created_date", nullable = false)
	private LocalDateTime created_date;

	@Column(name = "updated_by")
	private Integer updated_by;

	@Column(name = "updated_date")
	private LocalDateTime updated_date;

	@Column(name = "is_per_and_curr")
	private Integer is_per_and_curr;
}
