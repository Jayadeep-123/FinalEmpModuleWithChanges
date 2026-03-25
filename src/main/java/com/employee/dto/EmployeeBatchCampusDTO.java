package com.employee.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
// @com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
public class EmployeeBatchCampusDTO {
  private String payrollId;
  private String employeeName;

  // Employee Mobile
  private String employeeMobileNo;

  // Manager Mobile
  private String managerMobileNo;

  // Reporting Manager Mobile
  private String reportingManagerMobileNo;

  // Primary Campus Info
  private Integer campusId;
  private String campusName;
  private Integer cityId;
  private String city;
  private String fullAddress;
  private String buildingMobileNo;
  private String campusContact;
  private String campusEmail;

  private List<CampusDetailDTO> campusDetails;

  // Additional Fields
  private String employeeType; // "Shared" or "Not Shared"
  private Integer managerId;
  private String managerName;
  private Integer reportingManagerId;
  private String reportingManagerName;
  private Integer departmentId;
  private String departmentName;
  private Integer designationId;
  private String designationName;

  private Integer roleId; // Role ID from sce_user_admin view
  // @com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.ALWAYS)
  private String role; // Role from sce_user_admin view

  private Integer subjectId;
  private String subjectName;
}
