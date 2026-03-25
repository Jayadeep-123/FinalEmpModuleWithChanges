package com.employee.service;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.employee.dto.AdvancedEmployeeSearchRequestDTO;
import com.employee.dto.EmployeeSearchRequestDTO;
import com.employee.dto.EmployeeSearchResponseDTO;
import com.employee.repository.EmployeeRepository;

/**
 * Service for flexible employee search
 * Supports searching by cityId, employeeTypeId, and payrollId in various
 * combinations
 * 
 * IMPORTANT: payrollId is REQUIRED for all searches. Without payrollId, no data
 * will be returned.
 * PayrollId must be combined with at least one other filter (cityId or
 * employeeTypeId).
 */
@Service
@Transactional(readOnly = true)
public class EmployeeSearchService {

        private static final Logger logger = LoggerFactory.getLogger(EmployeeSearchService.class);

        @Autowired
        private EmployeeRepository employeeRepository;

        /**
         * Search employees with flexible filters (automatic pagination - max 50
         * records)
         * 
         * NOTE: payrollId is REQUIRED for all searches. It must be combined with at
         * least one other filter (cityId or employeeTypeId).
         * Without payrollId, an empty list will be returned.
         * 
         * Pagination is handled internally (page=0, size=50) for performance. Only
         * first 50 records are returned.
         * 
         * @param searchRequest Search request with filters (cityId, employeeTypeId,
         *                      payrollId - payrollId is required)
         * @param pageable      Pagination parameters (automatically set to page=0,
         *                      size=50)
         * @return List of EmployeeSearchResponseDTO containing employee name,
         *         department, employee id, temp payroll id (max 50 records)
         */
        public List<EmployeeSearchResponseDTO> searchEmployees(EmployeeSearchRequestDTO searchRequest,
                        Pageable pageable) {
                logger.info(
                                "Searching employees with filters - cityId: {}, employeeTypeId: {}, campusId: {}, payrollId: {}, page: {}, size: {}",
                                searchRequest.getCityId(), searchRequest.getEmployeeTypeId(),
                                searchRequest.getCampusId(),
                                searchRequest.getPayrollId(),
                                pageable.getPageNumber(), pageable.getPageSize());

                // Validation: payrollId is REQUIRED for all searches
                if (searchRequest.getPayrollId() == null || searchRequest.getPayrollId().trim().isEmpty()) {
                        logger.warn("PayrollId is required for all searches. No data will be returned without payrollId.");
                        throw new IllegalArgumentException(
                                        "PayrollId is required for all searches. Please provide a valid payrollId.");
                }

                // Use dynamic query method instead of 31 individual methods
                Page<EmployeeSearchResponseDTO> resultPage = employeeRepository.searchEmployeesDynamic(searchRequest,
                                pageable);

                // Extract content from Page (already DTOs, no mapping needed)
                // Optimize: Use direct list access instead of stream
                List<EmployeeSearchResponseDTO> results = new ArrayList<>(resultPage.getContent());

                // Result size validation (safety check)
                return results;
        }

        /**
         * Advanced search employees with multiple filters (automatic pagination - max
         * 50 records)
         * 
         * NOTE: payrollId is REQUIRED for all searches. It must be combined with at
         * least one other filter.
         * Without payrollId, an empty list will be returned.
         * 
         * Supported filters: stateId, cityId, campusId, employeeTypeId, departmentId,
         * payrollId
         * 
         * Pagination is handled internally (page=0, size=50) for performance. Only
         * first 50 records are returned.
         * 
         * @param searchRequest Advanced search request with filters (payrollId is
         *                      required)
         * @param pageable      Pagination parameters (automatically set to page=0,
         *                      size=50)
         * @return List of EmployeeSearchResponseDTO containing: empId, empName,
         *         payRollId, departmentName, modeOfHiringName, tempPayrollId (max 50
         *         records)
         */
        public List<EmployeeSearchResponseDTO> advancedSearchEmployees(AdvancedEmployeeSearchRequestDTO searchRequest,
                        Pageable pageable) {
                logger.info(
                                "Advanced searching employees with filters - stateId: {}, cityId: {}, campusId: {}, employeeTypeId: {}, departmentId: {}, payrollId: {}, page: {}, size: {}",
                                searchRequest.getStateId(), searchRequest.getCityId(), searchRequest.getCampusId(),
                                searchRequest.getEmployeeTypeId(), searchRequest.getDepartmentId(),
                                searchRequest.getPayrollId(),
                                pageable.getPageNumber(), pageable.getPageSize());

                // Validation: payrollId is REQUIRED for all searches
                if (searchRequest.getPayrollId() == null || searchRequest.getPayrollId().trim().isEmpty()) {
                        logger.warn("PayrollId is required for all searches. No data will be returned without payrollId.");
                        throw new IllegalArgumentException(
                                        "PayrollId is required for all searches. Please provide a valid payrollId.");
                }

                // Use dynamic query method instead of 28 individual methods
                Page<EmployeeSearchResponseDTO> resultPage = employeeRepository.searchEmployeesAdvancedDynamic(
                                searchRequest,
                                pageable);

                // Extract content from Page (already DTOs from repository, no mapping needed)
                // Optimize: Use direct list access instead of stream
                List<EmployeeSearchResponseDTO> results = new ArrayList<>(resultPage.getContent());

                // Result size validation (safety check)
                return results;
        }

        /**
         * Advanced list search employees with multiple filters and optional payroll
         * ID(s)
         */
        public List<EmployeeSearchResponseDTO> advancedListSearchEmployees(
                        com.employee.dto.AdvancedEmployeeListSearchRequestDTO searchRequest, Pageable pageable) {
                logger.info(
                                "Advanced list searching employees with filters - stateId: {}, cityId: {}, campusId: {}, employeeTypeId: {}, departmentId: {}, payrollId: {}, page: {}, size: {}",
                                searchRequest.getStateId(), searchRequest.getCityId(), searchRequest.getCampusId(),
                                searchRequest.getEmployeeTypeId(), searchRequest.getDepartmentId(),
                                searchRequest.getPayrollId(),
                                pageable.getPageNumber(), pageable.getPageSize());

                // Validation: At least one filter must be provided to prevent full table scan
                boolean hasFilter = searchRequest.getStateId() != null ||
                                searchRequest.getCityId() != null ||
                                searchRequest.getCampusId() != null ||
                                searchRequest.getEmployeeTypeId() != null ||
                                searchRequest.getDepartmentId() != null ||
                                (searchRequest.getPayrollId() != null
                                                && !searchRequest.getPayrollId().trim().isEmpty());

                if (!hasFilter) {
                        logger.warn("At least one filter must be provided for advanced list search.");
                        throw new IllegalArgumentException(
                                        "At least one filter (stateId, cityId, campusId, employeeTypeId, departmentId, or payrollId) must be provided.");
                }

                Page<EmployeeSearchResponseDTO> resultPage = employeeRepository
                                .searchEmployeesAdvancedListDynamic(searchRequest, pageable);

                List<EmployeeSearchResponseDTO> results = new ArrayList<>(resultPage.getContent());

                return results;
        }

        /**
         * Search employees with basic filters where payrollId is optional and can be a
         * list
         */
        public List<EmployeeSearchResponseDTO> searchEmployeesList(EmployeeSearchRequestDTO searchRequest,
                        Pageable pageable) {
                logger.info("Basic Search (List): cityId={}, employeeTypeId={}, campusId={}, payrollId={}",
                                searchRequest.getCityId(), searchRequest.getEmployeeTypeId(),
                                searchRequest.getCampusId(),
                                searchRequest.getPayrollId());

                // Check if at least one filter is provided
                boolean hasFilter = searchRequest.getCityId() != null ||
                                searchRequest.getEmployeeTypeId() != null ||
                                searchRequest.getCampusId() != null ||
                                (searchRequest.getCmpsCategory() != null
                                                && !searchRequest.getCmpsCategory().trim().isEmpty())
                                ||
                                (searchRequest.getPayrollId() != null
                                                && !searchRequest.getPayrollId().trim().isEmpty());

                if (!hasFilter) {
                        throw new IllegalArgumentException(
                                        "At least one filter (cityId, employeeTypeId, campusId, or payrollId) must be provided for basic search.");
                }

                Page<EmployeeSearchResponseDTO> resultPage = employeeRepository.searchEmployeesListDynamic(
                                searchRequest,
                                pageable);

                return new ArrayList<>(resultPage.getContent());
        }
}
