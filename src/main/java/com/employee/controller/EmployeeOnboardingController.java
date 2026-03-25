package com.employee.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.employee.dto.BasicInfoDTO;
import com.employee.dto.TempPayrollIdResponseDTO;
import com.employee.service.EmployeeOnboardingService;

@RestController
@RequestMapping("/api/employee/onboarding")
@CrossOrigin("*")
public class EmployeeOnboardingController {

	@Autowired
	private EmployeeOnboardingService employeeOnboardingService;

	/**
	 * POST endpoint to create NEW employee and generate/validate temp_payroll_id
	 * 
	 * This endpoint creates a NEW employee from BasicInfoDTO and generates/validates temp_payroll_id.
	 * The hrEmployeeId parameter is the HR Employee ID (the recruiter creating the new employee).
	 * 
	 * Flow:
	 * 1. If tempPayrollId is provided from frontend:
	 *    - Check if it exists in SkillTestDetails table
	 *    - If exists, use it (don't generate new one)
	 *    - If not exists in SkillTestDetails, throw error
	 * 
	 * 2. If tempPayrollId is NOT provided from frontend:
	 *    - Check aadharNum + phoneNumber in SkillTestDetails, Employee, and EmpDetails tables
	 *    - If found, cannot generate (employee already exists)
	 *    - If not found, generate new tempPayrollId
	 * 
	 * 3. Generation logic:
	 *    - Get campus code from BasicInfoDTO.campusId
	 *    - Format: TEMP{campusCode}{4-digit-number}
	 *    - Check max tempPayrollId in BOTH SkillTestDetails and Employee tables
	 *    - Generate next number (increment from max)
	 * 
	 * @param hrEmployeeId HR Employee ID (emp_id) - the recruiter creating the new employee (used for created_by)
	 * @param basicInfo BasicInfoDTO containing new employee details, campusId, aadharNum, primaryMobileNo, and optional tempPayrollId
	 * @return ResponseEntity with tempPayrollId, employeeId, message, and the posted BasicInfoDTO
	 */
	@PostMapping("/generate-temp-payroll-id/{hrEmployeeId}")
	public ResponseEntity<TempPayrollIdResponseDTO> generateOrValidateTempPayrollId(
			@PathVariable Integer hrEmployeeId,
			@RequestBody BasicInfoDTO basicInfo) {
		
		TempPayrollIdResponseDTO response = employeeOnboardingService.generateOrValidateTempPayrollId(hrEmployeeId, basicInfo);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

}

