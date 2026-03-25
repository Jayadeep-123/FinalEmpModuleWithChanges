package com.employee.dto;

import java.sql.Date;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for Basic Info Tab (Step 1)
 * Maps to: Employee, EmpDetails, EmpPfDetails entities
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BasicInfoDTO {

	// Employee Entity fields
	private Integer empId; // Generated after save - will be included in response
	private Integer modeOfHiringId;
	private String firstName;
	private String lastName;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private Date dateOfJoin;
	private Long primaryMobileNo;
	private Long secondaryMobileNo;
	private String email;

	// Employee foreign key references (IDs) - All Integer types
	private Integer genderId;
	private Integer referenceEmpId; // Referred By
	private Integer hiredByEmpId; // Hired By
	// Note: designationId and departmentId are now handled in CategoryInfoDTO only
	// Removed from BasicInfoDTO to avoid conflicts - these should be set via
	// saveCategoryInfo API
	private Integer managerId; // Manager
	private Integer categoryId;
	private Integer reportingManagerId;
	private Integer empTypeId;
	private Integer empTypeHiringId;
	private Integer qualificationId; // Qualification ID - passed from BasicInfoDTO (not from qualification tab's
										// isHighest)
	private Integer empWorkModeId; // Working Mode
	private Integer replacedByEmpId; // Replacement Employee
	private Integer joinTypeId; // Joining As
	// Mode of Hiring

	// Contract dates - required when joinTypeId = 4 (Contract)
	private Date contractStartDate; // Contract start date
	private Date contractEndDate; // Contract end date

	// EmpDetails Entity fields
	private String adhaarName;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private Date dateOfBirth;

	@JsonAlias({ "aadhaarNum", "aadharNumber", "aadhaarNumber", "aadharNo", "aadhaarNo", "adhaarNo" })
	private Long adhaarNo;

	@JsonAlias({ "aadharEnrolmentNum", "aadhaarEnrolmentNum", "aadharEnrolmentNo", "aadhaarEnrolmentNo" })
	private String aadharEnrolmentNum; // Aadhaar Enrollment No
	private String pancardNum;

	private Integer bloodGroupId;
	private Integer casteId;
	private Integer religionId;
	private Integer maritalStatusId;
	private String emergencyPhNo; // Emergency contact phone number (REQUIRED NOT NULL)
	private Integer emergencyRelationId; // Emergency contact relation ID (FK to sce_stud_relation, optional)
	private Long sscNo; // SSC number - maps to Employee.ssc_no (Long type) - moved from EmpDocuments
	private Boolean sscNotAvailable;

	// EmpPfDetails Entity fields - Only previous UAN and previous ESI numbers are
	// stored at HR level
	@JsonAlias({ "preUanNo" })
	private Long preUanNum; // Previous UAN No (int8 - bigint) - accepts both "preUanNum" and "preUanNo"

	@JsonAlias({ "preEsiNo" })
	private Long preEsiNum; // Previous ESI No (int8 - bigint) - accepts both "preEsiNum" and "preEsiNo"

	// Note: PF Number, PF Join Date, UAN Number, and ESI Number are NOT stored at
	// HR level
	// private String pfNo;
	// private Date pfJoinDate;
	// private Long uanNo;
	// private Long esiNo;

	// Working Information
	private Integer campusId;
	private Integer buildingId; // Building ID (FK to sce_campus.sce_building)

	private Integer totalExperience; // Total Years of Experience

	// For profile picture upload (handle separately or as base64)
	private String profilePicture; // Can be base64 string or file path

	// Audit field - passed from frontend
	private Integer createdBy; // User ID who created the employee record
	private Integer updatedBy; // User ID who updated the employee record

	// Temp Payroll ID - for validation against SkillTestDetl table
	private String tempPayrollId; // Optional - validates against sce_skill_test_detl.temp_payroll_id

	// Previous Chaitanya Employee ID - if employee worked at Chaitanya before
	private String preChaitanyaId; // Optional - validates that employee exists with is_active = 0

	// Age field
	private Integer age;
	private String fatherName;
	private Long uanNo;

}
