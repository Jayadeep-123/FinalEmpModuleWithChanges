package com.employee.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "sce_campus_profile", schema = "sce_campus")
public class CampusProfileView {

    @Id
    @Column(name = "cmps_id")
    private int cmpsId;   // Primary Key - cannot be null

    @Column(name = "cmps_code")
    private String cmpsCode;

    @Column(name = "payroll_code")
    private Integer payrollCode;   // nullable

    @Column(name = "year_of_established")
    private Integer yearOfEstablished;   // nullable

    @Column(name = "cmps_name")
    private String cmpsName;

    @Column(name = "cmps_type")
    private String cmpsType;

    @Column(name = "org_id")
    private Integer orgId;   // nullable

    @Column(name = "org_name")
    private String orgName;

    @Column(name = "zone_id")
    private Integer zoneId;   // nullable

    @Column(name = "zone_name")
    private String zoneName;

    @Column(name = "educate_type_name")
    private String educateTypeName;

    @Column(name = "managed_by_id")
    private Integer managedById;   // nullable

    @Column(name = "campus_managed_by")
    private String campusManagedBy;

    @Column(name = "number_of_employees")
    private Integer numberOfEmployees;   // nullable

    @Column(name = "board_code")
    private String boardCode;

    @Column(name = "building_id")
    private Integer buildingId;   // nullable

    @Column(name = "mobile_no")
    private String mobileNo;

    @Column(name = "address_type")
    private String addressType;

    @Column(name = "plot_no")
    private String plotNo;

    @Column(name = "area")
    private String area;

    @Column(name = "street")
    private String street;

    @Column(name = "landmark")
    private String landmark;

    @Column(name = "pin_code")
    private Integer pinCode;   // nullable
}
