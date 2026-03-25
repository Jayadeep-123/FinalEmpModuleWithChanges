package com.employee.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.employee.dto.BackToCampusDTO;
import com.employee.dto.CheckListUpdateDTO;
import com.employee.dto.ForwardToCentralOfficeResponseDTO;
import com.employee.dto.SalaryInfoDTO;
import com.employee.service.EmpSalaryInfoService;

@RestController
@RequestMapping("/api/employee/Do Controller")
@CrossOrigin("*")
public class EmpSalaryInfoController {

	@Autowired
	private EmpSalaryInfoService empSalaryInfoService;

	/**
	 * GET endpoint to retrieve salary info by temp_payroll_id
	 * Returns only DTO, not the full entity
	 */
	@GetMapping("/by-temp-payroll-id")
	public ResponseEntity<SalaryInfoDTO> getSalaryInfoByTempPayrollId(
			@RequestParam("tempPayrollId") String tempPayrollId) {
		SalaryInfoDTO salaryInfoDTO = empSalaryInfoService.getSalaryInfoByTempPayrollIdAsDTO(tempPayrollId);
		return new ResponseEntity<>(salaryInfoDTO, HttpStatus.OK);
	}

	/**
	 * GET endpoint to retrieve salary info by payroll_id
	 * Returns only DTO, not the full entity
	 */

	/**
	 * POST endpoint to forward employee to Central Office
	 * This is the main endpoint for salary info creation/update and forwarding to
	 * central office.
	 * Called when clicking "Forward to Central Office" button.
	 * 
	 * Flow:
	 * 1. Saves/updates salary info (EmpSalaryInfo)
	 * 2. Saves/updates PF/ESI/UAN details (EmpPfDetails)
	 * 3. Updates checklist IDs in Employee table
	 * 4. Updates org_id (Company/Organization) in Employee table (if provided)
	 * 5. Updates employee status to "Pending at CO" (when forwarding to Central
	 * Office)
	 * 6. Clears any previous remarks
	 * 
	 * @param salaryInfoDTO Contains tempPayrollId, salary info, checklist IDs, etc.
	 * @return ResponseEntity with the updated SalaryInfoDTO
	 */
	@PostMapping("/forward-to-central-office/{tempPayrollId}")
	public ResponseEntity<ForwardToCentralOfficeResponseDTO> forwardToCentralOffice(
			@RequestBody SalaryInfoDTO salaryInfoDTO,
			@PathVariable String tempPayrollId) {

		// Ensure tempPayrollId is set in the DTO
		if (tempPayrollId != null && !tempPayrollId.equals("{tempPayrollId}") && !tempPayrollId.trim().isEmpty()) {
			salaryInfoDTO.setTempPayrollId(tempPayrollId);
		} else {
			if (salaryInfoDTO.getTempPayrollId() == null || salaryInfoDTO.getTempPayrollId().trim().isEmpty()) {
				throw new com.employee.exception.ResourceNotFoundException(
						"tempPayrollId is required in either path or request body.");
			}
		}

		ForwardToCentralOfficeResponseDTO result = empSalaryInfoService.forwardToCentralOffice(salaryInfoDTO);
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	/**
	 * POST endpoint to save salary info without forwarding
	 */
	@PostMapping("/save-salary-info")
	public ResponseEntity<SalaryInfoDTO> saveSalaryInfo(@RequestBody SalaryInfoDTO salaryInfoDTO) {
		SalaryInfoDTO result = empSalaryInfoService.saveSalaryInfoOnly(salaryInfoDTO);
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	/**
	 * POST endpoint to send employee back to campus
	 * This endpoint is called when clicking "Back to Campus" button after entering
	 * remarks
	 * Sets emp_app_status_id to 1 and saves the remarks
	 * 
	 * @param backToCampusDTO Contains tempPayrollId, remarks (required), and
	 *                        optional checkListIds
	 * @return ResponseEntity with the updated BackToCampusDTO
	 */
	@PostMapping("/back-to-campus")
	public ResponseEntity<BackToCampusDTO> backToCampus(@RequestBody BackToCampusDTO backToCampusDTO) {
		BackToCampusDTO result = empSalaryInfoService.backToCampus(backToCampusDTO);
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	@PostMapping("/update-checklists")
	public ResponseEntity<CheckListUpdateDTO> updateChecklists(@RequestBody CheckListUpdateDTO dto) {
		CheckListUpdateDTO result = empSalaryInfoService.updateCheckListIds(dto);
		return new ResponseEntity<>(result, HttpStatus.OK);
	}
}
