package com.employee.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.employee.dto.CampusEmployeeDTO;
import com.employee.entity.Campus;
import com.employee.entity.CampusEmployee;
import com.employee.entity.Employee;
import com.employee.exception.ResourceNotFoundException;
import com.employee.repository.CampusEmployeeRepository;
import com.employee.repository.CampusRepository;
import com.employee.repository.EmployeeRepository;
import com.employee.repository.SharedEmployeeRepository;

@Service
public class CampusEmployeeService {

    private final CampusEmployeeRepository campusEmployeeRepository;
    private final EmployeeRepository employeeRepository;
    private final CampusRepository campusRepository;
    private final SharedEmployeeRepository sharedEmployeeRepository;

    public CampusEmployeeService(CampusEmployeeRepository campusEmployeeRepository,
            EmployeeRepository employeeRepository,
            CampusRepository campusRepository,
            SharedEmployeeRepository sharedEmployeeRepository) {
        this.campusEmployeeRepository = campusEmployeeRepository;
        this.employeeRepository = employeeRepository;
        this.campusRepository = campusRepository;
        this.sharedEmployeeRepository = sharedEmployeeRepository;
    }

    @Transactional
    public List<CampusEmployeeDTO> assignCampusesToEmployee(List<CampusEmployeeDTO> campusEmployeeDTOs) {
        if (campusEmployeeDTOs == null || campusEmployeeDTOs.isEmpty()) {
            throw new IllegalArgumentException("Campus assignment list cannot be empty");
        }

        List<CampusEmployeeDTO> savedDTOs = new ArrayList<>();

        for (CampusEmployeeDTO dto : campusEmployeeDTOs) {
            // Validate Employee by payrollId
            if (dto.getPayrollId() == null || dto.getPayrollId().trim().isEmpty()) {
                throw new IllegalArgumentException("Payroll ID is required");
            }

            Employee employee = employeeRepository.findByPayrollId(dto.getPayrollId())
                    .orElseGet(() -> employeeRepository.findByTempPayrollId(dto.getPayrollId())
                            .orElseThrow(() -> new ResourceNotFoundException(
                                    "Employee not found with Payroll ID: " + dto.getPayrollId())));

            // Validate Campus
            Campus campus = campusRepository.findById(dto.getCmpsId())
                    .orElseThrow(() -> new ResourceNotFoundException("Campus not found with ID: " + dto.getCmpsId()));

            // Check if mapping already exists
            List<CampusEmployee> existingMappings = campusEmployeeRepository.findByEmpIdAndCmpsId(employee.getEmp_id(),
                    campus.getCampusId());

            CampusEmployee entity;
            if (existingMappings != null && !existingMappings.isEmpty()) {
                // Update existing mapping (take the first one if multiples exist)
                entity = existingMappings.get(0);
            } else {
                // Create new mapping
                entity = new CampusEmployee();
                entity.setEmpId(employee);
                entity.setCmpsId(campus);
                entity.setCreatedBy(dto.getCreatedBy() != null ? dto.getCreatedBy() : 1);
            }

            entity.setRoleId(dto.getRoleId());
            entity.setAttendanceStatus(dto.getAttendanceStatus());
            entity.setIsActive(1); // Auto-set active to 1

            // Audit fields
            if (entity.getCmpsEmployeeId() != null) {
                entity.setUpdatedBy(dto.getUpdatedBy());
                entity.setUpdatedDate(LocalDateTime.now());
            }

            CampusEmployee savedEntity = campusEmployeeRepository.save(entity);
            savedDTOs.add(mapToDTO(savedEntity));
        }

        return savedDTOs;
    }

    @Transactional
    public List<CampusEmployeeDTO> unassignCampusesFromEmployee(List<CampusEmployeeDTO> campusEmployeeDTOs) {
        if (campusEmployeeDTOs == null || campusEmployeeDTOs.isEmpty()) {
            throw new IllegalArgumentException("Campus unassignment list cannot be empty");
        }

        List<CampusEmployeeDTO> updatedDTOs = new ArrayList<>();

        for (CampusEmployeeDTO dto : campusEmployeeDTOs) {
            boolean processed = false;

            if (dto.getPayrollId() == null || dto.getPayrollId().trim().isEmpty()) {
                throw new IllegalArgumentException("Payroll ID is required");
            }

            Employee employee = employeeRepository.findByPayrollId(dto.getPayrollId())
                    .orElseGet(() -> employeeRepository.findByTempPayrollId(dto.getPayrollId())
                            .orElseThrow(() -> new ResourceNotFoundException(
                                    "Employee not found with Payroll ID: " + dto.getPayrollId())));

            // 1. Check and deactivate CampusEmployee record
            // Changed to return List to handle potential duplicates
            java.util.List<CampusEmployee> campusEmployees = campusEmployeeRepository
                    .findByEmpIdAndCmpsId(employee.getEmp_id(), dto.getCmpsId());

            if (campusEmployees != null && !campusEmployees.isEmpty()) {
                for (CampusEmployee entity : campusEmployees) {
                    entity.setIsActive(0); // Set active to 0
                    entity.setUpdatedBy(dto.getUpdatedBy());
                    entity.setUpdatedDate(LocalDateTime.now());

                    CampusEmployee savedEntity = campusEmployeeRepository.save(entity);
                    updatedDTOs.add(mapToDTO(savedEntity));
                    processed = true;
                }
            }

            if (!processed) {
                // Log or handle case where record was not found if needed
            }
        }

        return updatedDTOs;
    }

    // Helper method to convert Entity to DTO
    private com.employee.dto.CampusEmployeeDTO convertToDTO(CampusEmployee entity) {
        String payrollId = entity.getEmpId().getPayRollId();
        if (payrollId == null) {
            payrollId = entity.getEmpId().getTempPayrollId();
        }
        return new com.employee.dto.CampusEmployeeDTO(
                payrollId,
                entity.getCmpsId().getCampusId(),
                entity.getRoleId(),
                entity.getAttendanceStatus(),
                entity.getCreatedBy(),
                entity.getUpdatedBy());
    }

    /**
     * Get Employee Location Details (CampusId, CampusName, CampusDetails)
     * Logic:
     * 1. Initialize CampusId/Name from Primary Campus if available.
     * 2. Add Campuses from CampusEmployee/SharedEmployee tables to list (if
     * distinct and NOT primary).
     */
    public com.employee.dto.EmployeeLocationDTO getEmployeeLocation(Integer empId) {
        com.employee.dto.EmployeeLocationDTO locationDTO = new com.employee.dto.EmployeeLocationDTO();
        List<com.employee.dto.CampusInfoDTO> campusDetails = new java.util.ArrayList<>();
        Set<Integer> addedCampusIds = new HashSet<>();

        // Initialize from Primary Campus if available
        com.employee.entity.Employee employee = employeeRepository.findById(empId).orElse(null);
        if (employee != null && employee.getCampus_id() != null) {
            Campus primaryCampus = employee.getCampus_id();

            // Set Top-Level Fields
            locationDTO.setCampusId(primaryCampus.getCampusId());
            locationDTO.setCampusName(primaryCampus.getCampusName());

            if (primaryCampus.getState() != null) {
                locationDTO.setStateId(primaryCampus.getState().getStateId());
                locationDTO.setStateName(primaryCampus.getState().getStateName());
            }
            if (primaryCampus.getCity() != null) {
                locationDTO.setCityId(primaryCampus.getCity().getCityId());
                locationDTO.setCityName(primaryCampus.getCity().getCityName());
            }

            // Set Building Fields
            if (employee.getBuilding_id() != null) {
                locationDTO.setBuildingId(employee.getBuilding_id().getBuildingId());
                locationDTO.setBuildingName(employee.getBuilding_id().getBuildingName());
            }

            // Track Primary Campus ID to avoid adding it to the list later
            addedCampusIds.add(primaryCampus.getCampusId());
        }

        // Step 1: Check CampusEmployee table
        List<CampusEmployee> multiCampuses = campusEmployeeRepository.findByEmpId(empId);

        if (multiCampuses != null && !multiCampuses.isEmpty()) {
            for (CampusEmployee ce : multiCampuses) {
                if (ce.getCmpsId() != null && !addedCampusIds.contains(ce.getCmpsId().getCampusId())) {
                    addCampusToDTO(ce.getCmpsId(), campusDetails, locationDTO);
                    addedCampusIds.add(ce.getCmpsId().getCampusId());
                }
            }
        } else {
            // Step 2: Check SharedEmployee table
            List<com.employee.entity.SharedEmployee> sharedEmployees = sharedEmployeeRepository
                    .findActiveByEmpId(empId);

            if (sharedEmployees != null && !sharedEmployees.isEmpty()) {
                for (com.employee.entity.SharedEmployee se : sharedEmployees) {
                    if (se.getCmpsId() != null && !addedCampusIds.contains(se.getCmpsId().getCampusId())) {
                        addCampusToDTO(se.getCmpsId(), campusDetails, locationDTO);
                        addedCampusIds.add(se.getCmpsId().getCampusId());
                    }
                }
            }
        }

        locationDTO.setCampusDetails(campusDetails);
        return locationDTO;
    }

    private void addCampusToDTO(Campus campus, List<com.employee.dto.CampusInfoDTO> list,
            com.employee.dto.EmployeeLocationDTO locationDTO) {
        if (campus != null) {
            list.add(new com.employee.dto.CampusInfoDTO(campus.getCampusId(), campus.getCampusName()));
        }
    }

    private CampusEmployeeDTO mapToDTO(CampusEmployee entity) {
        String payrollId = entity.getEmpId().getPayRollId();
        if (payrollId == null) {
            payrollId = entity.getEmpId().getTempPayrollId();
        }
        return new CampusEmployeeDTO(
                payrollId,
                entity.getCmpsId().getCampusId(),
                entity.getRoleId(),
                entity.getAttendanceStatus(),
                entity.getCreatedBy(),
                entity.getUpdatedBy());
    }
}
