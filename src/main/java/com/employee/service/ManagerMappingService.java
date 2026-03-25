package com.employee.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.employee.dto.BulkCompleteUnassignDTO;
import com.employee.dto.BulkManagerMappingDTO;
import com.employee.dto.BulkUnmappingDTO;
import com.employee.dto.CampusDetailDTO;
import com.employee.dto.CampusMappingDTO;
import com.employee.dto.CompleteUnassignDTO;
import com.employee.dto.CompleteUnassignResponseDTO;
import com.employee.dto.EmployeeBatchCampusDTO;
import com.employee.dto.EmployeeCampusAddressDTO;
import com.employee.dto.ManagerMappingDTO;
import com.employee.dto.SelectiveBulkUnmappingDTO;
import com.employee.dto.SelectiveUnmappingDTO;
import com.employee.dto.UnmappingDTO;
import com.employee.entity.Building;
import com.employee.entity.BuildingAddress;
import com.employee.entity.Campus;
import com.employee.entity.CampusEmployee;
import com.employee.entity.City;
import com.employee.entity.Department;
import com.employee.entity.Designation;
import com.employee.entity.EmpSubject;
import com.employee.entity.Employee;
import com.employee.entity.SharedEmployee;
import com.employee.entity.Subject;
import com.employee.exception.ResourceNotFoundException;
import com.employee.repository.BuildingAddressRepository;
import com.employee.repository.BuildingRepository;
import com.employee.repository.CampusEmployeeRepository;
import com.employee.repository.CampusRepository;
import com.employee.repository.CityRepository;
import com.employee.repository.DepartmentRepository;
import com.employee.repository.DesignationRepository;
import com.employee.repository.EmployeeRepository;
import com.employee.repository.EmpSubjectRepository;
import com.employee.repository.SharedEmployeeRepository;
import com.employee.repository.SubjectRepository;
import com.employee.repository.RoleRepository;
import com.employee.repository.CampusContactRepository;

/**
 * 
 * Service for Manager Mapping functionality.
 * Handles the hierarchy: City → Campus → Department → Designation → Employees
 * and updates work starting date for selected employees.
 */
@Service
public class ManagerMappingService {

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private CampusRepository campusRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private DesignationRepository designationRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private SharedEmployeeRepository sharedEmployeeRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private BuildingRepository buildingRepository;

    @Autowired
    private BuildingAddressRepository buildingAddressRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private CampusEmployeeRepository campusEmployeeRepository;

    @Autowired
    private CampusContactRepository campusContactRepository;

    @Autowired
    private EmpSubjectRepository empSubjectRepository;

    /**
     * 
     * Maps employee based on payrollId and updates their details.
     * 
     * Flow:
     * 1. Validate City
     * 2. Validate Campus (must belong to City)
     * 3. Validate Department exists and is active (master table - independent)
     * 4. Validate Designation exists and is active
     * 5. Find Employee by payrollId
     * 6. Validate Employee is active
     * 7. Validate Manager (managerId) exists and is active (if provided)
     * 8. Validate Reporting Manager (reportingManagerId) exists and is active (if
     * provided)
     * 9. Update employee: campus, department, designation, manager, reporting
     * manager, work starting date, remarks
     * 
     * @param mappingDTO The manager mapping request DTO
     * @return The same ManagerMappingDTO that was passed in
     */
    @Transactional
    public ManagerMappingDTO mapEmployeesAndUpdateWorkDate(ManagerMappingDTO mappingDTO) {
        // Validate required fields
        validateMappingDTO(mappingDTO);

        // Step 1: Validate City exists
        cityRepository.findById(mappingDTO.getCityId())
                .orElseThrow(() -> new ResourceNotFoundException("City not found with ID: " + mappingDTO.getCityId()));

        // Step 2: Find Employee by payrollId
        Employee employee = findEmployeeByPayrollId(mappingDTO.getPayrollId());

        // Step 3: Validate Employee is active
        if (employee.getIs_active() != 1) {
            throw new ResourceNotFoundException(
                    "Employee with payrollId " + mappingDTO.getPayrollId() + " is not active");
        }

        boolean employeeChanged = false;
        Campus employeeCampus = employee.getCampus_id();
        List<CampusMappingDTO> campusMappingsList = mappingDTO.getCampusMappings();

        // Step 4: Differentiate Primary vs Shared mapping
        List<CampusMappingDTO> sharedMappings = new ArrayList<>();

        if (campusMappingsList != null) {
            for (CampusMappingDTO dto : campusMappingsList) {
                // If this mapping matches the Primary Campus in the Employee table
                if (employeeCampus != null && employeeCampus.getCampusId() == dto.getCampusId()) {
                    // Update main employee fields
                    if (dto.getDepartmentId() != null && dto.getDepartmentId() > 0) {
                        if (employee.getDepartment() == null
                                || employee.getDepartment().getDepartment_id() != dto.getDepartmentId()) {
                            Department newDept = departmentRepository.findByIdAndIsActive(dto.getDepartmentId(), 1)
                                    .orElseThrow(() -> new ResourceNotFoundException(
                                            "Active Department not found: " + dto.getDepartmentId()));
                            employee.setDepartment(newDept);
                            employeeChanged = true;
                        }
                    } else if (employee.getDepartment() != null) {
                        employee.setDepartment(null);
                        employeeChanged = true;
                    }

                    if (dto.getDesignationId() != null && dto.getDesignationId() > 0) {
                        if (employee.getDesignation() == null
                                || employee.getDesignation().getDesignation_id() != dto.getDesignationId()) {
                            Designation newDesig = designationRepository.findByIdAndIsActive(dto.getDesignationId(), 1)
                                    .orElseThrow(() -> new ResourceNotFoundException(
                                            "Active Designation not found: " + dto.getDesignationId()));
                            employee.setDesignation(newDesig);
                            employeeChanged = true;
                        }
                    } else if (employee.getDesignation() != null) {
                        employee.setDesignation(null);
                        employeeChanged = true;
                    }
                }

                // ALWAYS process in sharedMappings if a subject is provided (even for Primary
                // Campus)
                // or if it's strictly a shared campus
                if (dto.getSubjectId() != null && dto.getSubjectId() > 0) {
                    sharedMappings.add(dto);
                } else if (employeeCampus == null || employeeCampus.getCampusId() != dto.getCampusId()) {
                    // Strictly shared campus (even without subject)
                    sharedMappings.add(dto);
                }
            }
        }

        // Step 5: Process shared campus mappings
        if (!sharedMappings.isEmpty()) {
            processCampusMappings(employee, sharedMappings, mappingDTO.getUpdatedBy());
        }

        // Step 6: Assign Manager - only if provided and different
        Integer managerIdValue = mappingDTO.getManagerId();
        if (managerIdValue != null) {
            if (managerIdValue == 0) {
                if (employee.getEmployee_manager_id() != null) {
                    employee.setEmployee_manager_id(null);
                    employeeChanged = true;
                }
            } else {
                if (employee.getEmployee_manager_id() == null
                        || employee.getEmployee_manager_id().getEmp_id() != managerIdValue) {
                    Employee manager = employeeRepository.findById(managerIdValue)
                            .orElseThrow(() -> new ResourceNotFoundException(
                                    "Manager not found with ID: " + managerIdValue));
                    if (manager.getIs_active() != 1)
                        throw new ResourceNotFoundException("Manager ID " + managerIdValue + " is not active");

                    employee.setEmployee_manager_id(manager);
                    employeeChanged = true;
                }
            }
        }

        // Step 7: Assign Reporting Manager
        Integer reportingManagerIdValue = mappingDTO.getReportingManagerId();
        if (reportingManagerIdValue != null) {
            if (reportingManagerIdValue == 0) {
                if (employee.getEmployee_reporting_id() != null) {
                    employee.setEmployee_reporting_id(null);
                    employeeChanged = true;
                }
            } else {
                if (employee.getEmployee_reporting_id() == null
                        || employee.getEmployee_reporting_id().getEmp_id() != reportingManagerIdValue) {
                    Employee rManager = employeeRepository.findById(reportingManagerIdValue)
                            .orElseThrow(() -> new ResourceNotFoundException(
                                    "Reporting Manager not found: " + reportingManagerIdValue));
                    if (rManager.getIs_active() != 1)
                        throw new ResourceNotFoundException(
                                "Reporting Manager ID " + reportingManagerIdValue + " is not active");

                    employee.setEmployee_reporting_id(rManager);
                    employeeChanged = true;
                }
            }
        }

        // Step 8: Update other fields
        if (mappingDTO.getWorkStartingDate() != null
                && !java.util.Objects.equals(employee.getContract_start_date(), mappingDTO.getWorkStartingDate())) {
            employee.setContract_start_date(mappingDTO.getWorkStartingDate());
            employeeChanged = true;
        }

        String cleanRemark = (mappingDTO.getRemark() != null) ? mappingDTO.getRemark().trim() : null;
        if (!java.util.Objects.equals(employee.getRemarks(), cleanRemark)) {
            employee.setRemarks(cleanRemark);
            employeeChanged = true;
        }

        if (employeeChanged) {
            employee.setUpdated_by(mappingDTO.getUpdatedBy());
            employee.setUpdated_date(LocalDateTime.now());
            employeeRepository.save(employee);
        }

        return mappingDTO;
    }

    /**
     * Maps multiple employees based on payrollIds and updates their details with
     * the same values.
     * 
     * Flow:
     * 1. Validate City
     * 2. Validate Campus (must belong to City)
     * 3. Validate Department exists and is active (master table - independent)
     * 4. Validate Designation exists and is active
     * 5. Validate Manager (managerId) exists and is active (if provided)
     * 6. Validate Reporting Manager (reportingManagerId) exists and is active (if
     * provided)
     * 7. For each payrollId:
     * - Find Employee by payrollId
     * - Validate Employee is active
     * - Update employee: campus, department, designation, manager, reporting
     * manager, work starting date, remarks
     * 
     * @param bulkMappingDTO The bulk manager mapping request DTO
     * @return The same BulkManagerMappingDTO that was passed in
     */
    @Transactional
    public BulkManagerMappingDTO mapMultipleEmployeesAndUpdateWorkDate(BulkManagerMappingDTO bulkMappingDTO) {
        // Validate required fields
        validateBulkMappingDTO(bulkMappingDTO);

        // Step 1: Validate City exists
        cityRepository.findById(bulkMappingDTO.getCityId())
                .orElseThrow(
                        () -> new ResourceNotFoundException("City not found with ID: " + bulkMappingDTO.getCityId()));

        // Step 2: Campus mappings array
        List<CampusMappingDTO> campusMappings = bulkMappingDTO.getCampusMappings();

        // Step 3: Pre-validate all campuses, departments, and designations if provided
        Department department = null;
        if (campusMappings != null && !campusMappings.isEmpty()) {
            for (CampusMappingDTO campusMapping : campusMappings) {
                // Validate Department exists and is active (if provided)
                Department campusDepartment = null;
                if (campusMapping.getDepartmentId() != null && campusMapping.getDepartmentId() > 0) {
                    campusDepartment = departmentRepository
                            .findByIdAndIsActive(campusMapping.getDepartmentId(), 1)
                            .orElseThrow(() -> new ResourceNotFoundException(
                                    "Active Department not found with ID: " + campusMapping.getDepartmentId()));
                }

                // Validate Campus is active and exists in the City
                Campus campus = campusRepository.findByCampusIdAndIsActive(campusMapping.getCampusId(), 1)
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Active Campus not found with ID: " + campusMapping.getCampusId()));

                if (campus.getCity() == null || campus.getCity().getCityId() != bulkMappingDTO.getCityId()) {
                    throw new ResourceNotFoundException(
                            String.format("Campus with ID %d is not assigned to City with ID %d",
                                    campusMapping.getCampusId(), bulkMappingDTO.getCityId()));
                }

                // Validate Designation exists and is active (if provided)
                Designation designation = null;
                if (campusMapping.getDesignationId() != null && campusMapping.getDesignationId() > 0) {
                    designation = designationRepository.findByIdAndIsActive(campusMapping.getDesignationId(), 1)
                            .orElseThrow(() -> new ResourceNotFoundException(
                                    "Active Designation not found with ID: " + campusMapping.getDesignationId()));

                    if (campusDepartment != null && (designation.getDepartment() == null
                            || designation.getDepartment().getDepartment_id() != campusMapping.getDepartmentId())) {
                        throw new ResourceNotFoundException(
                                String.format("Designation with ID %d does not belong to Department with ID %d",
                                        campusMapping.getDesignationId(), campusMapping.getDepartmentId()));
                    }
                }

                // Use first department for employee table if needed
                if (department == null) {
                    department = campusDepartment;
                }
            }
        }

        // Step 5: Assign Manager - only if provided (will validate campus match per
        // employee)
        // Treat 0 as null
        Employee manager = null;
        Integer managerIdValue = bulkMappingDTO.getManagerId();
        if (managerIdValue != null && managerIdValue != 0) {
            // Manager ID provided - validate it
            final Integer managerId = managerIdValue; // Make it final for lambda
            manager = employeeRepository.findById(managerId)
                    .orElseThrow(() -> new ResourceNotFoundException("Manager not found with ID: " + managerId));

            if (manager.getIs_active() != 1) {
                throw new ResourceNotFoundException("Manager with ID " + managerId + " is not active");
            }
        }
        // If managerId is not provided (null or 0), manager remains null - no
        // auto-assignment

        // Step 6: Assign Reporting Manager - only if provided (will validate campus
        // match per employee)
        // Treat 0 as null
        Employee reportingManager = null;
        Integer reportingManagerIdValue = bulkMappingDTO.getReportingManagerId();
        if (reportingManagerIdValue != null && reportingManagerIdValue != 0) {
            // Reporting Manager ID provided - validate it
            final Integer reportingManagerId = reportingManagerIdValue; // Make it final for lambda
            reportingManager = employeeRepository.findById(reportingManagerId)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Reporting Manager not found with ID: " + reportingManagerId));

            if (reportingManager.getIs_active() != 1) {
                throw new ResourceNotFoundException(
                        "Reporting Manager with ID " + reportingManagerId + " is not active");
            }
        }
        // If reportingManagerId is not provided (null or 0), reportingManager remains
        // null - no auto-assignment

        // Step 7: Process each payrollId
        List<String> processedPayrollIds = new ArrayList<>();
        List<String> failedPayrollIds = new ArrayList<>();

        for (String payrollId : bulkMappingDTO.getPayrollIds()) {
            try {
                // Find Employee by payrollId
                Employee employee = findEmployeeByPayrollId(payrollId);

                // Validate Employee is active
                if (employee.getIs_active() != 1) {
                    failedPayrollIds.add(payrollId + " (not active)");
                    continue;
                }

                // Get employee's existing campus (do not change it)
                Campus employeeCampus = employee.getCampus_id();
                // Removed validation to allow first-time mapping

                // For bulk operations, make sure manager/reporting manager is not the current
                // employee
                Employee employeeManager = manager;
                Employee employeeReportingManager = reportingManager;

                if (employeeManager != null && employeeManager.getEmp_id() == employee.getEmp_id()) {
                    employeeManager = null;
                }

                if (employeeReportingManager != null && employeeReportingManager.getEmp_id() == employee.getEmp_id()) {
                    employeeReportingManager = null;
                }

                // Validate manager is from the same city as employee (if provided)
                if (employeeManager != null) {
                    // Check employeeCampus null safety
                    Integer empCityId = (employeeCampus != null && employeeCampus.getCity() != null)
                            ? employeeCampus.getCity().getCityId()
                            : null;
                    if (empCityId == null) {
                        // If employee has no campus/city, we assume it's a new mapping and will inherit
                        // new campus city
                        // Use Bulk DTO City ID for validation
                        empCityId = bulkMappingDTO.getCityId();
                    }

                    if (employeeManager.getCampus_id() == null || employeeManager.getCampus_id().getCity() == null
                            || employeeManager.getCampus_id().getCity().getCityId() != empCityId) {
                        failedPayrollIds.add(payrollId + " (manager not from same city)");
                        continue;
                    }
                }

                // Validate reporting manager is from the same city as employee (if provided)
                if (employeeReportingManager != null) {
                    // Check employeeCampus null safety
                    Integer empCityId = (employeeCampus != null && employeeCampus.getCity() != null)
                            ? employeeCampus.getCity().getCityId()
                            : null;
                    if (empCityId == null) {
                        // If employee has no campus/city, we assume it's a new mapping and will inherit
                        // new campus city
                        // Use Bulk DTO City ID for validation
                        empCityId = bulkMappingDTO.getCityId();
                    }

                    if (employeeReportingManager.getCampus_id() == null
                            || employeeReportingManager.getCampus_id().getCity() == null
                            || employeeReportingManager.getCampus_id().getCity().getCityId() != empCityId) {
                        failedPayrollIds.add(payrollId + " (reporting manager not from same city)");
                        continue;
                    }
                }

                // Process each campus mapping for this employee
                // Only store in SharedEmployee table if multiple campuses are selected
                // Validate campus matching for bulk operations
                if (employeeCampus == null) {
                    // Assign first campus if null
                    CampusMappingDTO firstMapping = campusMappings.get(0);
                    Campus requestedPrimaryCampus = campusRepository
                            .findByCampusIdAndIsActive(firstMapping.getCampusId(), 1)
                            .orElseThrow(() -> new ResourceNotFoundException(
                                    "Active Campus not found with ID: " + firstMapping.getCampusId()));
                    employeeCampus = requestedPrimaryCampus;
                }

                // (No else override)

                boolean empChangedForBulk = false;
                Campus empCampusForBulk = employee.getCampus_id();

                // Separate mappings: Primary vs Shared
                List<CampusMappingDTO> bulkSharedMappings = new ArrayList<>();
                if (campusMappings != null) {
                    for (CampusMappingDTO dto : campusMappings) {
                        if (empCampusForBulk != null && empCampusForBulk.getCampusId() == dto.getCampusId()) {
                            // Primary Campus mapping found in request
                            if (dto.getDepartmentId() != null && dto.getDepartmentId() > 0) {
                                if (employee.getDepartment() == null
                                        || employee.getDepartment().getDepartment_id() != dto.getDepartmentId()) {
                                    Department newBulkDept = departmentRepository
                                            .findByIdAndIsActive(dto.getDepartmentId(), 1)
                                            .orElseThrow(() -> new ResourceNotFoundException(
                                                    "Active Department not found: " + dto.getDepartmentId()));
                                    employee.setDepartment(newBulkDept);
                                    empChangedForBulk = true;
                                }
                            } else if (employee.getDepartment() != null) {
                                employee.setDepartment(null);
                                empChangedForBulk = true;
                            }

                            if (dto.getDesignationId() != null && dto.getDesignationId() > 0) {
                                if (employee.getDesignation() == null
                                        || employee.getDesignation().getDesignation_id() != dto.getDesignationId()) {
                                    Designation newBulkDesig = designationRepository
                                            .findByIdAndIsActive(dto.getDesignationId(), 1)
                                            .orElseThrow(() -> new ResourceNotFoundException(
                                                    "Active Designation not found: " + dto.getDesignationId()));
                                    employee.setDesignation(newBulkDesig);
                                    empChangedForBulk = true;
                                }
                            } else if (employee.getDesignation() != null) {
                                employee.setDesignation(null);
                                empChangedForBulk = true;
                            }
                        }

                        // Add to shared logic if subject is present or if it's a shared campus
                        if (dto.getSubjectId() != null && dto.getSubjectId() > 0) {
                            bulkSharedMappings.add(dto);
                        } else if (empCampusForBulk == null || empCampusForBulk.getCampusId() != dto.getCampusId()) {
                            bulkSharedMappings.add(dto);
                        }
                    }
                }

                // Process Shared mappings
                if (!bulkSharedMappings.isEmpty()) {
                    processCampusMappings(employee, bulkSharedMappings, bulkMappingDTO.getUpdatedBy());
                }

                // Update Manager/Reporting Manager
                if (managerIdValue != null) {
                    if (employeeManager == null) {
                        if (employee.getEmployee_manager_id() != null) {
                            employee.setEmployee_manager_id(null);
                            empChangedForBulk = true;
                        }
                    } else if (employee.getEmployee_manager_id() == null
                            || employee.getEmployee_manager_id().getEmp_id() != employeeManager.getEmp_id()) {
                        employee.setEmployee_manager_id(employeeManager);
                        empChangedForBulk = true;
                    }
                }

                if (reportingManagerIdValue != null) {
                    if (employeeReportingManager == null) {
                        if (employee.getEmployee_reporting_id() != null) {
                            employee.setEmployee_reporting_id(null);
                            empChangedForBulk = true;
                        }
                    } else if (employee.getEmployee_reporting_id() == null || employee.getEmployee_reporting_id()
                            .getEmp_id() != employeeReportingManager.getEmp_id()) {
                        employee.setEmployee_reporting_id(employeeReportingManager);
                        empChangedForBulk = true;
                    }
                }

                if (bulkMappingDTO.getWorkStartingDate() != null && !java.util.Objects
                        .equals(employee.getContract_start_date(), bulkMappingDTO.getWorkStartingDate())) {
                    employee.setContract_start_date(bulkMappingDTO.getWorkStartingDate());
                    empChangedForBulk = true;
                }

                String cleanBulkRemark = (bulkMappingDTO.getRemark() != null) ? bulkMappingDTO.getRemark().trim()
                        : null;
                if (!java.util.Objects.equals(employee.getRemarks(), cleanBulkRemark)) {
                    employee.setRemarks(cleanBulkRemark);
                    empChangedForBulk = true;
                }

                if (empChangedForBulk) {
                    employee.setUpdated_by(bulkMappingDTO.getUpdatedBy());
                    employee.setUpdated_date(LocalDateTime.now());
                    employeeRepository.save(employee);
                }
                processedPayrollIds.add(payrollId);

            } catch (ResourceNotFoundException e) {
                failedPayrollIds.add(payrollId + " (not found)");
            } catch (Exception e) {
                failedPayrollIds.add(payrollId + " (" + e.getMessage() + ")");
            }
        }

        return bulkMappingDTO; // Return the request DTO
    }

    /**
     * Finds employee by payrollId (checks both tempPayrollId and payRollId).
     * 
     * @param payrollId Payroll ID (can be tempPayrollId or payRollId)
     * @return Employee found by payrollId
     * @throws ResourceNotFoundException if employee not found
     */
    private Employee findEmployeeByPayrollId(String payrollId) {
        if (payrollId == null || payrollId.trim().isEmpty()) {
            throw new IllegalArgumentException("payrollId is required");
        }

        // Try to find by tempPayrollId first
        Optional<Employee> employeeByTemp = employeeRepository.findByTempPayrollId(payrollId);
        if (employeeByTemp.isPresent()) {
            return employeeByTemp.get();
        }

        // Try to find by payRollId
        Optional<Employee> employeeByPayroll = employeeRepository.findByPayRollId(payrollId);
        if (employeeByPayroll.isPresent()) {
            return employeeByPayroll.get();
        }

        throw new ResourceNotFoundException("Employee not found with payrollId: " + payrollId);
    }

    /**
     * Creates or updates a SharedEmployee record for an employee working in
     * multiple campuses.
     * 
     * 
     * 
     * /**
     * Deactivates all existing active shared employee mappings for the given
     * employee.
     */
    /**
     * Processes campus mappings for an employee with Additive Logic:
     * 1. If campus ID matches an existing active mapping, it deactivates the old
     * record
     * and inserts a NEW one (to preserve history).
     * 2. If campus ID is new, it inserts a NEW record.
     * 3. Does NOT deactivate other existing records (Additive behavior).
     */
    private void processCampusMappings(Employee employee, List<CampusMappingDTO> newMappings, Integer updatedBy) {
        if (newMappings == null || newMappings.isEmpty()) {
            return;
        }

        // 1. Get currently active mappings
        List<com.employee.entity.SharedEmployee> activeMappings = sharedEmployeeRepository
                .findActiveByEmpId(employee.getEmp_id());

        Set<Integer> processedSharedEmployeeIds = new HashSet<>();

        for (CampusMappingDTO dto : newMappings) {
            boolean matched = false;

            // Validation (if provided)
            Department campusDepartment = null;
            if (dto.getDepartmentId() != null && dto.getDepartmentId() > 0) {
                campusDepartment = departmentRepository.findByIdAndIsActive(dto.getDepartmentId(), 1)
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Active Department not found with ID: " + dto.getDepartmentId()));
            }

            Campus campus = campusRepository.findByCampusIdAndIsActive(dto.getCampusId(), 1)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Active Campus not found with ID: " + dto.getCampusId()));

            Designation designation = null;
            if (dto.getDesignationId() != null && dto.getDesignationId() > 0) {
                designation = designationRepository.findByIdAndIsActive(dto.getDesignationId(), 1)
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Active Designation not found with ID: " + dto.getDesignationId()));

                if (campusDepartment != null && (designation.getDepartment() == null
                        || designation.getDepartment().getDepartment_id() != dto.getDepartmentId())) {
                    throw new ResourceNotFoundException(
                            String.format("Designation with ID %d does not belong to Department with ID %d",
                                    dto.getDesignationId(), dto.getDepartmentId()));
                }
            }

            // Check if this campus is already active for the employee
            if (activeMappings != null) {
                for (com.employee.entity.SharedEmployee existing : activeMappings) {
                    // Check if SUBJECT matches as well
                    Integer existingSubjectId = (existing.getSubjectId() != null)
                            ? existing.getSubjectId().getSubject_id()
                            : null;
                    Integer newSubjectId = (dto.getSubjectId() != null && dto.getSubjectId() != 0)
                            ? dto.getSubjectId()
                            : null;

                    boolean subjectsMatch = java.util.Objects.equals(existingSubjectId, newSubjectId);

                    if (existing.getCmpsId().getCampusId() == dto.getCampusId()
                            && subjectsMatch
                            && !processedSharedEmployeeIds.contains(existing.getSharedEmployeeId())) {

                        // Match found (Campus + Subject)! Check if designation is different
                        if (existing.getDesignationId().getDesignation_id() == dto.getDesignationId()) {
                            // No change for this Campus + Subject, skip
                            processedSharedEmployeeIds.add(existing.getSharedEmployeeId());
                            matched = true;
                            break;
                        }

                        // Designation is different for this Campus + Subject!
                        // Deactivate the old record and create a NEW one
                        existing.setIsActive(0);
                        existing.setUpdatedBy(updatedBy);
                        existing.setUpdatedDate(LocalDateTime.now());
                        sharedEmployeeRepository.save(existing);

                        createNewSharedEmployee(employee, campus, designation, dto.getSubjectId(), updatedBy);

                        processedSharedEmployeeIds.add(existing.getSharedEmployeeId());
                        matched = true;
                        break;
                    }
                }
            }

            if (!matched) {
                // New Campus Assignment
                createNewSharedEmployee(employee, campus, designation, dto.getSubjectId(), updatedBy);
            }
        }
    }

    /**
     * Creates a NEW SharedEmployee record (always active=1).
     */
    private void createNewSharedEmployee(Employee employee, Campus campus, Designation designation, Integer subjectId,
            Integer createdBy) {
        com.employee.entity.SharedEmployee sharedEmployee = new com.employee.entity.SharedEmployee();
        sharedEmployee.setEmpId(employee);
        sharedEmployee.setCmpsId(campus);
        sharedEmployee.setDesignationId(designation);
        sharedEmployee.setIsActive(1); // Always active for new record
        sharedEmployee.setCreatedBy(createdBy != null ? createdBy : 1);
        sharedEmployee.setCreatedDate(LocalDateTime.now());
        sharedEmployee.setUpdatedBy(createdBy != null ? createdBy : 1);
        sharedEmployee.setUpdatedDate(LocalDateTime.now());

        if (subjectId != null && subjectId != 0) {
            Subject subject = subjectRepository.findById(subjectId)
                    .orElseThrow(() -> new ResourceNotFoundException("Subject not found with ID: " + subjectId));
            sharedEmployee.setSubjectId(subject);
        }

        sharedEmployeeRepository.save(sharedEmployee);
    }

    /**
     * Validates the ManagerMappingDTO for required fields.
     * 
     * @param mappingDTO The DTO to validate
     * @throws IllegalArgumentException if any required field is null or invalid
     */
    private void validateMappingDTO(ManagerMappingDTO mappingDTO) {
        if (mappingDTO == null) {
            throw new IllegalArgumentException("ManagerMappingDTO cannot be null");
        }
        if (mappingDTO.getCityId() == null) {
            throw new IllegalArgumentException("cityId is required");
        }
        if (mappingDTO.getPayrollId() == null || mappingDTO.getPayrollId().trim().isEmpty()) {
            throw new IllegalArgumentException("payrollId is required");
        }
        if (mappingDTO.getWorkStartingDate() == null) {
            throw new IllegalArgumentException("workStartingDate is required");
        }

        // Validate campusMappings array - required only if no manager info is provided
        List<CampusMappingDTO> campusMappingsForValidation = mappingDTO.getCampusMappings();
        boolean hasManagerInfo = (mappingDTO.getManagerId() != null && mappingDTO.getManagerId() != 0) ||
                (mappingDTO.getReportingManagerId() != null && mappingDTO.getReportingManagerId() != 0);

        if ((campusMappingsForValidation == null || campusMappingsForValidation.isEmpty()) && !hasManagerInfo) {
            throw new IllegalArgumentException("Either campusMappings array or manager info is required");
        }

        // Validate each campus mapping if present
        if (campusMappingsForValidation != null) {
            for (CampusMappingDTO campusMapping : campusMappingsForValidation) {
                if (campusMapping.getCampusId() == null || campusMapping.getCampusId() <= 0) {
                    throw new IllegalArgumentException(
                            "Valid campusId (greater than 0) is required in each campusMapping object");
                }
                if (campusMapping.getDepartmentId() == null) {
                    throw new IllegalArgumentException(
                            "departmentId is required in each campusMapping object");
                }
                if (campusMapping.getDesignationId() == null) {
                    throw new IllegalArgumentException(
                            "designationId is required in each campusMapping object");
                }
            }
        }
    }

    /**
     * Validates the BulkManagerMappingDTO for required fields.
     * 
     * @param bulkMappingDTO The DTO to validate
     * @throws IllegalArgumentException if any required field is null or invalid
     */
    private void validateBulkMappingDTO(BulkManagerMappingDTO bulkMappingDTO) {
        if (bulkMappingDTO == null) {
            throw new IllegalArgumentException("BulkManagerMappingDTO cannot be null");
        }
        if (bulkMappingDTO.getCityId() == null) {
            throw new IllegalArgumentException("cityId is required");
        }

        // Validate campusMappings array - required only if no manager info is provided
        List<CampusMappingDTO> campusMappingsForValidation = bulkMappingDTO.getCampusMappings();
        boolean hasManagerInfo = (bulkMappingDTO.getManagerId() != null && bulkMappingDTO.getManagerId() != 0) ||
                (bulkMappingDTO.getReportingManagerId() != null && bulkMappingDTO.getReportingManagerId() != 0);

        if ((campusMappingsForValidation == null || campusMappingsForValidation.isEmpty()) && !hasManagerInfo) {
            throw new IllegalArgumentException("Either campusMappings array or manager info is required");
        }

        // Validate each campus mapping if present
        if (campusMappingsForValidation != null) {
            for (CampusMappingDTO campusMapping : campusMappingsForValidation) {
                if (campusMapping.getCampusId() == null || campusMapping.getCampusId() <= 0) {
                    throw new IllegalArgumentException(
                            "Valid campusId (greater than 0) is required in each campusMapping object");
                }
                if (campusMapping.getDepartmentId() == null) {
                    throw new IllegalArgumentException(
                            "departmentId is required in each campusMapping object");
                }
                if (campusMapping.getDesignationId() == null) {
                    throw new IllegalArgumentException(
                            "designationId is required in each campusMapping object");
                }
            }
        }
        if (bulkMappingDTO.getPayrollIds() == null || bulkMappingDTO.getPayrollIds().isEmpty()) {
            throw new IllegalArgumentException("payrollIds list is required and cannot be empty");
        }
        // Validate that all payrollIds in the list are not null or empty
        for (String payrollId : bulkMappingDTO.getPayrollIds()) {
            if (payrollId == null || payrollId.trim().isEmpty()) {
                throw new IllegalArgumentException("payrollIds list cannot contain null or empty values");
            }
        }
        if (bulkMappingDTO.getWorkStartingDate() == null) {
            throw new IllegalArgumentException("workStartingDate is required");
        }
    }

    /**
     * Unmaps manager and/or reporting manager from a single employee.
     * Does not require department or designation - but requires city, campus, and
     * last date of working.
     * 
     * Flow:
     * 1. Validate City exists
     * 2. Validate Campus (must belong to City)
     * 3. Find Employee by payrollId
     * 4. Validate Employee is active
     * 5. Update campus
     * 6. Set manager_id to null if provided managerId matches current manager
     * 7. Set reporting_manager_id to null if provided reportingManagerId matches
     * current reporting manager
     * 8. Update last date of working (contract end date)
     * 9. Update remarks (set to null if empty)
     * 10. Update employee
     * 
     * @param unmappingDTO The unmapping request DTO
     * @return The same UnmappingDTO that was passed in
     */
    @Transactional
    public UnmappingDTO unmapEmployee(UnmappingDTO unmappingDTO) {
        // Validate required fields
        if (unmappingDTO == null) {
            throw new IllegalArgumentException("UnmappingDTO cannot be null");
        }
        if (unmappingDTO.getCityId() == null) {
            throw new IllegalArgumentException("cityId is required");
        }
        if (unmappingDTO.getUnmappedCampusIds() == null || unmappingDTO.getUnmappedCampusIds().isEmpty()) {
            throw new IllegalArgumentException("unmappedCampusIds list is required and cannot be empty");
        }
        if (unmappingDTO.getPayrollId() == null || unmappingDTO.getPayrollId().trim().isEmpty()) {
            throw new IllegalArgumentException("payrollId is required");
        }
        if (unmappingDTO.getLastDate() == null) {
            throw new IllegalArgumentException("lastDate is required");
        }

        // Step 1: Validate City exists
        cityRepository.findById(unmappingDTO.getCityId())
                .orElseThrow(
                        () -> new ResourceNotFoundException("City not found with ID: " + unmappingDTO.getCityId()));

        List<Integer> campusesToProcess = new java.util.ArrayList<>();
        if (unmappingDTO.getUnmappedCampusIds() != null && !unmappingDTO.getUnmappedCampusIds().isEmpty()) {
            campusesToProcess.addAll(unmappingDTO.getUnmappedCampusIds());
        } else {
            throw new IllegalArgumentException("unmappedCampusIds list is required and cannot be empty");
        }

        // Step 3: Validate all campuses exist and belong to the City
        for (Integer cid : campusesToProcess) {
            Campus campus = campusRepository.findByCampusIdAndIsActive(cid, 1)
                    .orElseThrow(() -> new ResourceNotFoundException("Active Campus not found with ID: " + cid));

            if (campus.getCity() == null || campus.getCity().getCityId() != unmappingDTO.getCityId()) {
                throw new ResourceNotFoundException(
                        String.format("Campus with ID %d is not assigned to City with ID %d",
                                cid, unmappingDTO.getCityId()));
            }
        }

        // Step 4: Find Employee by payrollId
        Employee employee = findEmployeeByPayrollId(unmappingDTO.getPayrollId());

        // Step 6: Validate Employee is active
        if (employee.getIs_active() != 1) {
            throw new ResourceNotFoundException(
                    "Employee with payrollId " + unmappingDTO.getPayrollId() + " is not active");
        }

        // Step 7: Process campus/subject unmapping
        List<Integer> subjectIds = unmappingDTO.getUnmappedSubjectIds();
        List<Integer> successfullyUnmappedCampuses = new java.util.ArrayList<>();
        List<Integer> successfullyUnmappedSubjects = new java.util.ArrayList<>();

        for (Integer campusId : campusesToProcess) {
            if (subjectIds != null && !subjectIds.isEmpty()) {
                // --- Selective subject unmapping: deactivate the given subjects, campus stays
                // active ---
                boolean anySubjectFound = false;

                // Check in EmpSubject (primary campus subjects)
                if (employee.getCampus_id() != null && employee.getCampus_id().getCampusId() == campusId) {
                    List<EmpSubject> empSubjects = empSubjectRepository.findActiveSubjectsByEmpId(employee.getEmp_id());
                    for (EmpSubject es : empSubjects) {
                        if (es.getSubject_id() != null && subjectIds.contains(es.getSubject_id().getSubject_id())) {
                            es.setIs_active(0);
                            es.setUpdated_by(unmappingDTO.getUpdatedBy() != null ? unmappingDTO.getUpdatedBy() : 1);
                            es.setUpdated_date(LocalDateTime.now());
                            empSubjectRepository.save(es);
                            if (!successfullyUnmappedSubjects.contains(es.getSubject_id().getSubject_id())) {
                                successfullyUnmappedSubjects.add(es.getSubject_id().getSubject_id());
                            }
                            anySubjectFound = true;
                        }
                    }
                }

                // Check in SharedEmployee (shared campus subjects)
                List<SharedEmployee> sharedEmployees = sharedEmployeeRepository
                        .findAllByEmpIdAndCampusId(employee.getEmp_id(), campusId);
                for (SharedEmployee sharedEmployee : sharedEmployees) {
                    if (sharedEmployee.getIsActive() == 1
                            && sharedEmployee.getSubjectId() != null
                            && subjectIds.contains(sharedEmployee.getSubjectId().getSubject_id())) {
                        sharedEmployee.setIsActive(0);
                        sharedEmployee
                                .setUpdatedBy(unmappingDTO.getUpdatedBy() != null ? unmappingDTO.getUpdatedBy() : 1);
                        sharedEmployee.setUpdatedDate(LocalDateTime.now());
                        sharedEmployeeRepository.save(sharedEmployee);
                        if (!successfullyUnmappedSubjects.contains(sharedEmployee.getSubjectId().getSubject_id())) {
                            successfullyUnmappedSubjects.add(sharedEmployee.getSubjectId().getSubject_id());
                        }
                        anySubjectFound = true;
                    }
                }

                if (anySubjectFound) {
                    if (!successfullyUnmappedCampuses.contains(campusId)) {
                        successfullyUnmappedCampuses.add(campusId);
                    }
                }

            } else {
                // --- Full campus unmapping: remove campus and all its subjects ---
                boolean isSharedCampus = false;

                // Check if this campus is a shared campus
                List<SharedEmployee> sharedEmployees = sharedEmployeeRepository
                        .findAllByEmpIdAndCampusId(employee.getEmp_id(), campusId);

                for (SharedEmployee sharedEmployee : sharedEmployees) {
                    if (sharedEmployee.getIsActive() == 1) {
                        if (!successfullyUnmappedCampuses.contains(campusId)) {
                            successfullyUnmappedCampuses.add(campusId);
                        }
                        if (sharedEmployee.getSubjectId() != null) {
                            if (!successfullyUnmappedSubjects.contains(sharedEmployee.getSubjectId().getSubject_id())) {
                                successfullyUnmappedSubjects.add(sharedEmployee.getSubjectId().getSubject_id());
                            }
                        }
                        sharedEmployee.setIsActive(0);
                        sharedEmployee
                                .setUpdatedBy(unmappingDTO.getUpdatedBy() != null ? unmappingDTO.getUpdatedBy() : 1);
                        sharedEmployee.setUpdatedDate(LocalDateTime.now());
                        sharedEmployeeRepository.save(sharedEmployee);
                        isSharedCampus = true;
                    }
                }

                // If not a shared campus, check if it's the primary campus
                if (!isSharedCampus) {
                    if (employee.getCampus_id() != null &&
                            employee.getCampus_id().getCampusId() == campusId) {

                        if (!successfullyUnmappedCampuses.contains(campusId)) {
                            successfullyUnmappedCampuses.add(campusId);
                        }

                        // Remove primary campus
                        employee.setCampus_id(null);

                        // Deactivate all primary campus subjects
                        List<EmpSubject> empSubjects = empSubjectRepository
                                .findActiveSubjectsByEmpId(employee.getEmp_id());
                        for (EmpSubject es : empSubjects) {
                            if (!successfullyUnmappedSubjects.contains(es.getSubject_id().getSubject_id())) {
                                successfullyUnmappedSubjects.add(es.getSubject_id().getSubject_id());
                            }
                            es.setIs_active(0);
                            es.setUpdated_by(unmappingDTO.getUpdatedBy() != null ? unmappingDTO.getUpdatedBy() : 1);
                            es.setUpdated_date(LocalDateTime.now());
                            empSubjectRepository.save(es);
                        }
                    }
                }
            }
        }

        // If subject IDs were provided but NO subjects were found across ANY campus
        if (subjectIds != null && !subjectIds.isEmpty() && successfullyUnmappedSubjects.isEmpty()) {
            throw new ResourceNotFoundException(
                    "Active subjects with IDs " + subjectIds + " not found for this employee in campuses "
                            + campusesToProcess);
        }

        unmappingDTO.setUnmappedCampusIds(successfullyUnmappedCampuses);
        if (!successfullyUnmappedSubjects.isEmpty()) {
            unmappingDTO.setUnmappedSubjectIds(successfullyUnmappedSubjects);
        }

        // Step 8: Unmap manager if flag is true
        if (Boolean.TRUE.equals(unmappingDTO.getUnmapManager())) {
            if (employee.getEmployee_manager_id() == null) {
                throw new ResourceNotFoundException(
                        "Cannot unmap manager: Employee with payrollId '" + unmappingDTO.getPayrollId() +
                                "' does not have a manager assigned");
            }

            // If managerId is provided, validate it matches the current manager
            if (unmappingDTO.getManagerId() != null) {
                Integer currentManagerId = employee.getEmployee_manager_id().getEmp_id();
                if (!currentManagerId.equals(unmappingDTO.getManagerId())) {
                    throw new ResourceNotFoundException(
                            "Cannot unmap manager: Employee's current manager ID (" + currentManagerId +
                                    ") does not match the provided manager ID (" + unmappingDTO.getManagerId() + ")");
                }
            }

            employee.setEmployee_manager_id(null);
        }

        // Step 9: Unmap reporting manager if flag is true
        if (Boolean.TRUE.equals(unmappingDTO.getUnmapReportingManager())) {
            if (employee.getEmployee_reporting_id() == null) {
                throw new ResourceNotFoundException(
                        "Cannot unmap reporting manager: Employee with payrollId '" + unmappingDTO.getPayrollId() +
                                "' does not have a reporting manager assigned");
            }

            // If reportingManagerId is provided, validate it matches the current reporting
            // manager
            if (unmappingDTO.getReportingManagerId() != null) {
                Integer currentReportingManagerId = employee.getEmployee_reporting_id().getEmp_id();
                if (!currentReportingManagerId.equals(unmappingDTO.getReportingManagerId())) {
                    throw new ResourceNotFoundException(
                            "Cannot unmap reporting manager: Employee's current reporting manager ID (" +
                                    currentReportingManagerId + ") does not match the provided reporting manager ID (" +
                                    unmappingDTO.getReportingManagerId() + ")");
                }
            }

            employee.setEmployee_reporting_id(null);
        }

        // Step 10: Update last date of working (contract end date)
        employee.setContract_end_date(unmappingDTO.getLastDate());

        // Step 11: Update remarks: if value exists and is not empty, set it; otherwise
        // set to null
        // Treat null, empty string, whitespace-only, or string "null" as null
        String remarkValue = unmappingDTO.getRemark();
        if (remarkValue != null && !remarkValue.trim().isEmpty() && !remarkValue.trim().equalsIgnoreCase("null")) {
            employee.setRemarks(remarkValue.trim());
        } else {
            employee.setRemarks(null);
        }

        // Step 12: Update audit fields
        employee.setUpdated_by(unmappingDTO.getUpdatedBy() != null ? unmappingDTO.getUpdatedBy() : 1);
        employee.setUpdated_date(LocalDateTime.now());

        employeeRepository.save(employee);

        return unmappingDTO;
    }

    /**
     * Unmaps manager and/or reporting manager from multiple employees in bulk.
     * Does not require department or designation - but requires city, campus, and
     * last date of working.
     * 
     * Flow:
     * 1. Validate City exists
     * 2. Validate Campus (must belong to City)
     * 3. For each payrollId in the list:
     * - Find Employee by payrollId
     * - Validate Employee is active
     * - Update campus
     * - Set manager_id to null if provided managerId matches current manager
     * - Set reporting_manager_id to null if provided reportingManagerId matches
     * current reporting manager
     * - Update last date of working (contract end date)
     * - Update remarks (set to null if empty)
     * - Update employee
     * 
     * @param bulkUnmappingDTO The bulk unmapping request DTO
     * @return The same BulkUnmappingDTO that was passed in
     */
    @Transactional
    public BulkUnmappingDTO unmapMultipleEmployees(BulkUnmappingDTO bulkUnmappingDTO) {
        // ... (existing code)
        return bulkUnmappingDTO;
    }

    /**
     * Unmaps manager and/or reporting manager from multiple employees in bulk
     * (Enhanced).
     * Uses boolean flags to control what gets unmapped.
     * 
     * @param dto The selective bulk unmapping request DTO
     * @return The same SelectiveBulkUnmappingDTO that was passed in
     */
    @Transactional
    public SelectiveBulkUnmappingDTO selectiveUnmapMultipleEmployees(SelectiveBulkUnmappingDTO dto) {
        // Validate required fields
        if (dto == null) {
            throw new IllegalArgumentException("SelectiveBulkUnmappingDTO cannot be null");
        }
        if (dto.getPayrollIds() == null || dto.getPayrollIds().isEmpty()) {
            throw new IllegalArgumentException("payrollIds list is required and cannot be empty");
        }
        if (dto.getLastDate() == null) {
            throw new IllegalArgumentException("lastDate is required");
        }
        // Step 1: Validate City exists (if provided)
        if (dto.getCityId() != null) {
            cityRepository.findById(dto.getCityId())
                    .orElseThrow(
                            () -> new ResourceNotFoundException("City not found with ID: " + dto.getCityId()));
        }

        // Step 2: Validate all campuses exist and belong to the City (if provided)
        List<Integer> campusIdsToUnmap = dto.getUnmappedCampusIds();
        if (campusIdsToUnmap != null && !campusIdsToUnmap.isEmpty()) {
            for (Integer campusId : campusIdsToUnmap) {
                Campus campus = campusRepository.findByCampusIdAndIsActive(campusId, 1)
                        .orElseThrow(
                                () -> new ResourceNotFoundException("Active Campus not found with ID: " + campusId));

                if (dto.getCityId() != null) {
                    if (campus.getCity() == null || campus.getCity().getCityId() != dto.getCityId()) {
                        throw new ResourceNotFoundException(
                                String.format("Campus with ID %d is not assigned to City with ID %d",
                                        campusId, dto.getCityId()));
                    }
                }
            }
        }

        // Step 3: Process each payrollId
        for (String payrollId : dto.getPayrollIds()) {
            try {
                // Find Employee by payrollId
                Employee employee = findEmployeeByPayrollId(payrollId);

                // Validate Employee is active
                if (employee.getIs_active() != 1) {
                    continue;
                }

                // Get employee's existing campus
                Campus employeeCampus = employee.getCampus_id();

                // Deactivate SharedEmployee records and Primary campus for the specified
                // campuses (if provided)
                if (campusIdsToUnmap != null) {
                    for (Integer campusId : campusIdsToUnmap) {
                        if (employeeCampus != null && employeeCampus.getCampusId() == campusId) {
                            employee.setCampus_id(null);

                            // Deactivate primary campus subjects (EmpSubject table)
                            List<EmpSubject> empSubjects = empSubjectRepository
                                    .findActiveSubjectsByEmpId(employee.getEmp_id());
                            for (EmpSubject es : empSubjects) {
                                es.setIs_active(0);
                                es.setUpdated_by(dto.getUpdatedBy() != null ? dto.getUpdatedBy() : 1);
                                es.setUpdated_date(LocalDateTime.now());
                                empSubjectRepository.save(es);
                            }
                        }

                        List<SharedEmployee> sharedEmployees = sharedEmployeeRepository
                                .findAllByEmpIdAndCampusId(employee.getEmp_id(), campusId);
                        for (SharedEmployee sharedEmployee : sharedEmployees) {
                            if (sharedEmployee.getIsActive() == 1) {
                                sharedEmployee.setIsActive(0);
                                sharedEmployee.setUpdatedBy(dto.getUpdatedBy() != null ? dto.getUpdatedBy() : 1);
                                sharedEmployee.setUpdatedDate(LocalDateTime.now());
                                sharedEmployeeRepository.save(sharedEmployee);
                            }
                        }
                    }
                }

                // Selective Unmap Manager
                if (Boolean.TRUE.equals(dto.getUnmapManager())) {
                    if (employee.getEmployee_manager_id() != null) {
                        Integer managerIdValue = dto.getManagerId();
                        if (managerIdValue == null || managerIdValue == 0 ||
                                employee.getEmployee_manager_id().getEmp_id() == managerIdValue) {
                            employee.setEmployee_manager_id(null);
                        }
                    }
                }

                // Selective Unmap Reporting Manager
                if (Boolean.TRUE.equals(dto.getUnmapReportingManager())) {
                    if (employee.getEmployee_reporting_id() != null) {
                        Integer reportingManagerIdValue = dto.getReportingManagerId();
                        if (reportingManagerIdValue == null || reportingManagerIdValue == 0 ||
                                employee.getEmployee_reporting_id().getEmp_id() == reportingManagerIdValue) {
                            employee.setEmployee_reporting_id(null);
                        }
                    }
                }

                // Update last date of working
                employee.setContract_end_date(dto.getLastDate());

                // Update remarks
                String remarkValue = dto.getRemark();
                if (remarkValue != null && !remarkValue.trim().isEmpty()
                        && !remarkValue.trim().equalsIgnoreCase("null")) {
                    employee.setRemarks(remarkValue.trim());
                } else {
                    employee.setRemarks(null);
                }

                // Update audit fields
                employee.setUpdated_by(dto.getUpdatedBy() != null ? dto.getUpdatedBy() : 1);
                employee.setUpdated_date(LocalDateTime.now());

                employeeRepository.save(employee);

            } catch (Exception e) {
                // Log and continue for other employees
            }
        }

        return dto;
    }

    /**
     * BATCH METHOD: Process multiple employees at once
     */
    public List<EmployeeBatchCampusDTO> getMultipleCampusAddresses(List<String> payrollIds) {
        if (payrollIds == null || payrollIds.isEmpty()) {
            return Collections.emptyList();
        }

        // Fetch all employees in the list at once (handles both temp and permanent IDs)
        List<Employee> employees = employeeRepository.findAllByPayRollIdInOrTempPayrollIdIn(payrollIds);

        return employees.stream()
                .filter(emp -> emp.getIs_active() == 1) // Only process active employees
                .map(emp -> {
                    // Process to get prioritized and consolidated list of active campuses
                    List<EmployeeCampusAddressDTO> flatList = processSingleEmployee(emp, payrollIds, false);

                    EmployeeBatchCampusDTO batchDTO = new EmployeeBatchCampusDTO();

                    // Populate Top-Level Fields strictly from the PRIMARY campus (Employee table)
                    Object matchingMapping = null;
                    EmployeeCampusAddressDTO top = null;
                    if (emp.getCampus_id() != null && Integer.valueOf(1).equals(emp.getCampus_id().getIsActive())) {
                        int primaryCampusId = emp.getCampus_id().getCampusId();

                        // Fetch shared mappings to enrich primary campus details if they exist
                        // PRIORITY 1: CampusEmployee (Role)
                        // PRIORITY 2: SharedEmployee (Subject/Designation)
                        List<com.employee.entity.CampusEmployee> campusEmployees = campusEmployeeRepository
                                .findByEmpId(emp.getEmp_id());
                        List<SharedEmployee> sharedEmployees = sharedEmployeeRepository
                                .findActiveByEmpId(emp.getEmp_id());

                        if (campusEmployees != null) {
                            matchingMapping = campusEmployees.stream()
                                    .filter(ce -> ce.getCmpsId() != null
                                            && ce.getCmpsId().getCampusId() == primaryCampusId)
                                    .findFirst().orElse(null);
                        }

                        if (matchingMapping == null && sharedEmployees != null) {
                            matchingMapping = sharedEmployees.stream()
                                    .filter(se -> se.getCmpsId() != null
                                            && se.getCmpsId().getCampusId() == primaryCampusId)
                                    .findFirst().orElse(null);
                        }

                        // Create detailed DTO for primary campus
                        top = createDTOForCampus(emp, emp.getCampus_id(), payrollIds, false,
                                matchingMapping);

                        batchDTO.setCampusId(top.getCampusId());
                        batchDTO.setCampusName(top.getCampusName());
                        batchDTO.setCityId(top.getCityId());
                        batchDTO.setCity(top.getCity());
                        batchDTO.setFullAddress(top.getFullAddress());
                        batchDTO.setBuildingMobileNo(top.getBuildingMobileNo());
                        batchDTO.setCampusContact(top.getCampusContact());
                        batchDTO.setCampusEmail(top.getCampusEmail());

                    } else {
                        // Fallback if no primary campus
                        batchDTO.setFullAddress("Address: Primary campus not assigned or inactive");
                    }

                    // 1. Employee Basic Info
                    String displayId = emp.getPayRollId();
                    if (displayId == null || (payrollIds != null && !payrollIds.contains(displayId))) {
                        displayId = emp.getTempPayrollId();
                    }
                    batchDTO.setPayrollId(displayId != null ? displayId : emp.getPayRollId());

                    String fullName = emp.getFirst_name();
                    if (emp.getLast_name() != null) {
                        fullName += " " + emp.getLast_name();
                    }
                    batchDTO.setEmployeeName(fullName);
                    batchDTO.setEmployeeMobileNo(String.valueOf(emp.getPrimary_mobile_no()));

                    // 2. Manager Info
                    if (emp.getEmployee_manager_id() != null) {
                        Employee manager = emp.getEmployee_manager_id();
                        batchDTO.setManagerId(manager.getEmp_id());
                        String mgrName = manager.getFirst_name();
                        if (manager.getLast_name() != null) {
                            mgrName += " " + manager.getLast_name();
                        }
                        batchDTO.setManagerName(mgrName);
                        batchDTO.setManagerMobileNo(String.valueOf(manager.getPrimary_mobile_no()));
                    }

                    // 3. Reporting Manager Info
                    if (emp.getEmployee_reporting_id() != null) {
                        Employee rManager = emp.getEmployee_reporting_id();
                        batchDTO.setReportingManagerId(rManager.getEmp_id());
                        String rMgrName = rManager.getFirst_name();
                        if (rManager.getLast_name() != null) {
                            rMgrName += " " + rManager.getLast_name();
                        }
                        batchDTO.setReportingManagerName(rMgrName);
                        batchDTO.setReportingManagerMobileNo(String.valueOf(rManager.getPrimary_mobile_no()));
                    }

                    // 4. Department Info
                    if (emp.getDepartment() != null) {
                        batchDTO.setDepartmentId(emp.getDepartment().getDepartment_id());
                        batchDTO.setDepartmentName(emp.getDepartment().getDepartment_name());
                    }

                    // 5. Designation Info (If not already set from flatList top record)
                    if (batchDTO.getDesignationId() == null && emp.getDesignation() != null) {
                        batchDTO.setDesignationId(emp.getDesignation().getDesignation_id());
                        batchDTO.setDesignationName(emp.getDesignation().getDesignation_name());
                    }

                    // 5.5 Fetch Global Role Info (BEFORE Mapping Promotion to allow Mapping
                    // priority)
                    try {
                        List<String> rolesFromView = employeeRepository.findRoleNameByEmpId(emp.getEmp_id());
                        if (rolesFromView != null && !rolesFromView.isEmpty()) {
                            String roleNameFromView = rolesFromView.get(0);
                            com.employee.entity.Role roleEntity = roleRepository.findByRoleName(roleNameFromView);
                            if (roleEntity != null) {
                                batchDTO.setRoleId(roleEntity.getRoleId());
                                batchDTO.setRole(roleEntity.getRoleName());
                            } else {
                                batchDTO.setRole(roleNameFromView);
                            }
                        }
                    } catch (Exception e) {
                        // Ignore view errors
                    }

                    // 5.9 Promote Mapping Info to Top Level (PRIORITY OVER GLOBAL)
                    if (top != null) {
                        batchDTO.setDesignationId(top.getDesignationId());
                        batchDTO.setDesignationName(top.getDesignationName());
                        batchDTO.setSubjectId(top.getSubjectId());
                        batchDTO.setSubjectName(top.getSubjectName());

                        if (top.getRoleId() != null) {
                            batchDTO.setRoleId(top.getRoleId());
                        }
                        if (top.getRoleName() != null) {
                            batchDTO.setRole(top.getRoleName());
                        }
                    }

                    // 6. Map to CampusDetailDTO for Shared employees
                    List<CampusDetailDTO> details = flatList.stream()
                            .filter(flat -> flat.getCampusId() != null)
                            .map(flat -> {
                                CampusDetailDTO cd = new CampusDetailDTO();
                                cd.setCampusId(flat.getCampusId());
                                cd.setCampusName(flat.getCampusName());
                                cd.setCityId(flat.getCityId());
                                cd.setCity(flat.getCity());
                                cd.setFullAddress(flat.getFullAddress());
                                cd.setBuildingMobileNo(flat.getBuildingMobileNo());
                                cd.setSubjectId(flat.getSubjectId());
                                cd.setSubjectName(flat.getSubjectName());
                                cd.setDesignationId(flat.getDesignationId());
                                cd.setDesignationName(flat.getDesignationName());
                                cd.setRoleId(flat.getRoleId());
                                cd.setRoleName(flat.getRoleName());
                                cd.setCampusContact(flat.getCampusContact());
                                cd.setCampusEmail(flat.getCampusEmail());
                                return cd;
                            })
                            .collect(Collectors.toList());

                    // 7. Set Employee Type and Shared Details logic
                    batchDTO.setCampusDetails(details);
                    // Shared if they have other campuses OR if primary campus has a mapping record
                    if ((details != null && !details.isEmpty())
                            || (batchDTO.getCampusId() != null && matchingMapping != null)) {
                        batchDTO.setEmployeeType("Shared");
                    } else {
                        batchDTO.setEmployeeType("Not Shared");
                    }

                    return batchDTO;
                }).collect(Collectors.toList());
    }

    /**
     * SINGLE METHOD: Process one employee (Existing logic)
     */
    public EmployeeCampusAddressDTO getCampusAddress(String payrollId) {
        Employee emp = findEmployeeByPayrollId(payrollId);
        List<EmployeeCampusAddressDTO> results = processSingleEmployee(emp, Collections.singletonList(payrollId), true);
        return results.isEmpty() ? new EmployeeCampusAddressDTO() : results.get(0);
    }

    /**
     * CORE LOGIC: Converts Employee Entity to DTO
     */
    /**
     * CORE LOGIC: Converts Employee Entity to List of DTOs (Handling Multiple
     * Campuses)
     */
    private List<EmployeeCampusAddressDTO> processSingleEmployee(Employee emp, List<String> inputIds,
            boolean includeFullDetails) {
        // Use a list to maintain order and allow multiple mappings per campus
        List<EmployeeCampusAddressDTO> dtoList = new ArrayList<>();

        // 0. Fetch mapping records upfront
        List<com.employee.entity.CampusEmployee> campusEmployees = campusEmployeeRepository
                .findByEmpId(emp.getEmp_id());
        List<SharedEmployee> sharedEmployees = sharedEmployeeRepository.findActiveByEmpId(emp.getEmp_id());

        // 1. Add CampusEmployee mappings (Active only)
        // Note: We no longer exclude the primary campus to ensure all mappings are
        // "observed"
        if (campusEmployees != null) {
            for (com.employee.entity.CampusEmployee ce : campusEmployees) {
                if (ce.getCmpsId() != null && Integer.valueOf(1).equals(ce.getCmpsId().getIsActive())
                        && Integer.valueOf(1).equals(ce.getIsActive())) {
                    dtoList.add(createDTOForCampus(emp, ce.getCmpsId(), inputIds, includeFullDetails, ce));
                }
            }
        }

        // 2. Add SharedEmployee mappings (Active only)
        if (sharedEmployees != null) {
            for (SharedEmployee se : sharedEmployees) {
                if (se.getCmpsId() != null && Integer.valueOf(1).equals(se.getCmpsId().getIsActive())
                        && Integer.valueOf(1).equals(se.getIsActive())) {
                    dtoList.add(createDTOForCampus(emp, se.getCmpsId(), inputIds, includeFullDetails, se));
                }
            }
        }

        // Fallback for no campus assigned
        if (dtoList.isEmpty()) {
            EmployeeCampusAddressDTO dto = createBaseDTO(emp, inputIds, includeFullDetails);
            dto.setFullAddress("Address: Campus not assigned");
            dtoList.add(dto);
        }

        return dtoList;
    }

    private EmployeeCampusAddressDTO createDTOForCampus(Employee emp, Campus campus, List<String> inputIds,
            boolean includeFullDetails, Object mappingEntity) {
        EmployeeCampusAddressDTO dto = createBaseDTO(emp, inputIds, includeFullDetails);

        try {
            Integer campusId = campus.getCampusId();
            dto.setCampusId(campusId);
            dto.setCampusName(campus.getCampusName());

            // Extract details from mapping entity (SharedEmployee or CampusEmployee)
            if (mappingEntity instanceof SharedEmployee) {
                SharedEmployee se = (SharedEmployee) mappingEntity;
                if (se.getSubjectId() != null) {
                    dto.setSubjectId(se.getSubjectId().getSubject_id());
                    dto.setSubjectName(se.getSubjectId().getSubject_name());
                }
                if (se.getDesignationId() != null) {
                    dto.setDesignationId(se.getDesignationId().getDesignation_id());
                    dto.setDesignationName(se.getDesignationId().getDesignation_name());
                }

            } else if (mappingEntity instanceof com.employee.entity.CampusEmployee) {
                com.employee.entity.CampusEmployee ce = (com.employee.entity.CampusEmployee) mappingEntity;
                if (ce.getRoleId() != null) {
                    dto.setRoleId(ce.getRoleId());
                    // Fetch Role Name if possible
                    try {
                        roleRepository.findById(ce.getRoleId()).ifPresent(role -> {
                            dto.setRoleName(role.getRoleName());
                        });
                    } catch (Exception e) {
                        // Ignore
                    }
                }
                // CampusEmployee doesn't have Subject/Designation, fall back to Employee's
                // primary
                if (emp.getDesignation() != null) {
                    dto.setDesignationId(emp.getDesignation().getDesignation_id());
                    dto.setDesignationName(emp.getDesignation().getDesignation_name());
                }
            } else {
                // Fallback for primary campus (null mappingEntity)
                if (emp.getDesignation() != null) {
                    dto.setDesignationId(emp.getDesignation().getDesignation_id());
                    dto.setDesignationName(emp.getDesignation().getDesignation_name());
                }

            }

            // Populate City from Campus
            if (campus.getCity() != null) {
                dto.setCity(campus.getCity().getCityName());
                dto.setCityId(campus.getCity().getCityId());
            } else {
                // Try fetching if lazy loaded or null
                City result = campusRepository.findByCampusId(campusId);
                if (result != null) {
                    dto.setCity(result.getCityName());
                    dto.setCityId(result.getCityId());
                }
            }

            // Fetch buildings
            List<Building> buildings = buildingRepository.findBuildingsByCampusId(campusId);
            if (buildings == null || buildings.isEmpty()) {
                dto.setFullAddress("Address: No buildings found for campus");
                return dto;
            }

            // Pick main building logic
            Building selectedBuilding = buildings.stream()
                    .filter(b -> b.getIsMainBuilding() == 1)
                    .findFirst()
                    .orElse(buildings.get(0));

            // Fetch address
            BuildingAddress buildingAddress = buildingAddressRepository.findByBuildingIdAndAddressType(
                    selectedBuilding.getBuildingId(), "address");

            if (buildingAddress == null) {
                buildingAddress = buildingAddressRepository.findAddressByBuildingId(selectedBuilding.getBuildingId());
            }

            if (buildingAddress == null) {
                dto.setFullAddress("Address: No address record found for building");
                return dto;
            }

            // Build Address String
            StringJoiner sj = new StringJoiner(", ");
            if (buildingAddress.getPlot_no() != null)
                sj.add(buildingAddress.getPlot_no());
            if (buildingAddress.getArea() != null)
                sj.add(buildingAddress.getArea());
            if (buildingAddress.getStreet() != null)
                sj.add(buildingAddress.getStreet());
            if (buildingAddress.getLandmark() != null)
                sj.add(buildingAddress.getLandmark());
            if (buildingAddress.getPin_code() != null)
                sj.add(String.valueOf(buildingAddress.getPin_code()));

            dto.setFullAddress(sj.toString());
            dto.setBuildingMobileNo(buildingAddress.getMobile_no());

            // Populate campusContact with building mobile number as per new instruction
            dto.setCampusContact(buildingAddress.getMobile_no());
            // campusEmail remains null as it's not in the building address table

        } catch (Exception e) {
            dto.setFullAddress("Address Error: " + e.getMessage());
        }

        return dto;
    }

    private EmployeeCampusAddressDTO createBaseDTO(Employee emp, List<String> inputIds, boolean includeFullDetails) {
        EmployeeCampusAddressDTO dto = new EmployeeCampusAddressDTO();

        // 1. Core ID and Basic Info
        String displayId = emp.getPayRollId();
        if (displayId == null || (inputIds != null && !inputIds.contains(displayId))) {
            displayId = emp.getTempPayrollId();
        }
        dto.setPayrollId(displayId != null ? displayId : emp.getPayRollId());

        // Populate Employee Name (Always useful)
        String fullName = emp.getFirst_name();
        if (emp.getLast_name() != null) {
            fullName += " " + emp.getLast_name();
        }
        dto.setEmployeeName(fullName);

        if (includeFullDetails) {
            // 2. Populate Employee Mobile and Manager Info
            dto.setEmployeeMobileNo(
                    emp.getPrimary_mobile_no() != 0 ? String.valueOf(emp.getPrimary_mobile_no()) : null);

            if (emp.getEmployee_manager_id() != null) {
                dto.setManagerId(emp.getEmployee_manager_id().getEmp_id());
            }

            if (emp.getEmployee_reporting_id() != null) {
                dto.setReportingManagerId(emp.getEmployee_reporting_id().getEmp_id());
            }

            // Additional manager text info
            mapManagerInfo(emp, dto);

            // Populate Department Info
            if (emp.getDepartment() != null) {
                dto.setDepartmentId(emp.getDepartment().getDepartment_id());
                dto.setDepartmentName(emp.getDepartment().getDepartment_name());
            }

            // Populate Designation Info
            if (emp.getDesignation() != null) {
                dto.setDesignationId(emp.getDesignation().getDesignation_id());
                dto.setDesignationName(emp.getDesignation().getDesignation_name());
            }

            // Populate Employee Type Info
            if (emp.getEmployee_type_id() != null) {
                dto.setEmployeeTypeId(emp.getEmployee_type_id().getEmp_type_id());
                dto.setEmployeeTypeName(emp.getEmployee_type_id().getEmp_type());
            }
        }

        return dto;
    }

    private void mapManagerInfo(Employee emp, EmployeeCampusAddressDTO dto) {
        // Direct Manager
        if (emp.getEmployee_manager_id() != null) {
            Employee manager = emp.getEmployee_manager_id();
            dto.setManagerId(manager.getEmp_id());

            String name = manager.getFirst_name();
            if (manager.getLast_name() != null) {
                name += " " + manager.getLast_name();
            }
            dto.setManagerName(name);

            long mob = manager.getPrimary_mobile_no();
            dto.setManagerMobileNo(mob != 0 ? String.valueOf(mob) : null);
        }

        // Reporting Manager
        if (emp.getEmployee_reporting_id() != null) {
            Employee reportingManager = emp.getEmployee_reporting_id();
            dto.setReportingManagerId(reportingManager.getEmp_id());

            String rName = reportingManager.getFirst_name();
            if (reportingManager.getLast_name() != null) {
                rName += " " + reportingManager.getLast_name();
            }
            dto.setReportingManagerName(rName);

            long rMob = reportingManager.getPrimary_mobile_no();
            dto.setReportingManagerMobileNo(rMob != 0 ? String.valueOf(rMob) : null);
        }
    }

    /**
     * Updates the name of a campus.
     */
    public Campus updateCampusName(Integer campusId, String campusName) {
        Campus campus = campusRepository.findById(campusId)
                .orElseThrow(() -> new ResourceNotFoundException("Campus not found with ID: " + campusId));
        campus.setCampusName(campusName);
        return campusRepository.save(campus);
    }

    /**
     * Saves a list of CampusEmployee records.
     * 
     * @param dtos The list of DTOs containing data to save
     * @return The list of saved DTOs
     */
    @Transactional
    public List<com.employee.dto.CampusEmployeeDTO> saveCampusEmployees(List<com.employee.dto.CampusEmployeeDTO> dtos) {
        if (dtos == null || dtos.isEmpty()) {
            throw new IllegalArgumentException("Input list cannot be empty");
        }

        List<com.employee.dto.CampusEmployeeDTO> savedDtos = new ArrayList<>();

        for (com.employee.dto.CampusEmployeeDTO dto : dtos) {
            if (dto.getPayrollId() == null || dto.getPayrollId().trim().isEmpty()) {
                throw new IllegalArgumentException("Payroll ID is required");
            }
            if (dto.getCmpsId() == null) {
                throw new IllegalArgumentException("cmpsId is required");
            }

            Employee employee = employeeRepository.findByPayrollId(dto.getPayrollId())
                    .orElseGet(() -> employeeRepository.findByTempPayrollId(dto.getPayrollId())
                            .orElseThrow(() -> new ResourceNotFoundException(
                                    "Employee not found with Payroll ID: " + dto.getPayrollId())));

            Campus campus = campusRepository.findById(dto.getCmpsId())
                    .orElseThrow(() -> new ResourceNotFoundException("Campus not found with ID: " + dto.getCmpsId()));

            com.employee.entity.CampusEmployee entity = new com.employee.entity.CampusEmployee();
            entity.setEmpId(employee);
            entity.setCmpsId(campus);
            entity.setRoleId(dto.getRoleId());
            entity.setAttendanceStatus(dto.getAttendanceStatus());
            entity.setIsActive(1);
            entity.setCreatedBy(dto.getCreatedBy() != null ? dto.getCreatedBy() : 1);
            entity.setUpdatedBy(dto.getUpdatedBy());

            // createdDate and updatedDate are handled by DB defaults or JPA listeners

            campusEmployeeRepository.save(entity);

            savedDtos.add(dto);
        }
        return savedDtos;
    }

    /**
     * Selectively unmaps manager, reporting manager, and/or shared campuses based
     * on boolean flags.
     * 
     * This method provides fine-grained control over what gets unmapped:
     * - If unmapManager is true: Sets employee_manager_id to null
     * - If unmapReportingManager is true: Sets employee_reporting_id to null
     * - If unmapSharedCampuses is true: Deactivates shared campus assignments
     * - If campusIds is provided: Only deactivates those specific campuses
     * - If campusIds is null/empty: Deactivates ALL shared campuses
     * 
     * @param dto The selective unmapping request DTO with boolean flags
     * @return The same SelectiveUnmappingDTO that was passed in
     * @throws ResourceNotFoundException if employee not found or not active
     * @throws IllegalArgumentException  if required fields are missing
     */
    @Transactional
    public SelectiveUnmappingDTO selectiveUnmapEmployee(SelectiveUnmappingDTO dto) {
        // Validate required fields
        if (dto == null) {
            throw new IllegalArgumentException("SelectiveUnmappingDTO cannot be null");
        }
        if (dto.getPayrollId() == null || dto.getPayrollId().trim().isEmpty()) {
            throw new IllegalArgumentException("payrollId is required");
        }

        // Find Employee by payrollId
        Employee employee = findEmployeeByPayrollId(dto.getPayrollId());

        // Validate Employee is active
        if (employee.getIs_active() != 1) {
            throw new ResourceNotFoundException(
                    "Employee with payrollId " + dto.getPayrollId() + " is not active");
        }

        boolean employeeChanged = false;

        // Step 1: Unmap Manager if flag is true
        if (Boolean.TRUE.equals(dto.getUnmapManager())) {
            if (employee.getEmployee_manager_id() == null) {
                throw new ResourceNotFoundException(
                        "Cannot unmap manager: Employee with payrollId '" + dto.getPayrollId() +
                                "' does not have a manager assigned");
            }

            // If managerId is provided, validate it matches the current manager
            if (dto.getManagerId() != null) {
                Integer currentManagerId = employee.getEmployee_manager_id().getEmp_id();
                if (!currentManagerId.equals(dto.getManagerId())) {
                    throw new ResourceNotFoundException(
                            "Cannot unmap manager: Employee's current manager ID (" + currentManagerId +
                                    ") does not match the provided manager ID (" + dto.getManagerId() + ")");
                }
            }

            employee.setEmployee_manager_id(null);
            employeeChanged = true;
        }

        // Step 2: Unmap Reporting Manager if flag is true
        if (Boolean.TRUE.equals(dto.getUnmapReportingManager())) {
            if (employee.getEmployee_reporting_id() == null) {
                throw new ResourceNotFoundException(
                        "Cannot unmap reporting manager: Employee with payrollId '" + dto.getPayrollId() +
                                "' does not have a reporting manager assigned");
            }

            // If reportingManagerId is provided, validate it matches the current reporting
            // manager
            if (dto.getReportingManagerId() != null) {
                Integer currentReportingManagerId = employee.getEmployee_reporting_id().getEmp_id();
                if (!currentReportingManagerId.equals(dto.getReportingManagerId())) {
                    throw new ResourceNotFoundException(
                            "Cannot unmap reporting manager: Employee's current reporting manager ID (" +
                                    currentReportingManagerId + ") does not match the provided reporting manager ID (" +
                                    dto.getReportingManagerId() + ")");
                }
            }

            employee.setEmployee_reporting_id(null);
            employeeChanged = true;
        }

        // Step 3: Unmap Shared Campuses if campusIds provided
        // Simply providing campusIds in the request is enough to trigger unmapping
        if (dto.getUnmappedCampusIds() != null && !dto.getUnmappedCampusIds().isEmpty()) {
            for (Integer campusId : dto.getUnmappedCampusIds()) {
                // Check if this campus is the primary campus
                if (employee.getCampus_id() != null &&
                        employee.getCampus_id().getCampusId() == campusId) {
                    employee.setCampus_id(null);
                    employeeChanged = true;
                }

                // Deactivate shared campus records
                List<SharedEmployee> sharedEmployees = sharedEmployeeRepository
                        .findAllByEmpIdAndCampusId(employee.getEmp_id(), campusId);

                for (SharedEmployee sharedEmployee : sharedEmployees) {
                    if (sharedEmployee.getIsActive() == 1) {
                        sharedEmployee.setIsActive(0);
                        sharedEmployee.setUpdatedBy(dto.getUpdatedBy() != null ? dto.getUpdatedBy() : 1);
                        sharedEmployee.setUpdatedDate(LocalDateTime.now());
                        sharedEmployeeRepository.save(sharedEmployee);
                    }
                }
            }
        }

        // Step 4: Update remarks if provided
        String remarkValue = dto.getRemark();
        if (remarkValue != null && !remarkValue.trim().isEmpty() && !remarkValue.trim().equalsIgnoreCase("null")) {
            employee.setRemarks(remarkValue.trim());
            employeeChanged = true;
        }

        // Step 5: Update audit fields if employee changed
        if (employeeChanged) {
            employee.setUpdated_by(dto.getUpdatedBy() != null ? dto.getUpdatedBy() : 1);
            employee.setUpdated_date(LocalDateTime.now());
            employeeRepository.save(employee);
        }

        return dto;
    }

    /**
     * Complete Unassign - Unassigns ALL assignments from an employee
     * 
     * This method:
     * 1. Collects current assignments (for frontend auto-population)
     * 2. Sets manager to null
     * 3. Sets reporting manager to null
     * 4. Sets primary campus to null
     * 5. Deactivates ALL shared campus records
     * 6. Updates remarks and audit fields
     * 
     * @param dto CompleteUnassignDTO containing payrollId, remark, and updatedBy
     * @return CompleteUnassignResponseDTO with current assignments and success
     *         message
     */
    @Transactional
    public CompleteUnassignResponseDTO completeUnassign(CompleteUnassignDTO dto) {
        // Validate required fields
        if (dto == null) {
            throw new IllegalArgumentException("CompleteUnassignDTO cannot be null");
        }
        if (dto.getPayrollId() == null || dto.getPayrollId().trim().isEmpty()) {
            throw new IllegalArgumentException("payrollId is required");
        }

        // Find Employee by payrollId
        Employee employee = findEmployeeByPayrollId(dto.getPayrollId());

        // Validate Employee is active
        if (employee.getIs_active() != 1) {
            throw new ResourceNotFoundException(
                    "Employee with payrollId " + dto.getPayrollId() + " is not active");
        }

        // Prepare response DTO
        CompleteUnassignResponseDTO response = new CompleteUnassignResponseDTO();
        response.setPayrollId(dto.getPayrollId());

        // Step 1: Collect current manager information
        if (employee.getEmployee_manager_id() != null) {
            response.setManagerId(employee.getEmployee_manager_id().getEmp_id());
            response.setManagerName(employee.getEmployee_manager_id().getFirst_name() + " " +
                    employee.getEmployee_manager_id().getLast_name());
        }

        // Step 2: Collect current reporting manager information
        if (employee.getEmployee_reporting_id() != null) {
            response.setReportingManagerId(employee.getEmployee_reporting_id().getEmp_id());
            response.setReportingManagerName(employee.getEmployee_reporting_id().getFirst_name() + " " +
                    employee.getEmployee_reporting_id().getLast_name());
        }

        // Step 3: Collect current primary campus information
        if (employee.getCampus_id() != null) {
            response.setPrimaryCampusId(employee.getCampus_id().getCampusId());
            response.setPrimaryCampusName(employee.getCampus_id().getCampusName());
        }

        // Step 4: Collect shared campus information
        List<SharedEmployee> sharedEmployees = sharedEmployeeRepository.findActiveByEmpId(employee.getEmp_id());
        List<CompleteUnassignResponseDTO.SharedCampusInfo> sharedCampusList = new ArrayList<>();

        for (SharedEmployee sharedEmployee : sharedEmployees) {
            if (sharedEmployee.getCmpsId() != null) {
                CompleteUnassignResponseDTO.SharedCampusInfo campusInfo = new CompleteUnassignResponseDTO.SharedCampusInfo();
                campusInfo.setCampusId(sharedEmployee.getCmpsId().getCampusId());
                campusInfo.setCampusName(sharedEmployee.getCmpsId().getCampusName());
                sharedCampusList.add(campusInfo);
            }
        }
        response.setSharedCampuses(sharedCampusList);

        // Step 4b: Collect campus role information (sce_cmps_emp)
        List<CampusEmployee> campusEmployees = campusEmployeeRepository.findByEmpId(employee.getEmp_id());
        List<CompleteUnassignResponseDTO.SharedCampusInfo> campusRoleList = new ArrayList<>();
        for (CampusEmployee ce : campusEmployees) {
            if (ce.getCmpsId() != null) {
                CompleteUnassignResponseDTO.SharedCampusInfo roleInfo = new CompleteUnassignResponseDTO.SharedCampusInfo();
                roleInfo.setCampusId(ce.getCmpsId().getCampusId());
                roleInfo.setCampusName(ce.getCmpsId().getCampusName());
                campusRoleList.add(roleInfo);
            }
        }
        response.setCampusRoles(campusRoleList);

        // Step 5: Perform complete unassignment
        boolean employeeChanged = false;

        // Unassign manager
        if (employee.getEmployee_manager_id() != null) {
            employee.setEmployee_manager_id(null);
            employeeChanged = true;
        }

        // Unassign reporting manager
        if (employee.getEmployee_reporting_id() != null) {
            employee.setEmployee_reporting_id(null);
            employeeChanged = true;
        }

        // Unassign primary campus
        if (employee.getCampus_id() != null) {
            employee.setCampus_id(null);
            employeeChanged = true;

            // Deactivate primary campus subjects (EmpSubject table)
            List<EmpSubject> empSubjects = empSubjectRepository.findActiveSubjectsByEmpId(employee.getEmp_id());
            for (EmpSubject es : empSubjects) {
                es.setIs_active(0);
                es.setUpdated_by(dto.getUpdatedBy() != null ? dto.getUpdatedBy() : 1);
                es.setUpdated_date(LocalDateTime.now());
                empSubjectRepository.save(es);
            }
        }

        // Deactivate ALL shared campuses
        for (SharedEmployee sharedEmployee : sharedEmployees) {
            sharedEmployee.setIsActive(0);
            sharedEmployee.setUpdatedBy(dto.getUpdatedBy() != null ? dto.getUpdatedBy() : 1);
            sharedEmployee.setUpdatedDate(LocalDateTime.now());
            sharedEmployeeRepository.save(sharedEmployee);
        }

        // Deactivate ALL campus employees (sce_cmps_emp)
        for (CampusEmployee ce : campusEmployees) {
            ce.setIsActive(0);
            ce.setUpdatedBy(dto.getUpdatedBy() != null ? dto.getUpdatedBy() : 1);
            ce.setUpdatedDate(LocalDateTime.now());
            campusEmployeeRepository.save(ce);
            employeeChanged = true;
        }

        // Step 6: Update remarks if provided
        String remarkValue = dto.getRemark();
        if (remarkValue != null && !remarkValue.trim().isEmpty() && !remarkValue.trim().equalsIgnoreCase("null")) {
            employee.setRemarks(remarkValue.trim());
            employeeChanged = true;
        }

        // Step 7: Update audit fields if employee changed
        if (employeeChanged) {
            employee.setUpdated_by(dto.getUpdatedBy() != null ? dto.getUpdatedBy() : 1);
            employee.setUpdated_date(LocalDateTime.now());
            employeeRepository.save(employee);
        }
        // Set success message
        response.setMessage(
                "Successfully unassigned all assignments for employee with payrollId: " + dto.getPayrollId());

        return response;
    }

    /**
     * Bulk Complete Unassign - Unassigns ALL assignments from multiple employees.
     * 
     * @param dto BulkCompleteUnassignDTO containing payrollIds, remark, and
     *            updatedBy
     * @return List of CompleteUnassignResponseDTO for each processed employee
     */
    @Transactional
    public List<CompleteUnassignResponseDTO> bulkCompleteUnmap(BulkCompleteUnassignDTO dto) {
        // Validate required fields
        if (dto == null) {
            throw new IllegalArgumentException("BulkCompleteUnassignDTO cannot be null");
        }
        if (dto.getPayrollIds() == null || dto.getPayrollIds().isEmpty()) {
            throw new IllegalArgumentException("payrollIds list is required and cannot be empty");
        }

        List<CompleteUnassignResponseDTO> results = new ArrayList<>();

        for (String payrollId : dto.getPayrollIds()) {
            try {
                // Reuse existing completeUnassign logic instead of duplicating
                CompleteUnassignDTO singleDto = new CompleteUnassignDTO();
                singleDto.setPayrollId(payrollId);
                singleDto.setRemark(dto.getRemark());
                singleDto.setUpdatedBy(dto.getUpdatedBy());

                CompleteUnassignResponseDTO singleResponse = completeUnassign(singleDto);
                results.add(singleResponse);
            } catch (Exception e) {
                // Create error response for this specific employee
                CompleteUnassignResponseDTO errorResponse = new CompleteUnassignResponseDTO();
                errorResponse.setPayrollId(payrollId);
                errorResponse.setMessage("Failed: " + e.getMessage());
                results.add(errorResponse);
            }
        }

        return results;
    }
}