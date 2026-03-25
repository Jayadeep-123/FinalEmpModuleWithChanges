package com.employee.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.employee.dto.EmployeeSearchResponseDTO;
import com.employee.dto.AdvancedEmployeeSearchRequestDTO;
import com.employee.dto.EmployeeSearchRequestDTO;

public interface EmployeeRepositoryCustom {
    
    /**
     * Dynamic search method that handles all filter combinations
     * Replaces the 31 individual query methods with a single dynamic query
     */
    Page<EmployeeSearchResponseDTO> searchEmployeesDynamic(EmployeeSearchRequestDTO searchRequest, Pageable pageable);
    
    /**
     * Dynamic advanced search method that handles all filter combinations
     * Replaces the 28 individual query methods with a single dynamic query
     */
    Page<EmployeeSearchResponseDTO> searchEmployeesAdvancedDynamic(AdvancedEmployeeSearchRequestDTO searchRequest, Pageable pageable);

    /**
     * Dynamic advanced search method that handles a list of payroll IDs
     */
    Page<EmployeeSearchResponseDTO> searchEmployeesAdvancedListDynamic(com.employee.dto.AdvancedEmployeeListSearchRequestDTO searchRequest, Pageable pageable);

    /**
     * Dynamic search method for basic filters with optional payroll list
     */
    Page<EmployeeSearchResponseDTO> searchEmployeesListDynamic(EmployeeSearchRequestDTO searchRequest, Pageable pageable);
}

