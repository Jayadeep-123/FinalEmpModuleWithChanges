package com.employee.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.employee.dto.AdvancedEmployeeSearchRequestDTO;
import com.employee.dto.EmployeeSearchRequestDTO;
import com.employee.dto.EmployeeSearchResponseDTO;
import com.employee.service.EmployeeSearchService;

/**
 * Controller for flexible employee search
 * Supports searching by cityId, employeeTypeId, and payrollId in various
 * combinations
 */
@RestController
@RequestMapping("/api/employee/search")
@CrossOrigin("*")
public class EmployeeSearchController {

    @Autowired
    private EmployeeSearchService employeeSearchService;

    /**
     * GET endpoint for flexible employee search (automatic pagination - max 50
     * records)
     *
     * Supports filter combinations (payrollId is REQUIRED):
     * - cityId + payrollId
     * - employeeTypeId + payrollId
     * - cityId + employeeTypeId + payrollId
     *
     * IMPORTANT: payrollId is REQUIRED for all searches. It must be combined with
     * at least one other filter (cityId or employeeTypeId).
     * Without payrollId, no data will be returned.
     *
     * NOTE: Pagination is handled automatically (first 50 records). No need to pass
     * page/size parameters.
     * This ensures performance even with 10+ lakh employees by limiting results.
     *
     * @param cityId         Optional - City ID filter (must be combined with
     *                       payrollId)
     * @param employeeTypeId Optional - Employee Type ID filter (must be combined
     *                       with payrollId)
     * @param payrollId      Required - Payroll ID or Employee Name filter (must be
     *                       provided with cityId or employeeTypeId)
     * @return ResponseEntity with List of EmployeeSearchResponseDTO containing:
     *         empId, empName, payRollId, departmentName, modeOfHiringName,
     *         tempPayrollId
     *         Maximum 50 records returned automatically
     */
    @GetMapping
    public ResponseEntity<?> searchEmployees(
            @RequestParam(value = "cityId", required = false) Integer cityId,
            @RequestParam(value = "employeeTypeId", required = false) Integer employeeTypeId,
            @RequestParam(value = "campusId", required = false) Integer campusId,
            @RequestParam(value = "cmpsCategory", required = false) String cmpsCategory,
            @RequestParam(value = "payrollId", required = true) String payrollId) {

        // Build search request DTO
        EmployeeSearchRequestDTO searchRequest = new EmployeeSearchRequestDTO();
        searchRequest.setCityId(cityId);
        searchRequest.setEmployeeTypeId(employeeTypeId);
        searchRequest.setCampusId(campusId);
        searchRequest.setPayrollId(payrollId);
        searchRequest.setCmpsCategory(cmpsCategory);

        // Automatic pagination: Always use first page, 2000 records, sorted by emp_id
        // ascending
        Pageable pageable = PageRequest.of(0, 2000, Sort.by(Sort.Direction.ASC, "emp_id"));

        // Perform search (validation is handled in service)
        List<EmployeeSearchResponseDTO> results = employeeSearchService.searchEmployees(searchRequest, pageable);

        return ResponseEntity.ok(results);
    }

    /**
     * GET endpoint for advanced employee search with multiple filters (automatic
     * pagination - max 50 records)
     *
     * Supports filter combinations (payrollId is REQUIRED):
     * - stateId + payrollId
     * - cityId + payrollId
     * - campusId + payrollId
     * - employeeTypeId + payrollId
     * - departmentId + payrollId
     * - Multiple combinations of the above filters (31 total combinations)
     *
     * IMPORTANT: payrollId is REQUIRED for all searches. It must be combined with
     * at least one other filter.
     * Without payrollId, no data will be returned.
     *
     * NOTE: Pagination is handled automatically (first 50 records). No need to pass
     * page/size parameters.
     * This ensures performance even with 10+ lakh employees by limiting results.
     *
     * @param stateId        Optional - State ID filter (must be combined with
     *                       payrollId)
     * @param cityId         Optional - City ID filter (must be combined with
     *                       payrollId)
     * @param campusId       Optional - Campus ID filter (must be combined with
     *                       payrollId)
     * @param employeeTypeId Optional - Employee Type ID filter (must be combined
     *                       with payrollId)
     * @param departmentId   Optional - Department ID filter (must be combined with
     *                       payrollId)
     * @param payrollId      Required - Payroll ID filter (must be provided with at
     *                       least one other filter)
     * @return ResponseEntity with List of EmployeeSearchResponseDTO containing:
     *         empId, empName, payRollId, departmentName, modeOfHiringName,
     *         tempPayrollId
     *         Maximum 50 records returned automatically
     */
    @GetMapping("/advanced")
    public ResponseEntity<?> advancedSearchEmployees(
            @RequestParam(value = "stateId", required = false) Integer stateId,
            @RequestParam(value = "cityId", required = false) Integer cityId,
            @RequestParam(value = "campusId", required = false) Integer campusId,
            @RequestParam(value = "employeeTypeId", required = false) Integer employeeTypeId,
            @RequestParam(value = "departmentId", required = false) Integer departmentId,
            @RequestParam(value = "cmpsCategory", required = false) String cmpsCategory,
            @RequestParam(value = "payrollId", required = false) String payrollId) {

        // Build advanced search request DTO
        AdvancedEmployeeSearchRequestDTO searchRequest = new AdvancedEmployeeSearchRequestDTO();
        searchRequest.setStateId(stateId);
        searchRequest.setCityId(cityId);
        searchRequest.setCampusId(campusId);
        searchRequest.setEmployeeTypeId(employeeTypeId);
        searchRequest.setDepartmentId(departmentId);
        searchRequest.setPayrollId(payrollId);
        searchRequest.setCmpsCategory(cmpsCategory);

        // Automatic pagination: Always use first page, 2000 records, sorted by emp_id
        // ascending
        Pageable pageable = PageRequest.of(0, 2000, Sort.by(Sort.Direction.ASC, "emp_id"));

        // Perform advanced search (validation is handled in service)
        List<EmployeeSearchResponseDTO> results = employeeSearchService.advancedSearchEmployees(searchRequest,
                pageable);

        return ResponseEntity.ok(results);
    }

    /**
     * GET endpoint for advanced list search (payrollId is optional and can be a
     * list)
     *
     * @param stateId        Optional
     * @param cityId         Optional
     * @param campusId       Optional
     * @param employeeTypeId Optional
     * @param departmentId   Optional
     * @param payrollId      Optional - Single or comma-separated payroll IDs
     * @return List of EmployeeSearchResponseDTO
     */
    @GetMapping("/advanced-list")
    public ResponseEntity<?> advancedListSearchEmployees(
            @RequestParam(value = "stateId", required = false) Integer stateId,
            @RequestParam(value = "cityId", required = false) Integer cityId,
            @RequestParam(value = "campusId", required = false) String campusId,
            @RequestParam(value = "employeeTypeId", required = false) Integer employeeTypeId,
            @RequestParam(value = "departmentId", required = false) Integer departmentId,
            @RequestParam(value = "cmpsCategory", required = false) String cmpsCategory,
            @RequestParam(value = "payrollId", required = false) String payrollId) {

        com.employee.dto.AdvancedEmployeeListSearchRequestDTO searchRequest = new com.employee.dto.AdvancedEmployeeListSearchRequestDTO();
        searchRequest.setStateId(stateId);
        searchRequest.setCityId(cityId);
        searchRequest.setCampusId(campusId);
        searchRequest.setEmployeeTypeId(employeeTypeId);
        searchRequest.setDepartmentId(departmentId);
        searchRequest.setPayrollId(payrollId);
        searchRequest.setCmpsCategory(cmpsCategory);

        Pageable pageable = PageRequest.of(0, 2000, Sort.by(Sort.Direction.ASC, "emp_id"));

        List<EmployeeSearchResponseDTO> results = employeeSearchService.advancedListSearchEmployees(searchRequest,
                pageable);

        return ResponseEntity.ok(results);
    }

    /**
     * GET endpoint for search list (payrollId is optional and can be a list)
     * 
     * @param cityId         Optional
     * @param employeeTypeId Optional
     * @param campusId       Optional
     * @param payrollId      Optional - Single/comma-separated Payroll ID or
     *                       Employee Name
     */
    @GetMapping("/list")
    public ResponseEntity<?> searchEmployeesList(
            @RequestParam(value = "cityId", required = false) Integer cityId,
            @RequestParam(value = "employeeTypeId", required = false) Integer employeeTypeId,
            @RequestParam(value = "campusId", required = false) Integer campusId,
            @RequestParam(value = "cmpsCategory", required = false) String cmpsCategory,
            @RequestParam(value = "payrollId", required = false) String payrollId) {

        EmployeeSearchRequestDTO searchRequest = new EmployeeSearchRequestDTO();
        searchRequest.setCityId(cityId);
        searchRequest.setEmployeeTypeId(employeeTypeId);
        searchRequest.setCampusId(campusId);
        searchRequest.setPayrollId(payrollId);
        searchRequest.setCmpsCategory(cmpsCategory);

        Pageable pageable = PageRequest.of(0, 2000, Sort.by(Sort.Direction.ASC, "emp_id"));

        List<EmployeeSearchResponseDTO> results = employeeSearchService.searchEmployeesList(searchRequest, pageable);

        return ResponseEntity.ok(results);
    }
}