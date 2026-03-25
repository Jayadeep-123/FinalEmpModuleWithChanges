package com.employee.entity;

import java.sql.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "sce_building", schema = "sce_campus")
public class Building {
	@Id
	// @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "building_id")
	private int buildingId;

	@Column(name = "building_name")
	private String buildingName;

	// -------- Foreign Keys --------
	@ManyToOne
	@JoinColumn(name = "cmps_id")
	private Campus campusId;

	@ManyToOne
	@JoinColumn(name = "city_id")
	private City cityId;

	@ManyToOne
	@JoinColumn(name = "district_id")
	private District districtId;

	@ManyToOne
	@JoinColumn(name = "state_id")
	private State stateId;

	// @ManyToOne
	// @JoinColumn(name = "bldg_owner_id")
	// private BuildingOwner buildingOwnerId;

	// -------- Other Fields --------
	@Column(name = "country_id")
	private int countryId;

	@Column(name = "fusion_building_code")
	private Integer fusionBuildingCode;

	@Column(name = "purpose")
	private String purpose;


	@Column(name = "is_active")
	private int isActive;

	@Column(name = "created_by")
	private int createdBy;

	@Column(name = "is_main_building")
	private int isMainBuilding;

	@Column(name = "building_ip_no")
	private String buildingIpNo;

	@Column(name = "is_cellor")
	private int isCellor;

	@Column(name = "fusion_building")
	private String fusionBuilding;

	@Column(name = "building_img")
	private String buildingImg;

	@Column(name = "img_captured_date")
	private Date imgCapturedDate;

}
