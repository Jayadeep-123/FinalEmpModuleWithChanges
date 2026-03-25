package com.employee.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.employee.dto.AddressResponseDTO;
import com.employee.dto.AllDocumentsDTO;
import com.employee.dto.CategoryInfoDTO1;
import com.employee.dto.EmpExperienceDetailsDTO;
import com.employee.dto.EmployeeAgreementDetailsDto;
import com.employee.dto.EmployeeBankDetailsResponseDTO;
import com.employee.dto.FamilyInfoResponseDTO;
import com.employee.dto.FullBasicInfoDto;
import com.employee.dto.ManagerDTO;
import com.employee.dto.QualificationDetailsDto;
import com.employee.dto.QualificationInfoDTO;
import com.employee.dto.ReferenceDTO;
import com.employee.dto.SameInstituteEmployeesDTO;
import com.employee.dto.WorkingInfoDTO;
import com.employee.entity.EmpOnboardingStatusView;
import com.employee.entity.EmployeeOnboardingView;
import com.employee.entity.SkillTestApproval;
import com.employee.service.GetEmpDetailsService;

import com.employee.dto.EmpPfEsiResponseDTO;

@RestController
@RequestMapping("/api/EmpDetailsFORCODO")
@CrossOrigin("*")
public class GetEmpDetailsController {

	@Autowired
	GetEmpDetailsService getEmpDetailsService;

	// @Autowired EmpExperienceDetailsRepository empExperienceDetailsRepo;

	// @GetMapping("/{empId}/family-details")
	// public List<EmpFamilyDetailsDTO> getFamilyDetails(@PathVariable int empId) {
	// return getEmpDetailsService.getFamilyDetailsByEmpId(empId);
	// }

	// @GetMapping("/EmpProfileView/{payrollId}") // 1. Change path variable name to
	// 'payrollId'
	// public ResponseEntity<?> getProfileByPayrollId(@PathVariable String
	// payrollId) { // 2. Change method name and path variable parameter
	//
	// // 3. Call the correct service method with the correct parameter
	// Optional<EmpProfileView> profile =
	// getEmpDetailsService.getProfileByPayrollId(payrollId);
	//
	// if (profile.isPresent()) {
	// return ResponseEntity.ok(profile.get());
	// } else {
	// // 4. Update the error message to show which ID was searched
	// return ResponseEntity.status(404).body("No employee found for PayrollId: " +
	// payrollId);
	// }
	// }

	@GetMapping("/EmployeeOnboardingProfileCardView/{tempPayrollId}")
	public ResponseEntity<?> getOnboardingByTempPayrollId(@PathVariable String tempPayrollId) {
		Optional<EmployeeOnboardingView> result = getEmpDetailsService
				.getEmployeeOnboardingByTempPayrollId(tempPayrollId);
		if (result.isPresent()) {
			return ResponseEntity.ok(result.get());
		} else {
			return ResponseEntity.status(404)
					.body("No onboarding details found for tempPayrollId: " + tempPayrollId);
		}
	}

	@GetMapping("/previousEmployeeInfo/{tempPayrollId}")
	public ResponseEntity<List<EmpExperienceDetailsDTO>> getEmployeeExperience(
			@PathVariable String tempPayrollId) {

		List<EmpExperienceDetailsDTO> dtoList = getEmpDetailsService.getExperienceByTempPayrollId(tempPayrollId);

		if (dtoList.isEmpty()) {
			return ResponseEntity.noContent().build();
		}

		return ResponseEntity.ok(dtoList);
	}

	@GetMapping("/category-info/{temppayrollId}")
	public ResponseEntity<List<CategoryInfoDTO1>> getCategoryInfo(
			@PathVariable String temppayrollId) {

		List<CategoryInfoDTO1> categoryInfoList = getEmpDetailsService.getCategoryInfo(temppayrollId);

		if (categoryInfoList.isEmpty()) {
			return ResponseEntity.noContent().build();
		}

		return ResponseEntity.ok(categoryInfoList);
	}

	@GetMapping("/EmpBankDetails/{tempPayrollId}")
	public ResponseEntity<?> getBankDetails(@PathVariable String tempPayrollId) {
		EmployeeBankDetailsResponseDTO response = getEmpDetailsService.getBankDetailsByTempPayrollId(tempPayrollId);

		if (response.getPersonalBankInfo() == null && response.getSalaryAccountInfo() == null) {
			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		}

		return ResponseEntity.ok(response);
	}

	@GetMapping("/managerDetails/{tempPayrollId}")
	public ResponseEntity<ManagerDTO> getManagerDetails(@PathVariable String tempPayrollId) {
		ManagerDTO manager = getEmpDetailsService.getManagerDetailsByTempPayrollId(tempPayrollId);
		return manager != null ? ResponseEntity.ok(manager) : ResponseEntity.notFound().build();
	}

	@GetMapping("/reference/{tempPayrollId}")
	public ResponseEntity<ReferenceDTO> getReferenceDetails(@PathVariable String tempPayrollId) {
		ReferenceDTO reference = getEmpDetailsService.getReferenceDetailsByTempPayrollId(tempPayrollId);
		return reference != null ? ResponseEntity.ok(reference) : ResponseEntity.notFound().build();
	}

	// @GetMapping("/skillTestResult/{tempPayrollId}")
	// public List<SkillTestResultDTO> getSkillTestResults(@PathVariable String
	// tempPayrollId) {
	// return getEmpDetailsService.getSkillTestResultsByPayrollId(tempPayrollId);
	// }

	@GetMapping("/agreement-cheque/{tempPayrollId}")
	public EmployeeAgreementDetailsDto getAgreementChequeInfo(@PathVariable String tempPayrollId) {
		return getEmpDetailsService.getAgreementChequeInfo(tempPayrollId);
	}

	@GetMapping("/approval-details/{tempEmployeeId}")
	public ResponseEntity<SkillTestApproval> getDetails(@PathVariable String tempEmployeeId) {

		Optional<SkillTestApproval> details = getEmpDetailsService.getSkillTestApprovalDetails(tempEmployeeId);

		// This is a clean way to return 200 OK if the data exists,
		// or 404 Not Found if it doesn't.
		return details.map(ResponseEntity::ok)
				.orElseGet(() -> ResponseEntity.notFound().build());
	}

	@GetMapping("/all-documents/{tempPayrollId}")
	public ResponseEntity<AllDocumentsDTO> getAllDocumentsByTempPayrollId(
			@PathVariable("tempPayrollId") String tempPayrollId) {
		AllDocumentsDTO result = getEmpDetailsService.getAllDocumentsByTempPayrollId(tempPayrollId);
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	@GetMapping("/working-info/{tempPayrollId}")
	public ResponseEntity<WorkingInfoDTO> getWorkingInfo(
			@PathVariable String tempPayrollId) {

		try {
			WorkingInfoDTO dto = getEmpDetailsService.getWorkingInfoByTempPayrollId(tempPayrollId);
			return ResponseEntity.ok(dto);
		} catch (RuntimeException e) {
			// Log the exception and return a 404 Not Found response
			// It's best practice to use custom exceptions and proper HTTP status codes.
			return ResponseEntity.notFound().build();
		}
	}

	@GetMapping("/Qualification-info/{tempPayrollId}")
	public ResponseEntity<QualificationInfoDTO> getHighestQualificationDetails(
			@PathVariable String tempPayrollId) {

		try {
			QualificationInfoDTO dto = getEmpDetailsService.getHighestQualificationDetails(tempPayrollId);
			return ResponseEntity.ok(dto);
		} catch (RuntimeException e) {
			// Log the exception and return a 404 Not Found response
			return ResponseEntity.notFound().build();
		}
	}

	@GetMapping("/family-info/{tempPayrollId}")
	public ResponseEntity<?> getEmployeeFamilyDetails(
			@PathVariable String tempPayrollId) {

		try {
			FamilyInfoResponseDTO response = getEmpDetailsService
					.getFamilyDetailsWithAddressInfo(tempPayrollId);

			if (response.getFamilyMembers() == null || response.getFamilyMembers().isEmpty()) {
				return new ResponseEntity<>("No family details found for employee with ID: " + tempPayrollId,
						HttpStatus.NOT_FOUND);
			}

			return new ResponseEntity<>(response, HttpStatus.OK);

		} catch (RuntimeException e) {
			// Handle Employee not found or other service-level exceptions
			return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
		} catch (Exception e) {
			// General internal server error handling
			return new ResponseEntity<>("An error occurred while fetching details.", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/status")
	public List<EmpOnboardingStatusView> getEmpStatus(
			@org.springframework.web.bind.annotation.RequestParam(required = false) String categoryName) {
		if (categoryName != null && !categoryName.isEmpty()) {
			return getEmpDetailsService.getEmpStatusByCategoryName(categoryName);
		}
		return getEmpDetailsService.getEmpStatus();
	}

	@GetMapping("/employee/basic-info/{tempPayrollId}")
	public FullBasicInfoDto getEmployeeDetails(
			@PathVariable String tempPayrollId) {
		return getEmpDetailsService.getEmployeeDetailsByTempPayrollId(tempPayrollId);
	}

	@GetMapping("/address/{tempPayrollId}")
	public Map<String, List<AddressResponseDTO>> getEmployeeAddress(
			@PathVariable String tempPayrollId) {

		return getEmpDetailsService.getEmployeeAddresses(tempPayrollId);
	}

	@GetMapping("/qualifications/{tempPayrollId}")
	public List<QualificationDetailsDto> getEmployeeQualifications(
			@PathVariable String tempPayrollId) {

		return getEmpDetailsService.getQualificationsByTempPayrollId(tempPayrollId);
	}

	@GetMapping("/payroll/{payrollId}")
	public ResponseEntity<List<EmpExperienceDetailsDTO>> getExperienceByPayrollId(
			@PathVariable String payrollId) {

		List<EmpExperienceDetailsDTO> experienceList = getEmpDetailsService.getExperienceByPayrollId(payrollId);

		if (experienceList.isEmpty()) {
			return ResponseEntity.noContent().build();
		}

		return ResponseEntity.ok(experienceList);
	}

	/**
	 * Get employees with same institute based on highest qualification
	 * 
	 * @param payrollId Payroll ID of the employee
	 * @return SameInstituteEmployeesDTO containing institute, qualification info,
	 *         and list of employees (minimum 4)
	 */
	@GetMapping("/same-institute/{payrollId}")
	public ResponseEntity<SameInstituteEmployeesDTO> getEmployeesWithSameInstitute(
			@PathVariable String payrollId) {
		try {
			SameInstituteEmployeesDTO result = getEmpDetailsService.getEmployeesWithSameInstitute(payrollId);
			return ResponseEntity.ok(result);
		} catch (RuntimeException e) {
			return ResponseEntity.notFound().build();
		}
	}

	@GetMapping("/pf-esi-details/{tempPayrollId}")
	public ResponseEntity<EmpPfEsiResponseDTO> getPfEsiDetails(@PathVariable String tempPayrollId) {
		EmpPfEsiResponseDTO response = getEmpDetailsService.getPfEsiDetailsByTempPayrollId(tempPayrollId);
		return ResponseEntity.ok(response);
	}

}
