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
@Table(name="sce_cmps_org",schema="sce_campus")
public class CmpsOrg {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="cmps_org_id")
	private int cmpsOrgId;
	
	@ManyToOne
	@JoinColumn(name="cmps_id")
	private Campus cmpsId;
	
	@ManyToOne
	@JoinColumn(name="org_id")
	private Organization orgId;
	
	@Column(name="is_active")
	private int isActive;
	
	
	

}