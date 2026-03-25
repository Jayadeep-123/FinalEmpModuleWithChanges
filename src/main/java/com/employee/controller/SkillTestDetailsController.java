package com.employee.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.employee.dto.SkillTestDetailsDto;
import com.employee.dto.SkillTestDetailsRequestDto;
import com.employee.dto.SkillTestDetailsResultDto;
import com.employee.dto.SkillTestResultDTO;

import com.employee.service.SkillTestDetailsService;
import com.employee.entity.SkillTestApproval;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
// Base path for all methods in this controller
@RequestMapping("/api/v1/skill-details")
// Allows requests from any origin (e.g., your React frontend)
@CrossOrigin("*")
public class SkillTestDetailsController {

    @Autowired
    private SkillTestDetailsService skillTestDetailsService;

    @PostMapping("/save/{emp_id}")
    public ResponseEntity<SkillTestDetailsDto> saveSkillTestDetails(
            @Valid @RequestBody SkillTestDetailsRequestDto requestDto,
            @PathVariable("emp_id") int emp_id) {

        log.info("Attempting to save skill test details for emp_id: {}", emp_id);

        // Call the service method (returns Response DTO to avoid Hibernate lazy loading
        // issues)
        SkillTestDetailsDto savedDetails = skillTestDetailsService.saveSkillTestDetails(requestDto, emp_id);

        // Return the saved Response DTO (JSON) with a "201 Created" HTTP status
        return new ResponseEntity<>(savedDetails, HttpStatus.CREATED);
    }

    /**
     * Get skill test details by tempPayrollId
     * GET /api/v1/skill-details/temp-payroll-id/{tempPayrollId}
     * 
     * @param tempPayrollId The temporary payroll ID
     * @return ResponseEntity with SkillTestDetailsDto containing all skill test
     *         details
     */
    @GetMapping("/temp-payroll-id/{tempPayrollId}")
    public ResponseEntity<SkillTestDetailsDto> getSkillTestDetailsByTempPayrollId(
            @PathVariable("tempPayrollId") String tempPayrollId) {

        log.info("Fetching skill test details for tempPayrollId: {}", tempPayrollId);

        // Call the service method to get skill test details
        SkillTestDetailsDto skillTestDetails = skillTestDetailsService
                .getSkillTestDetailsByTempPayrollId(tempPayrollId);

        // Return the DTO with "200 OK" HTTP status
        return ResponseEntity.ok(skillTestDetails);

    }

    @GetMapping("/passed_employees/skilltestApprovalTable")
    public List<SkillTestDetailsResultDto> get_details_of_passed_employees(
            @org.springframework.web.bind.annotation.RequestParam(required = false) String campusCategory) {
        return skillTestDetailsService.get_details_of_passed_employees(campusCategory);
    }

    @GetMapping("/approval/{tempPayrollId}")
    public ResponseEntity<SkillTestApproval> getApprovalDetailsByTempPayrollId(
            @PathVariable("tempPayrollId") String tempPayrollId) {
        log.info("Fetching approval details for tempPayrollId: {}", tempPayrollId);
        SkillTestApproval approvalDetails = skillTestDetailsService.getApprovalDetailsByTempPayrollId(tempPayrollId);
        return ResponseEntity.ok(approvalDetails);
    }

    @GetMapping("/results/{tempPayrollId}")
    public ResponseEntity<List<SkillTestResultDTO>> getSkillTestResultsByTempPayrollId(
            @PathVariable("tempPayrollId") String tempPayrollId) {
        log.info("Fetching skill test results for tempPayrollId: {}", tempPayrollId);
        List<SkillTestResultDTO> results = skillTestDetailsService
                .getSkillTestResultsByPayrollId(tempPayrollId);
        return ResponseEntity.ok(results);
    }

    /**
     * Get list of all active skill test details with simplified fields
     * GET /api/v1/skill-details/list
     */
    @GetMapping("/list")
    public ResponseEntity<List<com.employee.dto.SkillTestListDto>> getAllSkillTestList(
            @org.springframework.web.bind.annotation.RequestParam(required = false) String campusCategory) {
        log.info("Fetching all skill test list with campusCategory: {}", campusCategory);
        List<com.employee.dto.SkillTestListDto> list = skillTestDetailsService.getSkillTestList(campusCategory);
        return ResponseEntity.ok(list);
    }

    @PutMapping("/approvedorNot/{tempPayrollId}")
    public ResponseEntity<String> approveSkillTest(@PathVariable("tempPayrollId") String tempPayrollId,
            @org.springframework.web.bind.annotation.RequestParam(required = false) Integer updatedBy) {
        log.info("Approving skill test for tempPayrollId: {} by user: {}", tempPayrollId, updatedBy);
        skillTestDetailsService.approveSkillTestResult(tempPayrollId, updatedBy);
        return ResponseEntity.ok("Skill Test Approved successfully");
    }

    @PutMapping("/reject/{tempPayrollId}")
    public ResponseEntity<String> rejectSkillTest(@PathVariable("tempPayrollId") String tempPayrollId,
            @org.springframework.web.bind.annotation.RequestParam(required = false) Integer updatedBy) {
        log.info("Rejecting skill test for tempPayrollId: {} by user: {}", tempPayrollId, updatedBy);
        skillTestDetailsService.rejectSkillTestResult(tempPayrollId, updatedBy);
        return ResponseEntity.ok("Skill Test Rejected successfully");
    }
}