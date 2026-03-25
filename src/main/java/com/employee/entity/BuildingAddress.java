package com.employee.entity;
 
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
@Table(name="sce_building_address",schema="sce_campus")
public class BuildingAddress {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int building_address_id;
	
	private String address_type;
	
	@ManyToOne
	@JoinColumn(name = "building_id")
	private Building building;
 
	private String bied_code;
	
	private String mobile_no;
	
	private String plot_no;
	private String area;
	private String street;
	private String landmark;
	private Integer pin_code;
	private Integer latitude;
	private Integer longitude;
	
	
	@ManyToOne
	@JoinColumn(name = "city_id")
	private City city;
	
	@ManyToOne
	@JoinColumn(name = "state_id")
	private State state;
	
	@ManyToOne
	@JoinColumn(name = "zone_id")
	private Zone zone;
	
	@ManyToOne
	@JoinColumn(name = "district_id")
	private District  district;
	private String purpose;
	private Integer is_active;
 
}
 