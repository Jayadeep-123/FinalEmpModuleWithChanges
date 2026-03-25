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
@Table(name = "sce_cmps_contacts", schema = "sce_campus")
public class CampusContact {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cmps_contacts_id")
    private Integer cmpsContactsId;
	
	@ManyToOne
	@JoinColumn(name="cmps_id")
	private Campus cmpsId;
	
	@ManyToOne
	@JoinColumn(name="emp_id")
	private Employee empId;
	
	@Column(name="emp_name")
	private String empName;
	
	@Column(name="designation")
	private String designation;
	
	@Column(name="contact_no")
	private long contactNo;
	
	@Column(name="email")
	private String email;
	
	@Column(name="is_active")
	private int isActive;
	

}