package com.employee.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.employee.dto.AddressInfoDTO;
import com.employee.dto.BasicInfoDTO;
import com.employee.dto.FamilyInfoDTO;
import com.employee.dto.PreviousEmployerInfoDTO;
import com.employee.entity.Building;
import com.employee.entity.EmpaddressInfo;
import com.employee.entity.EmpDetails;
import com.employee.entity.EmpDocuments;
import com.employee.entity.EmpExperienceDetails;
import com.employee.entity.EmpFamilyDetails;
import com.employee.entity.EmpPfDetails;
import com.employee.entity.Employee;
import com.employee.entity.EmployeeCheckListStatus;
import com.employee.entity.EmployeeStatus;
import com.employee.exception.ResourceNotFoundException;
import com.employee.repository.BloodGroupRepository;
import com.employee.repository.BuildingRepository;
import com.employee.repository.CampusRepository;
import com.employee.repository.CasteRepository;
import com.employee.repository.CategoryRepository;
import com.employee.repository.CityRepository;
import com.employee.repository.CountryRepository;
import com.employee.repository.DistrictRepository;
import com.employee.repository.EmpDetailsRepository;
import com.employee.repository.EmpDocTypeRepository;
import com.employee.repository.EmpDocumentsRepository;
import com.employee.repository.EmpExperienceDetailsRepository;
import com.employee.repository.EmpFamilyDetailsRepository;
import com.employee.repository.EmpPfDetailsRepository;
import com.employee.repository.EmpaddressInfoRepository;
import com.employee.repository.EmployeeCheckListStatusRepository;
import com.employee.repository.EmployeeRepository;
import com.employee.repository.EmployeeStatusRepository;
import com.employee.repository.EmployeeTypeRepository;
import com.employee.repository.GenderRepository;
import com.employee.repository.JoiningAsRepository;
import com.employee.repository.MaritalStatusRepository;
import com.employee.repository.ModeOfHiringRepository;
import com.employee.repository.RelationRepository;
import com.employee.repository.RelegionRepository;
import com.employee.repository.StateRepository;
import com.employee.repository.WorkingModeRepository;
import com.employee.repository.OccupationRepository;
import com.employee.repository.QualificationRepository;
import com.employee.repository.EmployeeTypeHiringRepository;

/**
 * Service for handling Basic Info related tabs (4 APIs).
 * Contains: Basic Info, Address Info, Family Info, Previous Employer Info
 * * This service is completely independent and does not use
 * EmployeeEntityPreparationService.
 * All helper methods are implemented directly within this service.
 */
@Service
@Transactional
public class EmployeeBasicInfoTabService {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeBasicInfoTabService.class);

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private QualificationRepository qualificationRepository;

    @Autowired
    private EmpDetailsRepository empDetailsRepository;

    @Autowired
    private EmpPfDetailsRepository empPfDetailsRepository;

    @Autowired
    private EmpDocumentsRepository empDocumentsRepository;

    @Autowired
    private EmployeeCheckListStatusRepository employeeCheckListStatusRepository;

    @Autowired
    private EmployeeStatusRepository employeeStatusRepository;

    @Autowired
    private EmpaddressInfoRepository empaddressInfoRepository;

    @Autowired
    private EmpFamilyDetailsRepository empFamilyDetailsRepository;

    @Autowired
    private EmpExperienceDetailsRepository empExperienceDetailsRepository;

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private StateRepository stateRepository;

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private DistrictRepository districtRepository;

    @Autowired
    private RelationRepository relationRepository;

    @Autowired
    private GenderRepository genderRepository;

    @Autowired
    private BloodGroupRepository bloodGroupRepository;

    @Autowired
    private EmpDocTypeRepository empDocTypeRepository;

    @Autowired
    private CampusRepository campusRepository;

    @Autowired
    private BuildingRepository buildingRepository;

    @Autowired
    private EmployeeTypeRepository employeeTypeRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private MaritalStatusRepository maritalStatusRepository;

    @Autowired
    private WorkingModeRepository workingModeRepository;

    @Autowired
    private JoiningAsRepository joiningAsRepository;

    @Autowired
    private ModeOfHiringRepository modeOfHiringRepository;

    @Autowired
    private CasteRepository casteRepository;

    @Autowired
    private RelegionRepository relegionRepository;

    @Autowired
    private OccupationRepository occupationRepository;

    @Autowired
    private EmployeeTypeHiringRepository employeeTypeHiringRepository;

    // ============================================================================
    // API METHODS (4 APIs)
    // ============================================================================

    /**
     * API 1: Save Basic Info (Tab 1)
     * Creates or updates Employee, EmpDetails, and EmpPfDetails
     * * @param basicInfo Basic Info DTO (contains empId and tempPayrollId)
     * 
     * @return Saved BasicInfoDTO with empId
     */
    public BasicInfoDTO saveBasicInfo(BasicInfoDTO basicInfo) {
        Integer empId = basicInfo.getEmpId();
        String tempPayrollId = basicInfo.getTempPayrollId();
        logger.info("Saving Basic Info for empId: {}, tempPayrollId: {}", empId, tempPayrollId);

        try {
            // Step 1: Validate DTO data BEFORE any database operations
            validateBasicInfo(basicInfo, tempPayrollId);
        } catch (Exception e) {
            logger.error("❌ ERROR: Basic Info validation failed. NO ID consumed. Error: {}", e.getMessage(), e);
            throw e;
        }

        Employee employee = null;
        boolean isUpdate = false;

        // Step 2: Check if employee exists (read-only operation)
        if (empId != null && empId > 0) {
            employee = employeeRepository.findById(empId)
                    .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + empId));
            // Validate that employee is active before allowing updates
            if (employee.getIs_active() != 1) {
                throw new ResourceNotFoundException(
                        "Cannot update employee with ID: " + empId +
                                ". Employee is inactive (is_active = 0). Only active employees can be updated.");
            }
            isUpdate = true;
            logger.info("UPDATE MODE: Updating existing active employee (emp_id: {})", empId);
        } else if (tempPayrollId != null && !tempPayrollId.trim().isEmpty()) {
            Optional<Employee> existing = employeeRepository.findByTempPayrollId(tempPayrollId.trim());
            if (existing.isPresent()) {
                employee = existing.get();
                // Validate that employee is active before allowing updates
                if (employee.getIs_active() != 1) {
                    throw new ResourceNotFoundException(
                            "Cannot update employee with tempPayrollId: " + tempPayrollId +
                                    ". Employee is inactive (is_active = 0). Only active employees can be updated.");
                }
                isUpdate = true;
                logger.info("UPDATE MODE: Found existing active employee by tempPayrollId: {}", tempPayrollId);
            } else {
                isUpdate = false;
                logger.info("INSERT MODE: Creating new employee with tempPayrollId: {}", tempPayrollId);
            }
        } else {
            throw new ResourceNotFoundException("Either empId or tempPayrollId must be provided");
        }

        try {
            // Step 3: Prepare all entities in memory (NO database writes yet)
            if (!isUpdate) {
                // INSERT MODE: Create new employee entity
                employee = prepareEmployeeEntity(basicInfo);
                employee.setTempPayrollId(tempPayrollId.trim());
                setIncompletedStatus(employee);
            } else {
                // UPDATE MODE: Update existing employee entity
                updateEmployeeEntity(employee, basicInfo);
                // Set updated_by and updated_date for every change
                if (basicInfo.getUpdatedBy() != null && basicInfo.getUpdatedBy() > 0) {
                    employee.setUpdated_by(basicInfo.getUpdatedBy());
                    employee.setUpdated_date(LocalDateTime.now());
                } else if (basicInfo.getCreatedBy() != null && basicInfo.getCreatedBy() > 0) {
                    // Fallback to createdBy if updatedBy not provided on update
                    employee.setUpdated_by(basicInfo.getCreatedBy());
                    employee.setUpdated_date(LocalDateTime.now());
                }
            }

            // Prepare EmpDetails and EmpPfDetails entities (in memory only)
            EmpDetails empDetails = prepareEmpDetailsEntity(basicInfo, employee);
            EmpPfDetails empPfDetails = prepareEmpPfDetailsEntity(basicInfo, employee);

            // Step 4: Validate ALL prepared entities BEFORE saving (prevents emp_id
            // sequence consumption on failure)
            validatePreparedEntities(employee, empDetails, empPfDetails);
            validateEntityConstraints(employee, empDetails, empPfDetails);

            if (employee == null) {
                throw new ResourceNotFoundException("Failed to prepare employee entity.");
            }
            // Step 5: Save to database ONLY after all validations pass
            employee = employeeRepository.save(employee);
            logger.info("✅ Employee ID {} {} - proceeding with child entity saves",
                    isUpdate ? "updated" : "generated and consumed from sequence", employee.getEmp_id());

            // Save EmpDetails
            empDetails.setEmployee_id(employee);
            Integer auditUser = isUpdate
                    ? (basicInfo.getUpdatedBy() != null && basicInfo.getUpdatedBy() > 0 ? basicInfo.getUpdatedBy()
                            : basicInfo.getCreatedBy())
                    : basicInfo.getCreatedBy();
            saveEmpDetailsEntity(empDetails, employee, auditUser);

            // Save EmpPfDetails
            if (empPfDetails != null) {
                empPfDetails.setEmployee_id(employee);
                saveEmpPfDetailsEntity(empPfDetails, employee, auditUser);
            }

            basicInfo.setEmpId(employee.getEmp_id());
            logger.info("✅ Basic Info saved successfully for emp_id: {}", employee.getEmp_id());
            return basicInfo;

        } catch (Exception e) {
            if (employee != null && employee.getEmp_id() > 0) {
                logger.error(
                        "❌ ERROR: Basic Info save failed AFTER ID consumption. Employee ID {} was consumed but transaction rolled back. Root cause: {}",
                        employee.getEmp_id(), e.getMessage(), e);
            } else {
                logger.error("❌ ERROR: Basic Info save failed DURING PREPARATION. NO ID consumed. Error: {}",
                        e.getMessage(), e);
            }
            throw e;
        }
    }

    /**
     * API 2: Save Address Info (Tab 2)
     * * @param tempPayrollId Temp Payroll ID
     * 
     * @param addressInfo Address Info DTO
     * @return Saved AddressInfoDTO object
     */
    public AddressInfoDTO saveAddressInfo(String tempPayrollId, AddressInfoDTO addressInfo) {
        logger.info("Saving Address Info for tempPayrollId: {}", tempPayrollId);

        try {
            // Step 1: Validate DTO data BEFORE any database operations
            validateAddressInfo(addressInfo);
        } catch (Exception e) {
            logger.error("❌ ERROR: Address Info validation failed. NO data saved. Error: {}", e.getMessage(), e);
            throw e;
        }

        try {
            // Step 2: Find employee (read-only operation)
            Employee employee = findEmployeeByTempPayrollId(tempPayrollId);

            // Step 3: Save to database ONLY after all validations pass
            Integer createdBy = addressInfo.getCreatedBy();
            Integer updatedBy = addressInfo.getUpdatedBy();
            int count = saveAddressEntities(employee, addressInfo, createdBy, updatedBy);

            logger.info("✅ Saved {} address records for emp_id: {} (tempPayrollId: {})",
                    count, employee.getEmp_id(), tempPayrollId);

            // Return the saved DTO object
            return addressInfo;

        } catch (Exception e) {
            logger.error("❌ ERROR: Address Info save failed. Error: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * API 3: Save Family Info (Tab 3)
     * * @param tempPayrollId Temp Payroll ID
     * 
     * @param familyInfo Family Info DTO
     * @return Saved FamilyInfoDTO object
     */
    public FamilyInfoDTO saveFamilyInfo(String tempPayrollId, FamilyInfoDTO familyInfo) {
        logger.info("Saving Family Info for tempPayrollId: {}", tempPayrollId);

        try {
            // Step 1: Validate DTO data BEFORE any database operations
            validateFamilyInfo(familyInfo);
        } catch (Exception e) {
            logger.error("❌ ERROR: Family Info validation failed. NO data saved. Error: {}", e.getMessage(), e);
            throw e;
        }

        try {
            // Step 2: Find employee (read-only operation)
            Employee employee = findEmployeeByTempPayrollId(tempPayrollId);

            // Step 3: Save to database ONLY after all validations pass
            Integer createdBy = familyInfo.getCreatedBy();
            Integer updatedBy = familyInfo.getUpdatedBy();
            int count = saveFamilyEntities(employee, familyInfo, createdBy, updatedBy);
            saveFamilyGroupPhoto(employee, familyInfo, createdBy);

            logger.info("✅ Saved {} family member records for emp_id: {} (tempPayrollId: {})",
                    count, employee.getEmp_id(), tempPayrollId);

            // Return the saved DTO object
            return familyInfo;

        } catch (Exception e) {
            logger.error("❌ ERROR: Family Info save failed. Error: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * API 4: Save Previous Employer Info (Tab 4)
     * * @param tempPayrollId Temp Payroll ID
     * 
     * @param previousEmployerInfo Previous Employer Info DTO
     * @return Saved PreviousEmployerInfoDTO object
     */
    public PreviousEmployerInfoDTO savePreviousEmployerInfo(String tempPayrollId,
            PreviousEmployerInfoDTO previousEmployerInfo) {
        logger.info("Saving Previous Employer Info for tempPayrollId: {}", tempPayrollId);

        try {
            // Step 1: Validate DTO data BEFORE any database operations
            validatePreviousEmployerInfo(previousEmployerInfo);
        } catch (Exception e) {
            logger.error("❌ ERROR: Previous Employer Info validation failed. NO data saved. Error: {}", e.getMessage(),
                    e);
            throw e;
        }

        try {
            // Step 2: Find employee (read-only operation)
            Employee employee = findEmployeeByTempPayrollId(tempPayrollId);

            // Step 3: Save to database ONLY after all validations pass
            Integer createdBy = previousEmployerInfo.getCreatedBy();
            Integer updatedBy = previousEmployerInfo.getUpdatedBy();
            int count = saveExperienceEntities(employee, previousEmployerInfo, createdBy, updatedBy);

            logger.info("✅ Saved {} experience records for emp_id: {} (tempPayrollId: {})",
                    count, employee.getEmp_id(), tempPayrollId);

            // Return the saved DTO object
            return previousEmployerInfo;

        } catch (Exception e) {
            logger.error("❌ ERROR: Previous Employer Info save failed. Error: {}", e.getMessage(), e);
            throw e;
        }
    }

    // ============================================================================
    // HELPER METHODS - Employee Operations
    // ============================================================================

    /**
     * Helper: Save EmpDetails entity (handles update/create logic)
     */
    private void saveEmpDetailsEntity(EmpDetails empDetails, Employee employee, Integer updatedBy) {
        Optional<EmpDetails> existingDetails = empDetailsRepository.findByEmployeeId(employee.getEmp_id());
        if (existingDetails.isPresent()) {

            updateEmpDetailsFields(existingDetails.get(), empDetails);
            // Set updated_by and updated_date for UPDATE mode
            if (updatedBy != null) {
                existingDetails.get().setUpdated_by(updatedBy);
                existingDetails.get().setUpdated_date(LocalDateTime.now());
            }
            empDetailsRepository.save(existingDetails.get());
        } else {
            // Check by email
            if (empDetails.getPersonal_email() != null && !empDetails.getPersonal_email().trim().isEmpty()) {
                Optional<EmpDetails> existingByEmail = empDetailsRepository
                        .findByPersonal_email(empDetails.getPersonal_email().trim());
                if (existingByEmail.isPresent()) {
                    EmpDetails existing = existingByEmail.get();
                    // CRITICAL FIX: Prevent "stealing" records from existing ACTIVE employees
                    if (existing.getEmployee_id() != null && existing.getEmployee_id().getIs_active() == 1) {
                        throw new ResourceNotFoundException("Personal email '" + empDetails.getPersonal_email() +
                                "' is already associated with an active employee (ID: "
                                + existing.getEmployee_id().getEmp_id() + ").");
                    }

                    updateEmpDetailsFieldsExceptEmail(existing, empDetails);
                    existing.setEmployee_id(employee);
                    // Use updatedBy or fallback to employee's creator
                    existing.setUpdated_by(updatedBy != null ? updatedBy : employee.getCreated_by());
                    existing.setUpdated_date(LocalDateTime.now());
                    empDetailsRepository.save(existing);
                } else {
                    empDetailsRepository.save(empDetails);
                }
            } else {
                empDetailsRepository.save(empDetails);
            }
        }
    }

    /**
     * Helper: Save EmpPfDetails entity (handles update/create logic)
     */
    private void saveEmpPfDetailsEntity(EmpPfDetails empPfDetails, Employee employee, Integer updatedBy) {
        Optional<EmpPfDetails> existingPf = empPfDetailsRepository.findByEmployeeId(employee.getEmp_id());
        if (existingPf.isPresent()) {
            EmpPfDetails existing = existingPf.get();
            if (empPfDetails.getPre_esi_no() != null)
                existing.setPre_esi_no(empPfDetails.getPre_esi_no());
            if (empPfDetails.getUan_no() != null)
                existing.setUan_no(empPfDetails.getUan_no());
            existing.setIs_active(empPfDetails.getIs_active());
            // Set updated_by and updated_date for UPDATE mode
            if (updatedBy != null) {
                existing.setUpdated_by(updatedBy);
                existing.setUpdated_date(LocalDateTime.now());
            }
            empPfDetailsRepository.save(existing);
        } else {
            empPfDetailsRepository.save(empPfDetails);
        }
    }

    /**
     * Helper: Prepare Employee entity WITHOUT saving
     */
    private Employee prepareEmployeeEntity(BasicInfoDTO basicInfo) {
        if (basicInfo == null) {
            throw new ResourceNotFoundException("Basic Info is required");
        }

        Employee employee = new Employee();
        employee.setFirst_name(basicInfo.getFirstName());
        employee.setLast_name(basicInfo.getLastName());
        employee.setDate_of_join(basicInfo.getDateOfJoin());
        employee.setPrimary_mobile_no(basicInfo.getPrimaryMobileNo());
        employee.setSecondary_mobile_no(basicInfo.getSecondaryMobileNo());
        employee.setEmail(null); // Email goes to EmpDetails.personal_email only

        if (basicInfo.getTotalExperience() != null) {
            employee.setTotal_experience(basicInfo.getTotalExperience().doubleValue());
        }

        if (basicInfo.getAge() != null) {
            employee.setAge(basicInfo.getAge());
        }

        if (Boolean.TRUE.equals(basicInfo.getSscNotAvailable())) {
            employee.setSsc_no(null);
        } else if (basicInfo.getSscNo() != null) {
            employee.setSsc_no(basicInfo.getSscNo());
        }

        employee.setIs_active(1);

        if (basicInfo.getTempPayrollId() != null && !basicInfo.getTempPayrollId().trim().isEmpty()) {
            employee.setTempPayrollId(basicInfo.getTempPayrollId());
        }

        // Set created_by only if provided from frontend, otherwise leave as null
        // (entity default will handle)
        if (basicInfo.getCreatedBy() != null && basicInfo.getCreatedBy() > 0) {
            employee.setCreated_by(basicInfo.getCreatedBy());
        }
        // Set created_date - required field (NOT NULL constraint)
        employee.setCreated_date(LocalDateTime.now());
        if (basicInfo.getCampusId() != null) {
            employee.setCampus_id(campusRepository.findByCampusIdAndIsActive(basicInfo.getCampusId(), 1)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Active Campus not found with ID: " + basicInfo.getCampusId())));
        }

        // Update building_id - optional field
        if (basicInfo.getBuildingId() != null && basicInfo.getBuildingId() > 0) {
            Building building = buildingRepository.findById(basicInfo.getBuildingId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Building not found with ID: " + basicInfo.getBuildingId()));
            // Validate building is active
            if (building.getIsActive() != 1) {
                throw new ResourceNotFoundException(
                        "Building with ID: " + basicInfo.getBuildingId() + " is not active");
            }
            employee.setBuilding_id(building);
        } else if (basicInfo.getBuildingId() != null && basicInfo.getBuildingId() == 0) {
            // Explicitly set to null if 0 is provided
            employee.setBuilding_id(null);
        }
        // If buildingId is null, keep existing value

        if (basicInfo.getGenderId() != null) {
            employee.setGender(genderRepository.findByIdAndIsActive(basicInfo.getGenderId(), 1)
                    .orElseThrow(() -> new ResourceNotFoundException("Active Gender not found")));
        }

        // Note: designationId and departmentId are now handled in CategoryInfoDTO only
        // Removed from BasicInfoDTO to avoid conflicts - these should be set via
        // saveCategoryInfo API

        if (basicInfo.getCategoryId() != null) {
            employee.setCategory(categoryRepository.findByIdAndIsActive(basicInfo.getCategoryId(), 1)
                    .orElseThrow(() -> new ResourceNotFoundException("Active Category not found")));
        }

        if (basicInfo.getEmpTypeId() != null) {
            employee.setEmployee_type_id(employeeTypeRepository.findByIdAndIsActive(basicInfo.getEmpTypeId(), 1)
                    .orElseThrow(() -> new ResourceNotFoundException("Active EmployeeType not found")));
        }

        if (basicInfo.getQualificationId() != null) {
            employee.setQualification_id(qualificationRepository.findById(basicInfo.getQualificationId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Qualification not found with ID: " + basicInfo.getQualificationId())));
        }

        if (basicInfo.getEmpWorkModeId() != null) {
            employee.setWorkingMode_id(workingModeRepository.findByIdAndIsActive(basicInfo.getEmpWorkModeId(), 1)
                    .orElseThrow(() -> new ResourceNotFoundException("Active WorkingMode not found")));
        }

        if (basicInfo.getJoinTypeId() != null) {
            employee.setJoin_type_id(joiningAsRepository.findByIdAndIsActive(basicInfo.getJoinTypeId(), 1)
                    .orElseThrow(() -> new ResourceNotFoundException("Active JoiningAs not found")));

            if (basicInfo.getJoinTypeId() == 3) {
                if (basicInfo.getReplacedByEmpId() == null || basicInfo.getReplacedByEmpId() <= 0) {
                    throw new ResourceNotFoundException(
                            "replacedByEmpId is required when joinTypeId is 3 (Replacement). Please provide a valid replacement employee ID.");
                }
            }
        } else {
            employee.setJoin_type_id(null);
        }

        if (isConsultancyHiringType(basicInfo.getEmpTypeHiringId())) {
            if (basicInfo.getContractStartDate() != null) {
                employee.setContract_start_date(basicInfo.getContractStartDate());
            } else {
                employee.setContract_start_date(basicInfo.getDateOfJoin());
            }

            if (basicInfo.getContractEndDate() != null) {
                employee.setContract_end_date(basicInfo.getContractEndDate());
            } else {
                java.sql.Date startDate = basicInfo.getContractStartDate() != null
                        ? basicInfo.getContractStartDate()
                        : basicInfo.getDateOfJoin();
                if (startDate != null) {
                    long oneYearInMillis = 365L * 24 * 60 * 60 * 1000;
                    java.util.Date endDateUtil = new java.util.Date(startDate.getTime() + oneYearInMillis);
                    employee.setContract_end_date(new java.sql.Date(endDateUtil.getTime()));
                }
            }
        }

        if (basicInfo.getModeOfHiringId() != null) {
            employee.setModeOfHiring_id(modeOfHiringRepository.findByIdAndIsActive(basicInfo.getModeOfHiringId(), 1)
                    .orElseThrow(() -> new ResourceNotFoundException("Active ModeOfHiring not found")));
        }

        if (basicInfo.getEmpTypeHiringId() != null) {
            employee.setEmployee_type_hiring_id(employeeTypeHiringRepository.findById(basicInfo.getEmpTypeHiringId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "EmployeeTypeHiring not found with ID: " + basicInfo.getEmpTypeHiringId())));
        }

        // Handle reference employees
        if (basicInfo.getReferenceEmpId() != null && basicInfo.getReferenceEmpId() > 0) {
            employee.setEmployee_reference(employeeRepository.findByIdAndIs_active(basicInfo.getReferenceEmpId(), 1)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Active Reference Employee not found with ID: " + basicInfo.getReferenceEmpId())));
        } else {
            employee.setEmployee_reference(null);
        }

        if (basicInfo.getHiredByEmpId() != null && basicInfo.getHiredByEmpId() > 0) {
            employee.setEmployee_hired(employeeRepository.findByIdAndIs_active(basicInfo.getHiredByEmpId(), 1)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Active Hired By Employee not found with ID: " + basicInfo.getHiredByEmpId())));
        } else {
            employee.setEmployee_hired(null);
        }

        if (basicInfo.getManagerId() != null && basicInfo.getManagerId() > 0) {
            employee.setEmployee_manager_id(employeeRepository.findByIdAndIs_active(basicInfo.getManagerId(), 1)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Active Manager not found with ID: " + basicInfo.getManagerId())));
        } else {
            employee.setEmployee_manager_id(null);
        }

        if (basicInfo.getReportingManagerId() != null && basicInfo.getReportingManagerId() > 0) {
            employee.setEmployee_reporting_id(employeeRepository
                    .findByIdAndIs_active(basicInfo.getReportingManagerId(), 1)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Active Reporting Manager not found with ID: " + basicInfo.getReportingManagerId())));
        } else {
            employee.setEmployee_reporting_id(null);
        }

        if (basicInfo.getJoinTypeId() != null && basicInfo.getJoinTypeId() == 3) {
            if (basicInfo.getReplacedByEmpId() == null || basicInfo.getReplacedByEmpId() <= 0) {
                throw new ResourceNotFoundException(
                        "replacedByEmpId is required when joinTypeId is 3 (Replacement). Please provide a valid replacement employee ID.");
            }
            employee.setEmployee_replaceby_id(employeeRepository.findById(basicInfo.getReplacedByEmpId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Replacement Employee not found with ID: " + basicInfo.getReplacedByEmpId())));
        } else if (basicInfo.getReplacedByEmpId() != null && basicInfo.getReplacedByEmpId() > 0) {
            employee.setEmployee_replaceby_id(employeeRepository.findById(basicInfo.getReplacedByEmpId())
                    .orElse(null));
        } else {
            employee.setEmployee_replaceby_id(null);
        }

        // Handle preChaitanyaId: if entered, must be an inactive employee (is_active =
        // 0), if not entered, set to null
        if (basicInfo.getPreChaitanyaId() != null && !basicInfo.getPreChaitanyaId().trim().isEmpty()
                && !"0".equals(basicInfo.getPreChaitanyaId().trim())) {
            Employee preChaitanyaEmp = employeeRepository
                    .findByPayRollIdAndIs_active(basicInfo.getPreChaitanyaId().trim(), 0)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Previous Chaitanya Employee not found with ID: " + basicInfo.getPreChaitanyaId()
                                    + ". Only inactive employees (is_active = 0) can be used as previous Chaitanya employee."));
            employee.setPre_chaitanya_id(preChaitanyaEmp.getPayRollId());
        } else {
            employee.setPre_chaitanya_id(null);
        }

        // Set emp_status_id from EmployeeStatus - always use "Current"
        EmployeeStatus employeeStatus = employeeStatusRepository.findByStatusNameAndIsActive("Current", 1)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Active EmployeeStatus with name 'Current' not found"));
        employee.setEmp_status_id(employeeStatus);

        return employee;
    }

    /**
     * Helper: Update existing employee entity with new data from BasicInfoDTO
     */
    private void updateEmployeeEntity(Employee employee, BasicInfoDTO basicInfo) {
        if (basicInfo == null) {
            return;
        }

        logger.info("🔄 Updating employee entity (emp_id: {}) with new data", employee.getEmp_id());

        if (basicInfo.getFirstName() != null && !basicInfo.getFirstName().trim().isEmpty()) {
            employee.setFirst_name(basicInfo.getFirstName());
        }

        if (basicInfo.getLastName() != null && !basicInfo.getLastName().trim().isEmpty()) {
            employee.setLast_name(basicInfo.getLastName());
        }

        if (basicInfo.getDateOfJoin() != null) {
            employee.setDate_of_join(basicInfo.getDateOfJoin());
        }

        if (basicInfo.getPrimaryMobileNo() != null && basicInfo.getPrimaryMobileNo() > 0) {
            employee.setPrimary_mobile_no(basicInfo.getPrimaryMobileNo());
        }

        if (basicInfo.getSecondaryMobileNo() != null && basicInfo.getSecondaryMobileNo() > 0) {
            employee.setSecondary_mobile_no(basicInfo.getSecondaryMobileNo());
        }

        employee.setEmail(null);

        if (basicInfo.getTotalExperience() != null) {
            employee.setTotal_experience(basicInfo.getTotalExperience().doubleValue());
        }

        if (basicInfo.getAge() != null) {
            employee.setAge(basicInfo.getAge());
        }

        if (Boolean.TRUE.equals(basicInfo.getSscNotAvailable())) {
            employee.setSsc_no(null);
        } else if (basicInfo.getSscNo() != null) {
            employee.setSsc_no(basicInfo.getSscNo());
        }

        if (basicInfo.getTempPayrollId() != null && !basicInfo.getTempPayrollId().trim().isEmpty()) {
            employee.setTempPayrollId(basicInfo.getTempPayrollId().trim());
        }

        if (basicInfo.getCreatedBy() != null && basicInfo.getCreatedBy() > 0) {
            employee.setCreated_by(basicInfo.getCreatedBy());
        }

        if (basicInfo.getCampusId() != null) {
            employee.setCampus_id(campusRepository.findByCampusIdAndIsActive(basicInfo.getCampusId(), 1)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Active Campus not found with ID: " + basicInfo.getCampusId())));
        }

        // Update building_id - optional field
        if (basicInfo.getBuildingId() != null && basicInfo.getBuildingId() > 0) {
            Building building = buildingRepository.findById(basicInfo.getBuildingId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Building not found with ID: " + basicInfo.getBuildingId()));
            // Validate building is active
            if (building.getIsActive() != 1) {
                throw new ResourceNotFoundException(
                        "Building with ID: " + basicInfo.getBuildingId() + " is not active");
            }
            employee.setBuilding_id(building);
        } else if (basicInfo.getBuildingId() != null && basicInfo.getBuildingId() == 0) {
            // Explicitly set to null if 0 is provided
            employee.setBuilding_id(null);
        }
        // If buildingId is null, keep existing value

        if (basicInfo.getGenderId() != null) {
            employee.setGender(genderRepository.findByIdAndIsActive(basicInfo.getGenderId(), 1)
                    .orElseThrow(() -> new ResourceNotFoundException("Active Gender not found")));
        }

        // Note: designationId and departmentId are now handled in CategoryInfoDTO only
        // Removed from BasicInfoDTO to avoid conflicts - these should be set via
        // saveCategoryInfo API

        if (basicInfo.getCategoryId() != null) {
            employee.setCategory(categoryRepository.findByIdAndIsActive(basicInfo.getCategoryId(), 1)
                    .orElseThrow(() -> new ResourceNotFoundException("Active Category not found")));
        }

        if (basicInfo.getEmpTypeId() != null) {
            employee.setEmployee_type_id(employeeTypeRepository.findByIdAndIsActive(basicInfo.getEmpTypeId(), 1)
                    .orElseThrow(() -> new ResourceNotFoundException("Active EmployeeType not found")));
        }

        if (basicInfo.getQualificationId() != null) {
            employee.setQualification_id(qualificationRepository.findById(basicInfo.getQualificationId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Qualification not found with ID: " + basicInfo.getQualificationId())));
        }

        if (basicInfo.getEmpWorkModeId() != null) {
            employee.setWorkingMode_id(workingModeRepository.findByIdAndIsActive(basicInfo.getEmpWorkModeId(), 1)
                    .orElseThrow(() -> new ResourceNotFoundException("Active WorkingMode not found")));
        }

        if (basicInfo.getJoinTypeId() != null) {
            employee.setJoin_type_id(joiningAsRepository.findByIdAndIsActive(basicInfo.getJoinTypeId(), 1)
                    .orElseThrow(() -> new ResourceNotFoundException("Active JoiningAs not found")));

            if (basicInfo.getJoinTypeId() == 3) {
                if (basicInfo.getReplacedByEmpId() == null || basicInfo.getReplacedByEmpId() <= 0) {
                    throw new ResourceNotFoundException(
                            "replacedByEmpId is required when joinTypeId is 3 (Replacement). Please provide a valid replacement employee ID.");
                }
                employee.setEmployee_replaceby_id(employeeRepository
                        .findById(basicInfo.getReplacedByEmpId())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Replacement Employee not found with ID: " + basicInfo.getReplacedByEmpId())));
            } else if (basicInfo.getReplacedByEmpId() != null && basicInfo.getReplacedByEmpId() > 0) {
                employee.setEmployee_replaceby_id(
                        employeeRepository.findById(basicInfo.getReplacedByEmpId())
                                .orElse(null));
            } else {
                employee.setEmployee_replaceby_id(null);
            }
        } else {
            employee.setJoin_type_id(null);
            employee.setEmployee_replaceby_id(null);
        }

        if (isConsultancyHiringType(basicInfo.getEmpTypeHiringId())) {
            if (basicInfo.getContractStartDate() != null) {
                employee.setContract_start_date(basicInfo.getContractStartDate());
            } else if (basicInfo.getDateOfJoin() != null) {
                employee.setContract_start_date(basicInfo.getDateOfJoin());
            }

            if (basicInfo.getContractEndDate() != null) {
                employee.setContract_end_date(basicInfo.getContractEndDate());
            } else {
                java.sql.Date startDate = basicInfo.getContractStartDate() != null
                        ? basicInfo.getContractStartDate()
                        : basicInfo.getDateOfJoin();
                if (startDate != null) {
                    long oneYearInMillis = 365L * 24 * 60 * 60 * 1000;
                    java.util.Date endDateUtil = new java.util.Date(startDate.getTime() + oneYearInMillis);
                    employee.setContract_end_date(new java.sql.Date(endDateUtil.getTime()));
                }
            }
        }

        if (basicInfo.getModeOfHiringId() != null) {
            employee.setModeOfHiring_id(modeOfHiringRepository.findByIdAndIsActive(basicInfo.getModeOfHiringId(), 1)
                    .orElseThrow(() -> new ResourceNotFoundException("Active ModeOfHiring not found")));
        }

        if (basicInfo.getEmpTypeHiringId() != null) {
            employee.setEmployee_type_hiring_id(employeeTypeHiringRepository.findById(basicInfo.getEmpTypeHiringId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "EmployeeTypeHiring not found with ID: " + basicInfo.getEmpTypeHiringId())));
        }

        if (basicInfo.getReferenceEmpId() != null && basicInfo.getReferenceEmpId() > 0) {
            employee.setEmployee_reference(employeeRepository.findByIdAndIs_active(basicInfo.getReferenceEmpId(), 1)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Active Reference Employee not found with ID: " + basicInfo.getReferenceEmpId())));
        } else {
            employee.setEmployee_reference(null);
        }

        if (basicInfo.getHiredByEmpId() != null && basicInfo.getHiredByEmpId() > 0) {
            employee.setEmployee_hired(employeeRepository.findByIdAndIs_active(basicInfo.getHiredByEmpId(), 1)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Active Hired By Employee not found with ID: " + basicInfo.getHiredByEmpId())));
        } else {
            employee.setEmployee_hired(null);
        }

        if (basicInfo.getManagerId() != null && basicInfo.getManagerId() > 0) {
            employee.setEmployee_manager_id(employeeRepository.findByIdAndIs_active(basicInfo.getManagerId(), 1)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Active Manager not found with ID: " + basicInfo.getManagerId())));
        } else {
            employee.setEmployee_manager_id(null);
        }

        if (basicInfo.getReportingManagerId() != null && basicInfo.getReportingManagerId() > 0) {
            employee.setEmployee_reporting_id(employeeRepository
                    .findByIdAndIs_active(basicInfo.getReportingManagerId(), 1)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Active Reporting Manager not found with ID: " + basicInfo.getReportingManagerId())));
        } else {
            employee.setEmployee_reporting_id(null);
        }

        // Handle preChaitanyaId: if entered, must be an inactive employee (is_active =
        // 0), if not entered, set to null
        if (basicInfo.getPreChaitanyaId() != null && !basicInfo.getPreChaitanyaId().trim().isEmpty()
                && !"0".equals(basicInfo.getPreChaitanyaId().trim())) {
            Employee preChaitanyaEmp = employeeRepository
                    .findByPayRollIdAndIs_active(basicInfo.getPreChaitanyaId().trim(), 0)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Previous Chaitanya Employee not found with ID: " + basicInfo.getPreChaitanyaId()
                                    + ". Only inactive employees (is_active = 0) can be used as previous Chaitanya employee."));
            employee.setPre_chaitanya_id(preChaitanyaEmp.getPayRollId());
        } else {
            employee.setPre_chaitanya_id(null);
        }

        // Set emp_status_id from EmployeeStatus - always use "Current"
        EmployeeStatus employeeStatus = employeeStatusRepository.findByStatusNameAndIsActive("Current", 1)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Active EmployeeStatus with name 'Current' not found"));
        employee.setEmp_status_id(employeeStatus);

        logger.info("✅ Completed updating employee entity (emp_id: {})", employee.getEmp_id());
    }

    /**
     * Helper: Set employee status to "Incompleted"
     */
    private void setIncompletedStatus(Employee employee) {
        EmployeeCheckListStatus incompletedStatus = employeeCheckListStatusRepository
                .findByCheck_app_status_name("Incompleted")
                .orElseThrow(() -> new ResourceNotFoundException(
                        "EmployeeCheckListStatus with name 'Incompleted' not found"));
        employee.setEmp_check_list_status_id(incompletedStatus);
    }

    /**
     * Helper: Find Employee by tempPayrollId
     * Validates that the employee is active (is_active = 1) before allowing updates
     */
    private Employee findEmployeeByTempPayrollId(String tempPayrollId) {
        Employee employee = employeeRepository.findByTempPayrollId(tempPayrollId.trim())
                .orElseThrow(
                        () -> new ResourceNotFoundException("Employee not found with tempPayrollId: " + tempPayrollId));

        // Validate that employee is active before allowing updates
        if (employee.getIs_active() != 1) {
            throw new ResourceNotFoundException(
                    "Cannot update employee with tempPayrollId: " + tempPayrollId +
                            ". Employee is inactive (is_active = 0). Only active employees can be updated.");
        }

        logger.info("Found active employee with emp_id: {} for tempPayrollId: {}", employee.getEmp_id(), tempPayrollId);
        return employee;
    }

    // ============================================================================
    // HELPER METHODS - EmpDetails Operations
    // ============================================================================

    /**
     * Helper: Prepare EmpDetails entity WITHOUT saving
     */
    private EmpDetails prepareEmpDetailsEntity(BasicInfoDTO basicInfo, Employee employee) {
        if (basicInfo == null) {
            throw new ResourceNotFoundException("Basic Info is required");
        }

        EmpDetails empDetails = new EmpDetails();
        empDetails.setEmployee_id(employee);
        empDetails.setAdhaar_name(basicInfo.getAdhaarName());
        empDetails.setDate_of_birth(basicInfo.getDateOfBirth());
        empDetails.setPersonal_email(basicInfo.getEmail());

        if (basicInfo.getEmergencyPhNo() == null || basicInfo.getEmergencyPhNo().trim().isEmpty()) {
            throw new ResourceNotFoundException(
                    "Emergency contact phone number (emergencyPhNo) is required (NOT NULL column)");
        }
        empDetails.setEmergency_ph_no(basicInfo.getEmergencyPhNo().trim());

        if (basicInfo.getEmergencyRelationId() != null && basicInfo.getEmergencyRelationId() > 0) {
            empDetails.setRelation_id(relationRepository.findById(basicInfo.getEmergencyRelationId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Emergency Relation not found with ID: " + basicInfo.getEmergencyRelationId())));
        } else {
            empDetails.setRelation_id(null);
        }

        empDetails.setAdhaar_no(basicInfo.getAdhaarNo());
        empDetails.setPancard_no(basicInfo.getPancardNum());
        empDetails.setAdhaar_enrolment_no(basicInfo.getAadharEnrolmentNum());
        empDetails.setPassout_year(0);
        empDetails.setIs_active(1);
        empDetails.setStatus("ACTIVE");

        if (basicInfo.getBloodGroupId() == null) {
            throw new ResourceNotFoundException("BloodGroup ID is required (NOT NULL column)");
        }
        empDetails.setBloodGroup_id(bloodGroupRepository.findByIdAndIsActive(basicInfo.getBloodGroupId(), 1)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Active BloodGroup not found with ID: " + basicInfo.getBloodGroupId())));

        if (basicInfo.getCasteId() == null) {
            throw new ResourceNotFoundException("Caste ID is required (NOT NULL column)");
        }
        empDetails.setCaste_id(casteRepository.findById(basicInfo.getCasteId())
                .orElseThrow(
                        () -> new ResourceNotFoundException("Caste not found with ID: " + basicInfo.getCasteId())));

        if (basicInfo.getReligionId() == null) {
            throw new ResourceNotFoundException("Religion ID is required (NOT NULL column)");
        }
        empDetails.setReligion_id(relegionRepository.findById(basicInfo.getReligionId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Religion not found with ID: " + basicInfo.getReligionId())));

        if (basicInfo.getMaritalStatusId() == null) {
            throw new ResourceNotFoundException("MaritalStatus ID is required (NOT NULL column)");
        }
        empDetails.setMarital_status_id(maritalStatusRepository.findByIdAndIsActive(basicInfo.getMaritalStatusId(), 1)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Active MaritalStatus not found with ID: " + basicInfo.getMaritalStatusId())));

        // Required fields with @NotNull constraint - must always be set
        if (basicInfo.getFatherName() == null || basicInfo.getFatherName().trim().isEmpty()) {
            throw new ResourceNotFoundException("Father Name is required (NOT NULL column)");
        }
        empDetails.setFatherName(basicInfo.getFatherName().trim());

        if (basicInfo.getUanNo() == null) {
            throw new ResourceNotFoundException("UAN Number is required (NOT NULL column)");
        }
        empDetails.setUanNo(basicInfo.getUanNo());

        // Set created_by and created_date (required NOT NULL columns)
        // created_by must be provided by user (from DTO) - no defaults or fallbacks
        Integer createdBy = basicInfo.getCreatedBy();
        if (createdBy == null || createdBy <= 0) {
            throw new ResourceNotFoundException(
                    "createdBy is required (NOT NULL column). Please provide createdBy in BasicInfoDTO.");
        }
        empDetails.setCreated_by(createdBy);
        empDetails.setCreated_date(LocalDateTime.now());

        return empDetails;
    }

    /**
     * Helper: Update EmpDetails fields from source to target
     */
    private void updateEmpDetailsFields(EmpDetails target, EmpDetails source) {
        if (source.getAdhaar_name() != null)
            target.setAdhaar_name(source.getAdhaar_name());
        if (source.getDate_of_birth() != null)
            target.setDate_of_birth(source.getDate_of_birth());
        if (source.getPersonal_email() != null)
            target.setPersonal_email(source.getPersonal_email());
        if (source.getEmergency_ph_no() != null)
            target.setEmergency_ph_no(source.getEmergency_ph_no());
        if (source.getRelation_id() != null)
            target.setRelation_id(source.getRelation_id());
        if (source.getAdhaar_no() != null)
            target.setAdhaar_no(source.getAdhaar_no());
        if (source.getPancard_no() != null)
            target.setPancard_no(source.getPancard_no());
        if (source.getAdhaar_enrolment_no() != null)
            target.setAdhaar_enrolment_no(source.getAdhaar_enrolment_no());
        if (source.getBloodGroup_id() != null)
            target.setBloodGroup_id(source.getBloodGroup_id());
        if (source.getCaste_id() != null)
            target.setCaste_id(source.getCaste_id());
        if (source.getReligion_id() != null)
            target.setReligion_id(source.getReligion_id());
        if (source.getMarital_status_id() != null)
            target.setMarital_status_id(source.getMarital_status_id());
        if (source.getFatherName() != null)
            target.setFatherName(source.getFatherName());
        if (source.getUanNo() != null)
            target.setUanNo(source.getUanNo());
        target.setIs_active(source.getIs_active());
        // Status field removed from Employee entity - removed setStatus call
    }

    /**
     * Helper: Update EmpDetails fields except personal_email
     */
    private void updateEmpDetailsFieldsExceptEmail(EmpDetails target, EmpDetails source) {
        if (source.getAdhaar_name() != null)
            target.setAdhaar_name(source.getAdhaar_name());
        if (source.getDate_of_birth() != null)
            target.setDate_of_birth(source.getDate_of_birth());
        // DO NOT update personal_email
        if (source.getEmergency_ph_no() != null)
            target.setEmergency_ph_no(source.getEmergency_ph_no());
        if (source.getRelation_id() != null)
            target.setRelation_id(source.getRelation_id());
        if (source.getAdhaar_no() != null)
            target.setAdhaar_no(source.getAdhaar_no());
        if (source.getPancard_no() != null)
            target.setPancard_no(source.getPancard_no());
        if (source.getAdhaar_enrolment_no() != null)
            target.setAdhaar_enrolment_no(source.getAdhaar_enrolment_no());
        if (source.getBloodGroup_id() != null)
            target.setBloodGroup_id(source.getBloodGroup_id());
        if (source.getCaste_id() != null)
            target.setCaste_id(source.getCaste_id());
        if (source.getReligion_id() != null)
            target.setReligion_id(source.getReligion_id());
        if (source.getMarital_status_id() != null)
            target.setMarital_status_id(source.getMarital_status_id());
        if (source.getFatherName() != null)
            target.setFatherName(source.getFatherName());
        if (source.getUanNo() != null)
            target.setUanNo(source.getUanNo());
        target.setIs_active(source.getIs_active());
        // Status field removed from Employee entity - removed setStatus call
    }

    // ============================================================================
    // HELPER METHODS - EmpPfDetails Operations
    // ============================================================================

    /**
     * Helper: Prepare EmpPfDetails entity WITHOUT saving
     */
    private EmpPfDetails prepareEmpPfDetailsEntity(BasicInfoDTO basicInfo, Employee employee) {
        if (basicInfo == null)
            return null;

        if (basicInfo.getPreUanNum() == null && basicInfo.getPreEsiNum() == null) {
            return null;
        }

        EmpPfDetails empPfDetails = new EmpPfDetails();
        empPfDetails.setEmployee_id(employee);
        empPfDetails.setPre_esi_no(basicInfo.getPreEsiNum());
        empPfDetails.setUan_no(basicInfo.getPreUanNum());
        empPfDetails.setIs_active(1);

        // Set created_by and created_date (required NOT NULL columns)
        // created_by must be provided by user (from DTO) - no defaults or fallbacks
        Integer createdBy = basicInfo.getCreatedBy();
        if (createdBy == null || createdBy <= 0) {
            throw new ResourceNotFoundException(
                    "createdBy is required (NOT NULL column). Please provide createdBy in BasicInfoDTO.");
        }
        empPfDetails.setCreated_by(createdBy);
        empPfDetails.setCreated_date(LocalDateTime.now());

        return empPfDetails;
    }

    // ============================================================================
    // HELPER METHODS - Address Operations
    // ============================================================================

    /**
     * Helper: Save Address Entities
     */
    private int saveAddressEntities(Employee employee, AddressInfoDTO addressInfo, Integer createdBy,
            Integer updatedBy) {
        List<EmpaddressInfo> addressEntities = prepareAddressEntities(addressInfo, employee, createdBy);
        updateOrCreateAddressEntities(addressEntities, employee, addressInfo, updatedBy);
        return addressEntities.size();
    }

    /**
     * Helper: Prepare Address entities WITHOUT saving
     * Logic:
     * - If addresses are same: Create 1 record with is_per_and_curr = 1
     * - If addresses are different: Create 2 records with is_per_and_curr = 0 for
     * both
     */
    private List<EmpaddressInfo> prepareAddressEntities(AddressInfoDTO addressInfo, Employee employee,
            Integer createdBy) {
        List<EmpaddressInfo> addressList = new ArrayList<>();

        if (addressInfo == null)
            return addressList;

        boolean addressesAreSame = Boolean.TRUE.equals(addressInfo.getPermanentAddressSameAsCurrent());

        if (addressInfo.getCurrentAddress() != null) {
            if (addressesAreSame) {
                // Addresses are same: Create 1 record with is_per_and_curr = 1
                EmpaddressInfo currentAddr = createAddressEntity(addressInfo.getCurrentAddress(), employee, "CURR",
                        createdBy, 1);
                addressList.add(currentAddr);
            } else {
                // Addresses are different: Create current address with is_per_and_curr = 0
                EmpaddressInfo currentAddr = createAddressEntity(addressInfo.getCurrentAddress(), employee, "CURR",
                        createdBy, 0);
                addressList.add(currentAddr);

                // Create permanent address with is_per_and_curr = 0
                if (addressInfo.getPermanentAddress() != null) {
                    EmpaddressInfo permanentAddr = createAddressEntity(addressInfo.getPermanentAddress(), employee,
                            "PERM", createdBy, 0);
                    addressList.add(permanentAddr);
                }
            }
        }

        return addressList;
    }

    /**
     * Helper: Create Address entity
     * 
     * @param isPerAndCurr 1 if permanent and current addresses are same, 0 if
     *                     different
     */
    private EmpaddressInfo createAddressEntity(AddressInfoDTO.AddressDTO addressDTO, Employee employee,
            String addressType, Integer createdBy, Integer isPerAndCurr) {
        EmpaddressInfo address = new EmpaddressInfo();
        address.setAddrs_type(addressType);
        address.setHouse_no(addressDTO.getAddressLine1());
        address.setLandmark(addressDTO.getAddressLine2() + " "
                + (addressDTO.getAddressLine3() != null ? addressDTO.getAddressLine3() : ""));
        address.setPostal_code(addressDTO.getPin());

        if (addressDTO.getPhoneNumber() != null && !addressDTO.getPhoneNumber().trim().isEmpty()) {
            try {
                address.setEmrg_contact_no(Long.parseLong(addressDTO.getPhoneNumber().trim()));
            } catch (NumberFormatException e) {
                logger.warn("Invalid phone number format for address: {}", addressDTO.getPhoneNumber());
                // Optionally throw exception or ignore
            }
        }

        address.setIs_active(1);
        address.setEmp_id(employee);
        address.setIs_per_and_curr(isPerAndCurr);

        if (addressDTO.getCountryId() != null) {
            address.setCountry_id(countryRepository.findById(addressDTO.getCountryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Country not found")));
        } else {
            throw new ResourceNotFoundException("Country ID is required (NOT NULL column)");
        }

        if (addressDTO.getStateId() != null) {
            address.setState_id(stateRepository.findById(addressDTO.getStateId())
                    .orElseThrow(() -> new ResourceNotFoundException("State not found")));
        } else {
            throw new ResourceNotFoundException("State ID is required (NOT NULL column)");
        }

        if (addressDTO.getCityId() != null) {
            address.setCity_id(cityRepository.findById(addressDTO.getCityId())
                    .orElseThrow(() -> new ResourceNotFoundException("City not found")));
        } else {
            throw new ResourceNotFoundException("City ID is required (NOT NULL column)");
        }

        if (addressDTO.getDistrictId() != null) {
            address.setDistrict_id(districtRepository.findById(addressDTO.getDistrictId())
                    .orElseThrow(() -> new ResourceNotFoundException("District not found")));
        }

        // Set created_by and created_date (required NOT NULL columns)
        // created_by must be provided by user (from DTO) - no defaults or fallbacks
        if (createdBy == null || createdBy <= 0) {
            throw new ResourceNotFoundException(
                    "createdBy is required (NOT NULL column). Please provide createdBy in AddressInfoDTO.");
        }
        address.setCreated_by(createdBy);
        address.setCreated_date(LocalDateTime.now());

        return address;
    }

    /**
     * Helper: Update or create Address entities
     */
    private void updateOrCreateAddressEntities(List<EmpaddressInfo> newAddresses, Employee employee,
            AddressInfoDTO addressInfo, Integer updatedBy) {
        int empId = employee.getEmp_id();

        List<EmpaddressInfo> existingAddresses = empaddressInfoRepository.findAll().stream()
                .filter(addr -> addr.getEmp_id() != null && addr.getEmp_id().getEmp_id() == empId
                        && addr.getIs_active() == 1)
                .collect(Collectors.toList());

        for (EmpaddressInfo newAddr : newAddresses) {
            newAddr.setEmp_id(employee);
            newAddr.setIs_active(1);

            Optional<EmpaddressInfo> existingByType = existingAddresses.stream()
                    .filter(addr -> addr.getAddrs_type() != null &&
                            addr.getAddrs_type().equals(newAddr.getAddrs_type()))
                    .findFirst();

            if (existingByType.isPresent()) {
                EmpaddressInfo existing = existingByType.get();
                boolean changed = updateAddressFields(existing, newAddr);
                // Set updated_by and updated_date ONLY if something changed
                if (changed) {
                    if (updatedBy != null && updatedBy > 0) {
                        existing.setUpdated_by(updatedBy);
                        existing.setUpdated_date(LocalDateTime.now());
                    }
                    empaddressInfoRepository.save(existing);
                }
                existingAddresses.remove(existing);
            } else {
                empaddressInfoRepository.save(newAddr);
            }
        }

        if (addressInfo != null && Boolean.TRUE.equals(addressInfo.getPermanentAddressSameAsCurrent())) {
            for (EmpaddressInfo existingAddr : existingAddresses) {
                if ("PERM".equals(existingAddr.getAddrs_type())) {
                    existingAddr.setIs_active(0);
                    if (updatedBy != null && updatedBy > 0) {
                        existingAddr.setUpdated_by(updatedBy);
                        existingAddr.setUpdated_date(LocalDateTime.now());
                    }
                    empaddressInfoRepository.save(existingAddr);
                }
            }
            existingAddresses.removeIf(addr -> "PERM".equals(addr.getAddrs_type()));
        }

        for (EmpaddressInfo remainingAddr : existingAddresses) {
            remainingAddr.setIs_active(0);
            if (updatedBy != null && updatedBy > 0) {
                remainingAddr.setUpdated_by(updatedBy);
                remainingAddr.setUpdated_date(LocalDateTime.now());
            }
            empaddressInfoRepository.save(remainingAddr);
        }
    }

    /**
     * Helper: Update Address fields
     */
    private boolean updateAddressFields(EmpaddressInfo target, EmpaddressInfo source) {
        boolean changed = false;

        // Hyper-robust string helper: treats null, empty, and whitespace as same,
        // case-insensitive
        java.util.function.BiPredicate<String, String> areStringsEqual = (s1, s2) -> {

            String str1 = (s1 == null || s1.trim().isEmpty()) ? "" : s1.trim();
            String str2 = (s2 == null || s2.trim().isEmpty()) ? "" : s2.trim();
            return str1.equalsIgnoreCase(str2);
        };

        // Hyper-robust numeric helper: treats null and 0 as same
        java.util.function.BiPredicate<Number, Number> areNumbersEqual = (n1, n2) -> {
            long v1 = (n1 == null) ? 0L : n1.longValue();
            long v2 = (n2 == null) ? 0L : n2.longValue();
            return v1 == v2;
        };

        if (!areStringsEqual.test(target.getAddrs_type(), source.getAddrs_type())) {
            target.setAddrs_type(source.getAddrs_type() != null ? source.getAddrs_type().trim() : null);
            changed = true;
        }

        Integer targetCountryId = target.getCountry_id() != null ? target.getCountry_id().getCountryId() : null;
        Integer sourceCountryId = source.getCountry_id() != null ? source.getCountry_id().getCountryId() : null;
        if (!areNumbersEqual.test(targetCountryId, sourceCountryId)) {
            target.setCountry_id(source.getCountry_id());
            changed = true;
        }

        Integer targetStateId = target.getState_id() != null ? target.getState_id().getStateId() : null;
        Integer sourceStateId = source.getState_id() != null ? source.getState_id().getStateId() : null;
        if (!areNumbersEqual.test(targetStateId, sourceStateId)) {
            target.setState_id(source.getState_id());
            changed = true;
        }

        Integer targetCityId = target.getCity_id() != null ? target.getCity_id().getCityId() : null;
        Integer sourceCityId = source.getCity_id() != null ? source.getCity_id().getCityId() : null;
        if (!areNumbersEqual.test(targetCityId, sourceCityId)) {
            target.setCity_id(source.getCity_id());
            changed = true;
        }

        Integer targetDistrictId = target.getDistrict_id() != null ? target.getDistrict_id().getDistrictId() : null;
        Integer sourceDistrictId = source.getDistrict_id() != null ? source.getDistrict_id().getDistrictId() : null;
        if (!areNumbersEqual.test(targetDistrictId, sourceDistrictId)) {
            target.setDistrict_id(source.getDistrict_id());
            changed = true;
        }

        if (!areStringsEqual.test(target.getPostal_code(), source.getPostal_code())) {
            target.setPostal_code(source.getPostal_code() != null ? source.getPostal_code().trim() : null);
            changed = true;
        }
        if (!areStringsEqual.test(target.getHouse_no(), source.getHouse_no())) {
            target.setHouse_no(source.getHouse_no() != null ? source.getHouse_no().trim() : null);
            changed = true;
        }
        if (!areStringsEqual.test(target.getLandmark(), source.getLandmark())) {
            target.setLandmark(source.getLandmark() != null ? source.getLandmark().trim() : null);
            changed = true;
        }
        if (!areNumbersEqual.test(target.getEmrg_contact_no(), source.getEmrg_contact_no())) {
            target.setEmrg_contact_no(source.getEmrg_contact_no());
            changed = true;
        }
        if (target.getIs_active() != source.getIs_active()) {
            target.setIs_active(source.getIs_active());
            changed = true;
        }
        if (!Objects.equals(target.getIs_per_and_curr(), source.getIs_per_and_curr())) {
            target.setIs_per_and_curr(source.getIs_per_and_curr());
            changed = true;
        }
        return changed;
    }

    // ============================================================================
    // HELPER METHODS - Family Operations
    // ============================================================================

    /**
     * Helper: Save Family Entities
     */
    private int saveFamilyEntities(Employee employee, FamilyInfoDTO familyInfo, Integer createdBy, Integer updatedBy) {
        List<EmpFamilyDetails> familyEntities = prepareFamilyEntities(familyInfo, employee, createdBy);
        updateOrCreateFamilyEntities(familyEntities, employee, updatedBy);
        return familyEntities.size();
    }

    /**
     * Helper: Prepare Family entities WITHOUT saving
     */
    private List<EmpFamilyDetails> prepareFamilyEntities(FamilyInfoDTO familyInfo, Employee employee,
            Integer createdBy) {
        List<EmpFamilyDetails> familyList = new ArrayList<>();

        if (familyInfo == null || familyInfo.getFamilyMembers() == null || familyInfo.getFamilyMembers().isEmpty()) {
            return familyList;
        }

        for (FamilyInfoDTO.FamilyMemberDTO memberDTO : familyInfo.getFamilyMembers()) {
            if (memberDTO != null) {
                // Skip "junk" family members (e.g., from Swagger default values)
                if (memberDTO.getFullName() == null || memberDTO.getFullName().trim().isEmpty()
                        || "string".equalsIgnoreCase(memberDTO.getFullName().trim())) {
                    continue;
                }
                EmpFamilyDetails familyMember = createFamilyMemberEntity(memberDTO, employee, createdBy);
                familyList.add(familyMember);
            }
        }

        return familyList;
    }

    /**
     * Helper: Create Family Member entity
     */
    private EmpFamilyDetails createFamilyMemberEntity(FamilyInfoDTO.FamilyMemberDTO memberDTO, Employee employee,
            Integer createdBy) {
        EmpFamilyDetails familyMember = new EmpFamilyDetails();

        familyMember.setEmp_id(employee);

        // Full Name and Aadhaar (Updated)
        familyMember.setFullName(memberDTO.getFullName());
        familyMember.setAdhaarNo(memberDTO.getAdhaarNo());

        familyMember.setIs_late(memberDTO.getIsLate() != null && memberDTO.getIsLate() ? "Y" : "N");

        // Handle occupation: If occupationId is provided, check if it exists in
        // Occupation table
        // If exists, use occupation_name from table; otherwise use occupation string
        // from frontend
        String occupationToStore = null;
        if (memberDTO.getOccupationId() != null && memberDTO.getOccupationId() > 0) {
            Optional<com.employee.entity.Occupation> occupationOpt = occupationRepository
                    .findById(memberDTO.getOccupationId());
            if (occupationOpt.isPresent() && occupationOpt.get().getIsActive() != null
                    && occupationOpt.get().getIsActive() == 1) {
                // Occupation ID exists and is active, use occupation_name from table
                occupationToStore = occupationOpt.get().getOccupation_name();
                logger.debug("Using occupation_name '{}' from Occupation table for occupationId: {}", occupationToStore,
                        memberDTO.getOccupationId());
            } else {
                // Occupation ID provided but not found or inactive, use occupation string from
                // frontend
                occupationToStore = memberDTO.getOccupation();
                logger.debug("Occupation ID {} not found or inactive, using occupation string from frontend: {}",
                        memberDTO.getOccupationId(), occupationToStore);
            }
        } else {
            // No occupationId provided (Others case), use occupation string from frontend
            occupationToStore = memberDTO.getOccupation();
            logger.debug("No occupationId provided, using occupation string from frontend: {}", occupationToStore);
        }

        if (occupationToStore == null || occupationToStore.trim().isEmpty()) {
            throw new ResourceNotFoundException(
                    "Occupation is required (NOT NULL column). Please provide either occupationId or occupation name.");
        }

        familyMember.setOccupation(occupationToStore);
        familyMember.setNationality(memberDTO.getNationality());
        familyMember.setIs_active(1);
        familyMember.setDate_of_birth(memberDTO.getDateOfBirth());

        if (memberDTO.getIsDependent() != null) {
            familyMember.setIs_dependent(memberDTO.getIsDependent() ? 1 : 0);
        } else {
            familyMember.setIs_dependent(null);
        }

        if (memberDTO.getRelationId() == null) {
            throw new ResourceNotFoundException("Relation ID is required (NOT NULL column)");
        }
        familyMember.setRelation_id(relationRepository.findById(memberDTO.getRelationId())
                .orElseThrow(() -> new ResourceNotFoundException("Relation not found")));

        Integer genderIdToUse;
        if (memberDTO.getRelationId() == 1) {
            genderIdToUse = 1; // Father - Male
        } else if (memberDTO.getRelationId() == 2) {
            genderIdToUse = 2; // Mother - Female
        } else {
            genderIdToUse = memberDTO.getGenderId();
        }

        if (genderIdToUse != null) {
            familyMember.setGender_id(genderRepository.findById(genderIdToUse)
                    .orElseThrow(() -> new ResourceNotFoundException("Gender not found with ID: " + genderIdToUse)));
        } else {
            throw new ResourceNotFoundException("Gender ID is required (NOT NULL column)");
        }

        if (memberDTO.getBloodGroupId() != null && memberDTO.getBloodGroupId() > 0) {
            familyMember.setBlood_group_id(bloodGroupRepository.findByIdAndIsActive(memberDTO.getBloodGroupId(), 1)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Active BloodGroup not found with ID: " + memberDTO.getBloodGroupId())));
        } else {
            familyMember.setBlood_group_id(null);
        }

        Integer isSriChaitanyaEmpValue = 0;
        if (memberDTO.getIsSriChaitanyaEmp() != null) {
            isSriChaitanyaEmpValue = memberDTO.getIsSriChaitanyaEmp() ? 1 : 0;
        }
        familyMember.setIs_sri_chaitanya_emp(isSriChaitanyaEmpValue);

        if (isSriChaitanyaEmpValue == 1) {
            if (memberDTO.getParentEmpId() == null || memberDTO.getParentEmpId().trim().isEmpty()) {
                throw new ResourceNotFoundException(
                        "parentEmpId is required when isSriChaitanyaEmp is true. Please provide a valid parent payroll ID.");
            }
            Employee parentEmployee = employeeRepository.findByPayrollId(memberDTO.getParentEmpId().trim())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Parent Employee not found with Payroll ID: " + memberDTO.getParentEmpId()));
            familyMember.setParent_emp_id(parentEmployee);
        } else {
            if (memberDTO.getParentEmpId() != null && !memberDTO.getParentEmpId().trim().isEmpty()
                    && !"0".equals(memberDTO.getParentEmpId().trim())) {
                Employee parentEmployee = employeeRepository.findByPayrollId(memberDTO.getParentEmpId().trim())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Parent Employee not found with Payroll ID: " + memberDTO.getParentEmpId()));
                familyMember.setParent_emp_id(parentEmployee);
            }
        }

        familyMember.setEmail(memberDTO.getEmail());

        if (memberDTO.getPhoneNumber() != null && !memberDTO.getPhoneNumber().trim().isEmpty()) {
            try {
                familyMember.setContact_no(Long.parseLong(memberDTO.getPhoneNumber()));
            } catch (NumberFormatException e) {
                throw new ResourceNotFoundException(
                        "Invalid phone number format for family member: " + memberDTO.getPhoneNumber());
            }
        }

        // Set created_by and created_date (required NOT NULL columns)
        // created_by must be provided by user (from DTO) - no defaults or fallbacks
        if (createdBy == null || createdBy <= 0) {
            throw new ResourceNotFoundException(
                    "createdBy is required (NOT NULL column). Please provide createdBy in FamilyInfoDTO.");
        }
        familyMember.setCreated_by(createdBy);
        familyMember.setCreated_date(LocalDateTime.now());

        return familyMember;
    }

    private void updateOrCreateFamilyEntities(List<EmpFamilyDetails> newFamily, Employee employee, Integer updatedBy) {
        int empId = employee.getEmp_id();

        // 1. Get existing active family members
        List<EmpFamilyDetails> existingFamily = empFamilyDetailsRepository.findAll().stream()
                .filter(fam -> fam.getEmp_id() != null && fam.getEmp_id().getEmp_id() == empId
                        && fam.getIs_active() == 1)
                .collect(Collectors.toList());

        // 2. Track which existing ones are matched to prevent duplicate matching
        java.util.Set<Integer> matchedExistingIds = new java.util.HashSet<>();

        // 3. Process newFamily list (updates or new records)
        for (EmpFamilyDetails newFam : newFamily) {
            newFam.setEmp_id(employee);
            newFam.setIs_active(1);

            // Find match in existing family by relation and name
            EmpFamilyDetails existingMatch = existingFamily.stream()
                    .filter(existing -> !matchedExistingIds.contains(existing.getEmp_family_detl_id()))
                    .filter(existing -> areFamilyMembersSame(existing, newFam))
                    .findFirst()
                    .orElse(null);

            if (existingMatch != null) {
                matchedExistingIds.add(existingMatch.getEmp_family_detl_id());
                boolean changed = updateFamilyFields(existingMatch, newFam);
                // Set updated_by and updated_date ONLY if something changed
                if (changed) {
                    if (updatedBy != null && updatedBy > 0) {
                        existingMatch.setUpdated_by(updatedBy);
                        existingMatch.setUpdated_date(LocalDateTime.now());
                    }
                    empFamilyDetailsRepository.save(existingMatch);
                }
            } else {
                // New record - no match found in existing
                empFamilyDetailsRepository.save(newFam);
            }
        }

        // 4. Deactivate existing records that weren't in the new list
        for (EmpFamilyDetails existing : existingFamily) {
            if (!matchedExistingIds.contains(existing.getEmp_family_detl_id())) {
                existing.setIs_active(0);
                if (updatedBy != null && updatedBy > 0) {
                    existing.setUpdated_by(updatedBy);
                    existing.setUpdated_date(LocalDateTime.now());
                }
                empFamilyDetailsRepository.save(existing);
            }
        }
    }

    /**
     * Helper: Check if two family members are the same (used for matching)
     */
    private boolean areFamilyMembersSame(EmpFamilyDetails f1, EmpFamilyDetails f2) {
        if (f1 == null || f2 == null)
            return false;

        Integer r1 = f1.getRelation_id() != null ? f1.getRelation_id().getStudentRelationId() : null;
        Integer r2 = f2.getRelation_id() != null ? f2.getRelation_id().getStudentRelationId() : null;

        if (!Objects.equals(r1, r2))
            return false;

        // If relation is Father (1) or Mother (2), match by relation alone.
        // This allows updating the parent's name without creating a new record.
        if (r1 != null && (r1 == 1 || r1 == 2)) {
            return true;
        }

        String n1 = f1.getFullName() != null ? f1.getFullName().trim() : "";
        String n2 = f2.getFullName() != null ? f2.getFullName().trim() : "";

        return n1.equalsIgnoreCase(n2);
    }

    /**
     * Helper: Update Family fields
     */
    private boolean updateFamilyFields(EmpFamilyDetails target, EmpFamilyDetails source) {
        boolean changed = false;
        String name = target.getFullName();

        // Hyper-robust string helper: treats null, empty, and whitespace as same,
        // case-insensitive
        java.util.function.BiPredicate<String, String> areStringsEqual = (s1, s2) -> {
            String str1 = (s1 == null || s1.trim().isEmpty()) ? "" : s1.trim();
            String str2 = (s2 == null || s2.trim().isEmpty()) ? "" : s2.trim();
            return str1.equalsIgnoreCase(str2);
        };

        // Hyper-robust numeric helper: treats null and 0 as same
        java.util.function.BiPredicate<Number, Number> areNumbersEqual = (n1, n2) -> {
            long v1 = (n1 == null) ? 0L : n1.longValue();
            long v2 = (n2 == null) ? 0L : n2.longValue();
            return v1 == v2;
        };

        // Hyper-robust date helper: compares YYYY-MM-DD using toLocalDate
        java.util.function.BiPredicate<java.sql.Date, java.sql.Date> areDatesEqual = (d1, d2) -> {
            if (d1 == null && d2 == null)
                return true;
            if (d1 == null || d2 == null)
                return false;
            return d1.toLocalDate().equals(d2.toLocalDate());
        };

        if (!areStringsEqual.test(target.getFullName(), source.getFullName())) {
            logger.info("Field 'fullName' changed for {}: '{}' -> '{}'", name, target.getFullName(),
                    source.getFullName());
            target.setFullName(source.getFullName() != null ? source.getFullName().trim() : null);
            changed = true;
        }
        if (!areNumbersEqual.test(target.getAdhaarNo(), source.getAdhaarNo())) {
            logger.info("Field 'adhaarNo' changed for {}: '{}' -> '{}'", name, target.getAdhaarNo(),
                    source.getAdhaarNo());
            target.setAdhaarNo(source.getAdhaarNo());
            changed = true;
        }
        if (!areDatesEqual.test(target.getDate_of_birth(), source.getDate_of_birth())) {
            logger.info("Field 'date_of_birth' changed for {}: '{}' -> '{}'", name, target.getDate_of_birth(),
                    source.getDate_of_birth());
            target.setDate_of_birth(source.getDate_of_birth());
            changed = true;
        }

        Integer targetGenderId = target.getGender_id() != null ? target.getGender_id().getGender_id() : null;
        Integer sourceGenderId = source.getGender_id() != null ? source.getGender_id().getGender_id() : null;
        if (!areNumbersEqual.test(targetGenderId, sourceGenderId)) {
            logger.info("Field 'genderId' changed for {}: '{}' -> '{}'", name, targetGenderId, sourceGenderId);
            target.setGender_id(source.getGender_id());
            changed = true;
        }

        Integer targetRelationId = target.getRelation_id() != null ? target.getRelation_id().getStudentRelationId()
                : null;
        Integer sourceRelationId = source.getRelation_id() != null ? source.getRelation_id().getStudentRelationId()
                : null;
        if (!areNumbersEqual.test(targetRelationId, sourceRelationId)) {
            logger.info("Field 'relationId' changed for {}: '{}' -> '{}'", name, targetRelationId, sourceRelationId);
            target.setRelation_id(source.getRelation_id());
            changed = true;
        }

        Integer targetBloodGroupId = target.getBlood_group_id() != null ? target.getBlood_group_id().getBloodGroupId()
                : null;
        Integer sourceBloodGroupId = source.getBlood_group_id() != null ? source.getBlood_group_id().getBloodGroupId()
                : null;
        if (!areNumbersEqual.test(targetBloodGroupId, sourceBloodGroupId)) {
            logger.info("Field 'bloodGroupId' changed for {}: '{}' -> '{}'", name, targetBloodGroupId,
                    sourceBloodGroupId);
            target.setBlood_group_id(source.getBlood_group_id());
            changed = true;
        }

        if (!areStringsEqual.test(target.getNationality(), source.getNationality())) {
            logger.info("Field 'nationality' changed for {}: '{}' -> '{}'", name, target.getNationality(),
                    source.getNationality());
            target.setNationality(source.getNationality() != null ? source.getNationality().trim() : null);
            changed = true;
        }
        if (!areStringsEqual.test(target.getOccupation(), source.getOccupation())) {
            logger.info("Field 'occupation' changed for {}: '{}' -> '{}'", name, target.getOccupation(),
                    source.getOccupation());
            target.setOccupation(source.getOccupation() != null ? source.getOccupation().trim() : null);
            changed = true;
        }
        if (!areNumbersEqual.test(target.getIs_dependent(), source.getIs_dependent())) {
            logger.info("Field 'is_dependent' changed for {}: '{}' -> '{}'", name, target.getIs_dependent(),
                    source.getIs_dependent());
            target.setIs_dependent(source.getIs_dependent());
            changed = true;
        }
        if (!areStringsEqual.test(target.getIs_late(), source.getIs_late())) {
            logger.info("Field 'is_late' changed for {}: '{}' -> '{}'", name, target.getIs_late(), source.getIs_late());
            target.setIs_late(source.getIs_late() != null ? source.getIs_late().trim() : null);
            changed = true;
        }
        if (!areNumbersEqual.test(target.getIs_sri_chaitanya_emp(), source.getIs_sri_chaitanya_emp())) {
            logger.info("Field 'is_sri_chaitanya_emp' changed for {}: '{}' -> '{}'", name,
                    target.getIs_sri_chaitanya_emp(), source.getIs_sri_chaitanya_emp());
            target.setIs_sri_chaitanya_emp(source.getIs_sri_chaitanya_emp());
            changed = true;
        }

        String targetParentPayrollId = target.getParent_emp_id() != null ? target.getParent_emp_id().getPayRollId()
                : null;
        String sourceParentPayrollId = source.getParent_emp_id() != null ? source.getParent_emp_id().getPayRollId()
                : null;
        if (!areStringsEqual.test(targetParentPayrollId, sourceParentPayrollId)) {
            logger.info("Field 'parentEmpId' changed for {}: '{}' -> '{}'", name, targetParentPayrollId,
                    sourceParentPayrollId);
            target.setParent_emp_id(source.getParent_emp_id());
            changed = true;
        }

        if (!areStringsEqual.test(target.getEmail(), source.getEmail())) {
            logger.info("Field 'email' changed for {}: '{}' -> '{}'", name, target.getEmail(), source.getEmail());
            target.setEmail(source.getEmail() != null ? source.getEmail().trim() : null);
            changed = true;
        }
        if (!areNumbersEqual.test(target.getContact_no(), source.getContact_no())) {
            logger.info("Field 'contactNo' changed for {}: '{}' -> '{}'", name, target.getContact_no(),
                    source.getContact_no());
            target.setContact_no(source.getContact_no());
            changed = true;
        }
        if (target.getIs_active() != source.getIs_active()) {
            logger.info("Field 'is_active' changed for {}: '{}' -> '{}'", name, target.getIs_active(),
                    source.getIs_active());
            target.setIs_active(source.getIs_active());
            changed = true;
        }

        return changed;
    }

    /**
     * Helper: Save Family Group Photo as document
     */
    private void saveFamilyGroupPhoto(Employee employee, FamilyInfoDTO familyInfo, Integer createdBy) {
        if (familyInfo != null && familyInfo.getFamilyGroupPhotoPath() != null
                && !familyInfo.getFamilyGroupPhotoPath().trim().isEmpty()
                && !"string".equalsIgnoreCase(familyInfo.getFamilyGroupPhotoPath().trim())) {
            EmpDocuments familyPhotoDoc = createFamilyGroupPhotoDocument(familyInfo, employee, createdBy);
            empDocumentsRepository.save(familyPhotoDoc);
            logger.info("✅ Family Group Photo saved as document for emp_id: {} (tempPayrollId: {})",
                    employee.getEmp_id(), employee.getTempPayrollId());
        }
    }

    /**
     * Helper: Create Family Group Photo document entity
     */
    private EmpDocuments createFamilyGroupPhotoDocument(FamilyInfoDTO familyInfo, Employee employee,
            Integer createdBy) {
        EmpDocuments doc = new EmpDocuments();
        doc.setEmp_id(employee);
        doc.setDoc_path(familyInfo.getFamilyGroupPhotoPath().trim());
        doc.setIs_verified(0);
        doc.setIs_active(1);

        // Always use "Family Group Photo" document type as requested
        doc.setEmp_doc_type_id(empDocTypeRepository.findByDocNameAndIsActive("Family Group Photo", 1)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Family Group Photo document type not found or inactive")));

        // Set created_by and created_date (required NOT NULL columns)
        // created_by must be provided by user (from DTO) - no defaults or fallbacks
        if (createdBy == null || createdBy <= 0) {
            throw new ResourceNotFoundException(
                    "createdBy is required (NOT NULL column). Please provide createdBy in FamilyInfoDTO.");
        }
        doc.setCreated_by(createdBy);
        doc.setCreated_date(LocalDateTime.now());

        return doc;
    }

    // ============================================================================
    // HELPER METHODS - Experience Operations
    // ============================================================================

    /**
     * Helper: Save Experience Entities
     */
    private int saveExperienceEntities(Employee employee, PreviousEmployerInfoDTO previousEmployerInfo,
            Integer createdBy, Integer updatedBy) {
        List<EmpExperienceDetails> experienceEntities = prepareExperienceEntities(previousEmployerInfo, employee,
                createdBy);
        updateOrCreateExperienceEntities(experienceEntities, employee, previousEmployerInfo, updatedBy); // Pass DTO to
                                                                                                         // handle
                                                                                                         // documents
        return experienceEntities.size();
    }

    /**
     * Helper: Prepare Experience entities WITHOUT saving
     */
    private List<EmpExperienceDetails> prepareExperienceEntities(PreviousEmployerInfoDTO previousEmployerInfo,
            Employee employee, Integer createdBy) {
        List<EmpExperienceDetails> experienceList = new ArrayList<>();

        if (previousEmployerInfo == null || previousEmployerInfo.getPreviousEmployers() == null
                || previousEmployerInfo.getPreviousEmployers().isEmpty()) {
            return experienceList;
        }

        for (PreviousEmployerInfoDTO.EmployerDetailsDTO employer : previousEmployerInfo.getPreviousEmployers()) {
            if (employer != null) {
                EmpExperienceDetails experience = createExperienceEntity(employer, employee, createdBy);
                experienceList.add(experience);
            }
        }

        return experienceList;
    }

    /**
     * Helper: Create Experience entity
     */
    private EmpExperienceDetails createExperienceEntity(PreviousEmployerInfoDTO.EmployerDetailsDTO employerDTO,
            Employee employee, Integer createdBy) {
        EmpExperienceDetails experience = new EmpExperienceDetails();
        experience.setEmployee_id(employee);

        if (employerDTO.getCompanyName() != null) {
            String companyName = employerDTO.getCompanyName().trim();
            if (companyName.length() > 50) {
                companyName = companyName.substring(0, 50);
            }
            experience.setPre_organigation_name(companyName);
        } else {
            throw new ResourceNotFoundException("Company Name is required (NOT NULL column)");
        }

        if (employerDTO.getFromDate() != null) {
            experience.setDate_of_join(employerDTO.getFromDate());
        } else {
            throw new ResourceNotFoundException("From Date is required (NOT NULL column)");
        }

        if (employerDTO.getToDate() != null) {
            experience.setDate_of_leave(employerDTO.getToDate());
        } else {
            throw new ResourceNotFoundException("To Date is required (NOT NULL column)");
        }

        if (employerDTO.getDesignation() != null) {
            String designation = employerDTO.getDesignation().trim();
            if (designation.length() > 50) {
                designation = designation.substring(0, 50);
            }
            experience.setDesignation(designation);
        } else {
            throw new ResourceNotFoundException("Designation is required (NOT NULL column)");
        }

        if (employerDTO.getLeavingReason() != null) {
            String leavingReason = employerDTO.getLeavingReason().trim();
            if (leavingReason.length() > 50) {
                leavingReason = leavingReason.substring(0, 50);
            }
            experience.setLeaving_reason(leavingReason);
        } else {
            throw new ResourceNotFoundException("Leaving Reason is required (NOT NULL column)");
        }

        if (employerDTO.getNatureOfDuties() != null) {
            String natureOfDuties = employerDTO.getNatureOfDuties().trim();
            if (natureOfDuties.length() > 50) {
                natureOfDuties = natureOfDuties.substring(0, 50);
            }
            experience.setNature_of_duties(natureOfDuties);
        } else {
            throw new ResourceNotFoundException("Nature of Duties is required (NOT NULL column)");
        }

        String companyAddress = employerDTO.getCompanyAddressLine1() != null ? employerDTO.getCompanyAddressLine1()
                : "";

        if (companyAddress.trim().isEmpty()) {
            throw new ResourceNotFoundException("Company Address is required (NOT NULL column)");
        }
        String trimmedAddress = companyAddress.trim();
        if (trimmedAddress.length() > 50) {
            trimmedAddress = trimmedAddress.substring(0, 50);
        }
        experience.setCompany_addr(trimmedAddress);

        experience.setGross_salary(
                employerDTO.getGrossSalaryPerMonth() != null ? employerDTO.getGrossSalaryPerMonth() : 0);

        if (employerDTO.getCtc() != null) {
            experience.setCtc(employerDTO.getCtc().doubleValue());
        }

        experience.setIs_active(1);

        // Set created_by and created_date (required NOT NULL columns)
        // created_by must be provided by user (from DTO) - no defaults or fallbacks
        if (createdBy == null || createdBy <= 0) {
            throw new ResourceNotFoundException(
                    "createdBy is required (NOT NULL column). Please provide createdBy in PreviousEmployerInfoDTO.");
        }
        experience.setCreated_by(createdBy);
        experience.setCreated_date(LocalDateTime.now());

        // Note: preChaitanyaId has been moved to BasicInfoDTO and Employee entity
        // It is no longer stored in EmpExperienceDetails (field removed from entity)

        return experience;
    }

    /**
     * Helper: Update or create Experience entities and their associated documents
     */
    private void updateOrCreateExperienceEntities(List<EmpExperienceDetails> newExperience, Employee employee,
            PreviousEmployerInfoDTO dto, Integer updatedBy) {
        int empId = employee.getEmp_id();
        Integer createdBy = dto.getCreatedBy();

        // 1. Get existing active experience records
        List<EmpExperienceDetails> existingExperience = empExperienceDetailsRepository.findAll().stream()
                .filter(exp -> exp.getEmployee_id() != null && exp.getEmployee_id().getEmp_id() == empId
                        && exp.getIs_active() == 1)
                .collect(Collectors.toList());

        // 2. Track which existing ones are matched to prevent duplicate matching
        java.util.Set<Integer> matchedExistingIds = new java.util.HashSet<>();

        // 3. Process newExperience list (updates or new records)
        // Since newExperience is prepared from dto.getPreviousEmployers(), we can use
        // index to sync them
        for (int i = 0; i < newExperience.size(); i++) {
            EmpExperienceDetails newExp = newExperience.get(i);
            newExp.setEmployee_id(employee);
            newExp.setIs_active(1);

            // Find match in existing experience by organization name
            EmpExperienceDetails existingMatch = existingExperience.stream()
                    .filter(existing -> !matchedExistingIds.contains(existing.getEmp_exp_detl_id()))
                    .filter(existing -> areExperiencesSame(existing, newExp))
                    .findFirst()
                    .orElse(null);

            EmpExperienceDetails savedExp;
            if (existingMatch != null) {
                matchedExistingIds.add(existingMatch.getEmp_exp_detl_id());
                boolean changed = updateExperienceFields(existingMatch, newExp);
                // Set updated_by and updated_date ONLY if something changed
                if (changed) {
                    if (updatedBy != null && updatedBy > 0) {
                        existingMatch.setUpdated_by(updatedBy);
                        existingMatch.setUpdated_date(LocalDateTime.now());
                    }
                    savedExp = empExperienceDetailsRepository.save(existingMatch);
                } else {
                    savedExp = existingMatch;
                }
            } else {
                // New record - no match found in existing
                savedExp = empExperienceDetailsRepository.save(newExp);
            }

            // Handle documents for this employer (use index i to link to correct DTO item)
            if (dto.getPreviousEmployers().get(i).getDocuments() != null) {
                saveEmployerDocuments(employee, savedExp, dto.getPreviousEmployers().get(i).getDocuments(),
                        createdBy, updatedBy);
            }
        }

        // 4. Deactivate existing records that weren't in the new list
        for (EmpExperienceDetails existing : existingExperience) {
            if (!matchedExistingIds.contains(existing.getEmp_exp_detl_id())) {
                existing.setIs_active(0);
                if (updatedBy != null && updatedBy > 0) {
                    existing.setUpdated_by(updatedBy);
                    existing.setUpdated_date(LocalDateTime.now());
                }
                empExperienceDetailsRepository.save(existing);
                // Deactivate associated documents
                deactivateExperienceDocuments(existing.getEmp_exp_detl_id(), updatedBy);
            }
        }
    }

    /**
     * Helper: Check if two experience records are the same (used for matching)
     */
    private boolean areExperiencesSame(EmpExperienceDetails e1, EmpExperienceDetails e2) {
        if (e1 == null || e2 == null)
            return false;
        String n1 = e1.getPre_organigation_name() != null ? e1.getPre_organigation_name().trim() : "";
        String n2 = e2.getPre_organigation_name() != null ? e2.getPre_organigation_name().trim() : "";
        return n1.equalsIgnoreCase(n2);
    }

    /**
     * Helper: Save documents for a specific experience record
     */
    private void saveEmployerDocuments(Employee employee, EmpExperienceDetails experience,
            List<PreviousEmployerInfoDTO.ExperienceDocumentDTO> docs, Integer createdBy, Integer updatedBy) {
        // REMOVED: Unconditional deactivation of existing documents.
        // User requested that documents remain active (is_active=1) during updates.

        for (PreviousEmployerInfoDTO.ExperienceDocumentDTO docDTO : docs) {
            String path = docDTO.getDocPath() != null ? docDTO.getDocPath().trim() : "";
            if (path.isEmpty() || "string".equalsIgnoreCase(path)) {
                continue;
            }

            Integer docTypeId = docDTO.getDocTypeId();
            if (docTypeId == null) {
                throw new ResourceNotFoundException("Document Type ID is required for experience documents");
            }

            // SMART CHECK: Only save if this documents doesn't already exist for this
            // experience record
            if (empDocumentsRepository.findExistingExperienceDoc(experience.getEmp_exp_detl_id(), docTypeId, path)
                    .isPresent()) {
                logger.info("📄 Skipping duplicate document (Type: {}, Path: {}) for experience ID: {}", docTypeId,
                        path, experience.getEmp_exp_detl_id());
                continue;
            }

            EmpDocuments doc = new EmpDocuments();
            doc.setEmp_id(employee);
            doc.setEmp_exp_detl_id(experience);
            doc.setDoc_path(path);
            doc.setIs_verified(0);
            doc.setIs_active(1);

            doc.setEmp_doc_type_id(empDocTypeRepository.findById(docTypeId)
                    .orElseThrow(() -> new ResourceNotFoundException("Document Type not found with ID: " + docTypeId)));

            doc.setCreated_by(createdBy != null ? createdBy : 1);
            doc.setCreated_date(LocalDateTime.now());
            if (updatedBy != null) {
                doc.setUpdated_by(updatedBy);
                doc.setUpdated_date(LocalDateTime.now());
            }

            empDocumentsRepository.save(doc);
            logger.info("✅ Saved new document for experience ID: {}", experience.getEmp_exp_detl_id());
        }
    }

    /**
     * Helper: Deactivate all documents for a specific experience record
     */
    private void deactivateExperienceDocuments(int expId, Integer updatedBy) {
        List<EmpDocuments> existingDocs = empDocumentsRepository.findAll().stream()
                .filter(d -> d.getEmp_exp_detl_id() != null && d.getEmp_exp_detl_id().getEmp_exp_detl_id() == expId
                        && d.getIs_active() == 1)
                .collect(Collectors.toList());

        for (EmpDocuments doc : existingDocs) {
            doc.setIs_active(0);
            if (updatedBy != null) {
                doc.setUpdated_by(updatedBy);
                doc.setUpdated_date(LocalDateTime.now());
            }
            empDocumentsRepository.save(doc);
        }
    }

    private boolean updateExperienceFields(EmpExperienceDetails target, EmpExperienceDetails source) {
        boolean changed = false;

        // Hyper-robust string helper: treats null, empty, and whitespace as same,
        // case-insensitive
        java.util.function.BiPredicate<String, String> areStringsEqual = (s1, s2) -> {
            String str1 = (s1 == null || s1.trim().isEmpty()) ? "" : s1.trim();
            String str2 = (s2 == null || s2.trim().isEmpty()) ? "" : s2.trim();
            return str1.equalsIgnoreCase(str2);
        };

        // Hyper-robust numeric helper: treats null and 0 as same
        java.util.function.BiPredicate<Number, Number> areNumbersEqual = (n1, n2) -> {
            long v1 = (n1 == null) ? 0L : n1.longValue();
            long v2 = (n2 == null) ? 0L : n2.longValue();
            return v1 == v2;
        };

        // Hyper-robust date helper: compares YYYY-MM-DD using toLocalDate
        java.util.function.BiPredicate<java.sql.Date, java.sql.Date> areDatesEqual = (d1, d2) -> {
            if (d1 == null && d2 == null)
                return true;
            if (d1 == null || d2 == null)
                return false;
            return d1.toLocalDate().equals(d2.toLocalDate());
        };

        if (!areStringsEqual.test(target.getPre_organigation_name(), source.getPre_organigation_name())) {
            target.setPre_organigation_name(
                    source.getPre_organigation_name() != null ? source.getPre_organigation_name().trim() : null);
            changed = true;
        }
        if (!areDatesEqual.test(target.getDate_of_join(), source.getDate_of_join())) {
            target.setDate_of_join(source.getDate_of_join());
            changed = true;
        }
        if (!areDatesEqual.test(target.getDate_of_leave(), source.getDate_of_leave())) {
            target.setDate_of_leave(source.getDate_of_leave());
            changed = true;
        }
        if (!areStringsEqual.test(target.getDesignation(), source.getDesignation())) {
            target.setDesignation(source.getDesignation() != null ? source.getDesignation().trim() : null);
            changed = true;
        }
        if (!areStringsEqual.test(target.getLeaving_reason(), source.getLeaving_reason())) {
            target.setLeaving_reason(source.getLeaving_reason() != null ? source.getLeaving_reason().trim() : null);
            changed = true;
        }
        if (!areStringsEqual.test(target.getNature_of_duties(), source.getNature_of_duties())) {
            target.setNature_of_duties(
                    source.getNature_of_duties() != null ? source.getNature_of_duties().trim() : null);
            changed = true;
        }
        if (!areStringsEqual.test(target.getCompany_addr(), source.getCompany_addr())) {
            target.setCompany_addr(source.getCompany_addr() != null ? source.getCompany_addr().trim() : null);
            changed = true;
        }
        if (!areNumbersEqual.test(target.getGross_salary(), source.getGross_salary())) {
            target.setGross_salary(source.getGross_salary());
            changed = true;
        }

        if (!Objects.equals(target.getCtc(), source.getCtc())) {
            target.setCtc(source.getCtc());
            changed = true;
        }
        if (target.getIs_active() != source.getIs_active()) {
            target.setIs_active(source.getIs_active());
            changed = true;
        }
        return changed;
    }

    // ============================================================================
    // HELPER METHODS - Validation Operations
    // ============================================================================

    /**
     * Helper: Validate prepared entities BEFORE saving to database
     * This is the FINAL check before employee.save() consumes a sequence number
     */
    private void validatePreparedEntities(Employee employee, EmpDetails empDetails, EmpPfDetails empPfDetails) {
        if (employee == null) {
            throw new ResourceNotFoundException("Employee entity cannot be null");
        }

        if (employee.getFirst_name() == null || employee.getFirst_name().trim().isEmpty()) {
            throw new ResourceNotFoundException("Employee first name is required");
        }

        if (employee.getLast_name() == null || employee.getLast_name().trim().isEmpty()) {
            throw new ResourceNotFoundException("Employee last name is required");
        }

        Integer empCreatedBy = employee.getCreated_by();
        if (empCreatedBy == null || empCreatedBy <= 0) {
            throw new ResourceNotFoundException("Employee created_by must be set (NOT NULL column)");
        }

        if (empDetails == null) {
            throw new ResourceNotFoundException("EmpDetails entity cannot be null");
        }

        Integer detailsCreatedBy = empDetails.getCreated_by();
        if (detailsCreatedBy == null || detailsCreatedBy <= 0) {
            empDetails.setCreated_by(empCreatedBy);
        }

        if (empDetails.getEmergency_ph_no() == null || empDetails.getEmergency_ph_no().trim().isEmpty()) {
            throw new ResourceNotFoundException("EmpDetails: Emergency Phone Number is required (NOT NULL column)");
        }

        if (empPfDetails != null) {
            Integer pfCreatedBy = empPfDetails.getCreated_by();
            if (pfCreatedBy == null || pfCreatedBy <= 0) {
                empPfDetails.setCreated_by(empCreatedBy);
            }
        }
    }

    /**
     * Helper: Validate entity constraints BEFORE saving to prevent emp_id sequence
     * consumption on failure
     * This checks all @NotNull, @Min, @Max constraints that would cause
     * ConstraintViolationException
     */
    private void validateEntityConstraints(Employee employee, EmpDetails empDetails, EmpPfDetails empPfDetails) {
        // Validate Employee entity constraints
        if (employee.getCreated_date() == null) {
            throw new ResourceNotFoundException("Employee created_date is required (NOT NULL column)");
        }
        if (employee.getCreated_by() == null || employee.getCreated_by() <= 0) {
            throw new ResourceNotFoundException("Employee created_by is required (NOT NULL column)");
        }
        if (employee.getEmp_status_id() == null) {
            throw new ResourceNotFoundException("Employee emp_status_id is required (NOT NULL column)");
        }
        if (employee.getEmp_check_list_status_id() == null) {
            throw new ResourceNotFoundException("Employee emp_app_status_id is required (NOT NULL column)");
        }

        // Validate EmpDetails entity constraints
        if (empDetails.getCreated_date() == null) {
            throw new ResourceNotFoundException("EmpDetails created_date is required (NOT NULL column)");
        }
        if (empDetails.getCreated_by() == null || empDetails.getCreated_by() <= 0) {
            throw new ResourceNotFoundException("EmpDetails created_by is required (NOT NULL column)");
        }
        if (empDetails.getFatherName() == null || empDetails.getFatherName().trim().isEmpty()) {
            throw new ResourceNotFoundException("EmpDetails fatherName is required (@NotNull constraint)");
        }
        if (empDetails.getUanNo() == null) {
            throw new ResourceNotFoundException("EmpDetails uanNo is required (@NotNull constraint)");
        }
        if (empDetails.getUanNo() < 100000000000L || empDetails.getUanNo() > 999999999999L) {
            throw new ResourceNotFoundException(
                    "EmpDetails uanNo must be between 100000000000 and 999999999999 (@Min/@Max constraint)");
        }

        // Validate EmpPfDetails entity constraints (if present)
        if (empPfDetails != null) {
            if (empPfDetails.getCreated_date() == null) {
                throw new ResourceNotFoundException("EmpPfDetails created_date is required (NOT NULL column)");
            }
            if (empPfDetails.getCreated_by() == null || empPfDetails.getCreated_by() <= 0) {
                throw new ResourceNotFoundException("EmpPfDetails created_by is required (NOT NULL column)");
            }
        }
    }

    /**
     * Helper: Validate Basic Info DTO
     */
    private void validateBasicInfo(BasicInfoDTO basicInfo, String tempPayrollId) {
        if (basicInfo == null) {
            throw new ResourceNotFoundException("Basic Info is required");
        }

        if (basicInfo.getFirstName() == null || basicInfo.getFirstName().trim().isEmpty()) {
            throw new ResourceNotFoundException("First Name is required");
        }

        if (basicInfo.getFirstName().length() > 50) {
            throw new ResourceNotFoundException("First Name cannot exceed 50 characters");
        }

        if (basicInfo.getLastName() == null || basicInfo.getLastName().trim().isEmpty()) {
            throw new ResourceNotFoundException("Last Name is required");
        }

        if (basicInfo.getLastName().length() > 50) {
            throw new ResourceNotFoundException("Last Name cannot exceed 50 characters");
        }

        if (basicInfo.getDateOfJoin() == null) {
            throw new ResourceNotFoundException("Date of Join is required");
        }

        if (basicInfo.getPrimaryMobileNo() == null || basicInfo.getPrimaryMobileNo() == 0) {
            throw new ResourceNotFoundException("Primary Mobile Number is required");
        }

        if (basicInfo.getEmail() != null && basicInfo.getEmail().length() > 50) {
            throw new ResourceNotFoundException("Email cannot exceed 50 characters");
        }

        if (basicInfo.getGenderId() != null) {
            genderRepository.findById(basicInfo.getGenderId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Gender not found with ID: " + basicInfo.getGenderId()));
        } else {
            throw new ResourceNotFoundException("Gender ID is required (NOT NULL column)");
        }

        // Note: designationId and departmentId are now handled in CategoryInfoDTO only
        // Validation for these fields should be done when CategoryInfo is saved

        if (basicInfo.getCategoryId() != null) {
            categoryRepository.findById(basicInfo.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Category not found with ID: " + basicInfo.getCategoryId()));
        } else {
            throw new ResourceNotFoundException("Category ID is required (NOT NULL column)");
        }

        if (basicInfo.getReferenceEmpId() != null && basicInfo.getReferenceEmpId() > 0) {
            employeeRepository.findByIdAndIs_active(basicInfo.getReferenceEmpId(), 1)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Active Reference Employee not found with ID: " + basicInfo.getReferenceEmpId()));
        }

        if (basicInfo.getHiredByEmpId() != null && basicInfo.getHiredByEmpId() > 0) {
            employeeRepository.findByIdAndIs_active(basicInfo.getHiredByEmpId(), 1)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Active Hired By Employee not found with ID: " + basicInfo.getHiredByEmpId()));
        }

        if (basicInfo.getManagerId() != null && basicInfo.getManagerId() > 0) {
            employeeRepository.findByIdAndIs_active(basicInfo.getManagerId(), 1)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Active Manager not found with ID: " + basicInfo.getManagerId()));
        }

        if (basicInfo.getReportingManagerId() != null && basicInfo.getReportingManagerId() > 0) {
            employeeRepository.findByIdAndIs_active(basicInfo.getReportingManagerId(), 1)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Active Reporting Manager not found with ID: " + basicInfo.getReportingManagerId()));
        }

        if (basicInfo.getReplacedByEmpId() != null && basicInfo.getReplacedByEmpId() > 0) {
            employeeRepository.findById(basicInfo.getReplacedByEmpId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Replacement Employee not found with ID: " + basicInfo.getReplacedByEmpId()));
        }

        // Validate preChaitanyaId: if entered, must be an inactive employee (is_active
        // = 0)
        if (basicInfo.getPreChaitanyaId() != null && !basicInfo.getPreChaitanyaId().trim().isEmpty()
                && !"0".equals(basicInfo.getPreChaitanyaId().trim())) {
            String prevPayrollId = basicInfo.getPreChaitanyaId().trim();
            employeeRepository.findByPayRollIdAndIs_active(prevPayrollId, 0)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Previous Chaitanya Employee not found with Payroll ID: "
                                    + prevPayrollId
                                    + ". Only inactive employees (is_active = 0) can be used as previous Chaitanya employee."));
        }

        if (basicInfo.getCampusId() != null && basicInfo.getCampusId() > 0) {
            campusRepository.findByCampusIdAndIsActive(basicInfo.getCampusId(), 1)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Active Campus not found with ID: " + basicInfo.getCampusId()));
        }

        if (basicInfo.getEmpTypeId() != null) {
            employeeTypeRepository.findById(basicInfo.getEmpTypeId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Employee Type not found with ID: " + basicInfo.getEmpTypeId()));
        }

        // Note: qualificationId is now passed from BasicInfoDTO (not from qualification
        // tab's isHighest)
        if (basicInfo.getQualificationId() != null) {
            qualificationRepository.findById(basicInfo.getQualificationId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Qualification not found with ID: " + basicInfo.getQualificationId()));
        }

        if (basicInfo.getEmpWorkModeId() != null) {
            workingModeRepository.findById(basicInfo.getEmpWorkModeId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Working Mode not found with ID: " + basicInfo.getEmpWorkModeId()));
        }

        if (basicInfo.getJoinTypeId() != null) {
            joiningAsRepository.findById(basicInfo.getJoinTypeId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Join Type not found with ID: " + basicInfo.getJoinTypeId()));

            if (basicInfo.getJoinTypeId() == 3) {
                if (basicInfo.getReplacedByEmpId() == null || basicInfo.getReplacedByEmpId() <= 0) {
                    throw new ResourceNotFoundException(
                            "replacedByEmpId is required when joinTypeId is 3 (Replacement). Please provide a valid replacement employee ID.");
                }
            }
        }

        if (basicInfo.getModeOfHiringId() != null) {
            modeOfHiringRepository.findById(basicInfo.getModeOfHiringId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Mode of Hiring not found with ID: " + basicInfo.getModeOfHiringId()));
        }

        if (basicInfo.getEmpTypeHiringId() != null) {
            employeeTypeHiringRepository.findById(basicInfo.getEmpTypeHiringId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Employee Type Hiring not found with ID: " + basicInfo.getEmpTypeHiringId()));
        }

        // Validate Aadhaar Number format IF provided (optional field)
        // CHANGED: Validation now supports Long type (removed .trim())
        if (basicInfo.getAdhaarNo() != null && basicInfo.getAdhaarNo() > 0) {
            String aadhar = String.valueOf(basicInfo.getAdhaarNo());

            // Layer 1: Format validation - must be exactly 12 numeric digits
            if (!aadhar.matches("^[0-9]{12}$")) {
                throw new ResourceNotFoundException(
                        "Aadhaar must be exactly 12 numeric digits. Please remove any spaces, dashes, or special characters.");
            }

            // Layer 2: Verhoeff algorithm validation - checks mathematical validity
            if (!isValidAadhaar(aadhar)) {
                throw new ResourceNotFoundException(
                        "Invalid Aadhaar number format. Please verify the Aadhaar number and try again.");
            }
        }

        // Validate tempPayrollId against SkillTestDetl table if provided
        // REMOVED VALIDATION: Allow tempPayrollId even if not found in Skill Test
        // Details (as per user request)
        /*
         * if (tempPayrollId != null && !tempPayrollId.trim().isEmpty()) {
         * skillTestDetailsRepository.findByTempPayrollId(tempPayrollId)
         * .orElseThrow(() -> new
         * ResourceNotFoundException("Temp Payroll ID not found in Skill Test Details: "
         * + tempPayrollId + ". Please provide a valid temp payroll ID."));
         * }
         */
    }

    /**
     * Helper: Validate Address Info DTO
     */
    private void validateAddressInfo(AddressInfoDTO addressInfo) {
        if (addressInfo == null) {
            return; // Address info is optional
        }

        if (addressInfo.getCurrentAddress() != null) {
            if (addressInfo.getCurrentAddress().getCityId() != null) {
                cityRepository.findById(addressInfo.getCurrentAddress().getCityId())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "City not found with ID: " + addressInfo.getCurrentAddress().getCityId()));
            }
            if (addressInfo.getCurrentAddress().getStateId() != null) {
                stateRepository.findById(addressInfo.getCurrentAddress().getStateId())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "State not found with ID: " + addressInfo.getCurrentAddress().getStateId()));
            }
            if (addressInfo.getCurrentAddress().getCountryId() != null) {
                countryRepository.findById(addressInfo.getCurrentAddress().getCountryId())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Country not found with ID: " + addressInfo.getCurrentAddress().getCountryId()));
            }
            if (addressInfo.getCurrentAddress().getDistrictId() != null) {
                districtRepository.findById(addressInfo.getCurrentAddress().getDistrictId())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "District not found with ID: " + addressInfo.getCurrentAddress().getDistrictId()));
            }
            if (addressInfo.getCurrentAddress().getPin() != null
                    && addressInfo.getCurrentAddress().getPin().length() > 10) {
                throw new ResourceNotFoundException("PIN code cannot exceed 10 characters");
            }
            if (addressInfo.getCurrentAddress().getName() != null
                    && addressInfo.getCurrentAddress().getName().length() > 50) {
                throw new ResourceNotFoundException("Address name cannot exceed 50 characters");
            }
        }

        // Only validate permanent address if permanentAddressSameAsCurrent is NOT true
        // If permanentAddressSameAsCurrent = true, permanent address is ignored (can be
        // null/empty)
        if (!Boolean.TRUE.equals(addressInfo.getPermanentAddressSameAsCurrent())
                && addressInfo.getPermanentAddress() != null) {
            if (addressInfo.getPermanentAddress().getCityId() != null) {
                cityRepository.findById(addressInfo.getPermanentAddress().getCityId())
                        .orElseThrow(() -> new ResourceNotFoundException("Permanent Address City not found with ID: "
                                + addressInfo.getPermanentAddress().getCityId()));
            }
            if (addressInfo.getPermanentAddress().getStateId() != null) {
                stateRepository.findById(addressInfo.getPermanentAddress().getStateId())
                        .orElseThrow(() -> new ResourceNotFoundException("Permanent Address State not found with ID: "
                                + addressInfo.getPermanentAddress().getStateId()));
            }
            if (addressInfo.getPermanentAddress().getCountryId() != null) {
                countryRepository.findById(addressInfo.getPermanentAddress().getCountryId())
                        .orElseThrow(() -> new ResourceNotFoundException("Permanent Address Country not found with ID: "
                                + addressInfo.getPermanentAddress().getCountryId()));
            }
            if (addressInfo.getPermanentAddress().getDistrictId() != null) {
                districtRepository.findById(addressInfo.getPermanentAddress().getDistrictId())
                        .orElseThrow(
                                () -> new ResourceNotFoundException("Permanent Address District not found with ID: "
                                        + addressInfo.getPermanentAddress().getDistrictId()));
            }
            if (addressInfo.getPermanentAddress().getPin() != null
                    && addressInfo.getPermanentAddress().getPin().length() > 10) {
                throw new ResourceNotFoundException("PIN code cannot exceed 10 characters");
            }
            if (addressInfo.getPermanentAddress().getName() != null
                    && addressInfo.getPermanentAddress().getName().length() > 50) {
                throw new ResourceNotFoundException("Address name cannot exceed 50 characters");
            }
        }
    }

    /**
     * Helper: Validate Family Info DTO
     */
    private void validateFamilyInfo(FamilyInfoDTO familyInfo) {
        if (familyInfo == null) {
            return; // Family info is optional
        }

        // Validate Family Group Photo document type if provided
        if (familyInfo.getFamilyGroupPhotoPath() != null && !familyInfo.getFamilyGroupPhotoPath().trim().isEmpty()
                && !"string".equalsIgnoreCase(familyInfo.getFamilyGroupPhotoPath().trim())) {
            empDocTypeRepository.findByDocNameAndIsActive("Family Group Photo", 1)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Family Group Photo document type not found or inactive."));
        }

        if (familyInfo.getFamilyMembers() != null) {
            for (FamilyInfoDTO.FamilyMemberDTO member : familyInfo.getFamilyMembers()) {
                if (member == null)
                    continue;

                // Skip validation for "junk" family members (e.g., from Swagger default values)
                if (member.getFullName() == null || member.getFullName().trim().isEmpty()
                        || "string".equalsIgnoreCase(member.getFullName().trim())) {
                    continue;
                }

                // 1. Validate Full Name (Already checked above, but kept for consistency if
                // needed)
                if (member.getFullName() == null || member.getFullName().trim().isEmpty()) {
                    throw new ResourceNotFoundException("Full Name is required for all family members.");
                }

                // 2. Relation Validation
                if (member.getRelationId() == null) {
                    throw new ResourceNotFoundException(
                            "Relation ID is required for family member: " + member.getFullName());
                }

                relationRepository.findById(member.getRelationId())
                        .orElseThrow(() -> new ResourceNotFoundException("Relation not found with ID: "
                                + member.getRelationId() + " for family member: " + member.getFullName()));

                // 3. Gender Validation: For Father (1) and Mother (2), gender is auto-set by
                // backend
                if (member.getRelationId() != 1 && member.getRelationId() != 2) {
                    if (member.getGenderId() == null) {
                        throw new ResourceNotFoundException(
                                "Gender ID is required for family member: " + member.getFullName() +
                                        " (relationId: " + member.getRelationId()
                                        + "). Gender is only auto-set for Father and Mother.");
                    }
                    genderRepository.findById(member.getGenderId())
                            .orElseThrow(() -> new ResourceNotFoundException("Gender not found with ID: "
                                    + member.getGenderId() + " for family member: " + member.getFullName()));
                }

                // 4. Blood Group Validation - NOW OPTIONAL
                if (member.getBloodGroupId() != null && member.getBloodGroupId() > 0) {
                    bloodGroupRepository.findByIdAndIsActive(member.getBloodGroupId(), 1)
                            .orElseThrow(() -> new ResourceNotFoundException("Active Blood Group not found with ID: "
                                    + member.getBloodGroupId() + " for family member: " + member.getFullName()));
                }

                // 5. Nationality Validation
                if (member.getNationality() == null || member.getNationality().trim().isEmpty()) {
                    throw new ResourceNotFoundException(
                            "Nationality is required for family member: " + member.getFullName());
                }

                // 6. Occupation Validation
                if (member.getOccupation() == null || member.getOccupation().trim().isEmpty()) {
                    throw new ResourceNotFoundException(
                            "Occupation is required for family member: " + member.getFullName());
                }
            }
        }
    }

    /**
     * Helper: Validate Previous Employer Info DTO
     */
    private void validatePreviousEmployerInfo(PreviousEmployerInfoDTO previousEmployerInfo) {
        if (previousEmployerInfo == null) {
            return; // Previous employer info is optional
        }

        if (previousEmployerInfo.getPreviousEmployers() != null) {
            for (PreviousEmployerInfoDTO.EmployerDetailsDTO employer : previousEmployerInfo.getPreviousEmployers()) {
                if (employer == null)
                    continue;

                if (employer.getCompanyName() == null || employer.getCompanyName().trim().isEmpty()) {
                    throw new ResourceNotFoundException("Company Name is required for previous employer");
                }
                if (employer.getCompanyName().length() > 50) {
                    throw new ResourceNotFoundException("Company Name cannot exceed 50 characters");
                }
                if (employer.getFromDate() == null) {
                    throw new ResourceNotFoundException(
                            "From Date is required for previous employer: " + employer.getCompanyName());
                }
                if (employer.getToDate() == null) {
                    throw new ResourceNotFoundException(
                            "To Date is required for previous employer: " + employer.getCompanyName());
                }
                if (employer.getDesignation() == null || employer.getDesignation().trim().isEmpty()) {
                    throw new ResourceNotFoundException(
                            "Designation is required for previous employer: " + employer.getCompanyName());
                }
                if (employer.getDesignation().length() > 50) {
                    throw new ResourceNotFoundException("Designation cannot exceed 50 characters");
                }
                if (employer.getLeavingReason() == null || employer.getLeavingReason().trim().isEmpty()) {
                    throw new ResourceNotFoundException(
                            "Leaving Reason is required for previous employer: " + employer.getCompanyName());
                }
                if (employer.getLeavingReason().length() > 50) {
                    throw new ResourceNotFoundException("Leaving Reason cannot exceed 50 characters");
                }
                if (employer.getNatureOfDuties() == null || employer.getNatureOfDuties().trim().isEmpty()) {
                    throw new ResourceNotFoundException(
                            "Nature of Duties is required for previous employer: " + employer.getCompanyName());
                }
                if (employer.getNatureOfDuties().length() > 50) {
                    throw new ResourceNotFoundException("Nature of Duties cannot exceed 50 characters");
                }
                if (employer.getCompanyAddressLine1() == null || employer.getCompanyAddressLine1().trim().isEmpty()) {
                    throw new ResourceNotFoundException(
                            "Company Address Line 1 is required for previous employer: " + employer.getCompanyName());
                }
                String companyAddress = employer.getCompanyAddressLine1();
                if (companyAddress.trim().length() > 50) {
                    throw new ResourceNotFoundException("Company Address cannot exceed 50 characters");
                }

                // Validate documents for this employer
                if (employer.getDocuments() != null) {
                    for (PreviousEmployerInfoDTO.ExperienceDocumentDTO doc : employer.getDocuments()) {
                        if (doc.getDocPath() == null || doc.getDocPath().trim().isEmpty()
                                || "string".equalsIgnoreCase(doc.getDocPath().trim())) {
                            continue;
                        }
                        if (doc.getDocTypeId() == null) {
                            throw new ResourceNotFoundException(
                                    "Document Type ID is required for documents of company: "
                                            + employer.getCompanyName());
                        }
                        empDocTypeRepository.findById(doc.getDocTypeId())
                                .orElseThrow(() -> new ResourceNotFoundException(
                                        "Document Type with ID " + doc.getDocTypeId()
                                                + " not found or inactive for company: " + employer.getCompanyName()));
                    }
                }
            }
        }
    }

    /**
     * Validate Aadhaar number using Verhoeff algorithm
     * This algorithm checks the mathematical validity of the Aadhaar number
     * structure
     * * @param aadhaar 12-digit Aadhaar number
     * 
     * @return true if Aadhaar format is valid, false otherwise
     */
    private boolean isValidAadhaar(String aadhaar) {
        // 1. Basic Regex Check: Length 12, cannot start with 0 or 1, numeric only
        if (aadhaar == null || !aadhaar.matches("^[2-9][0-9]{11}$")) {
            return false;
        }

        // Verhoeff multiplication table
        int[][] multiplicationTable = {
                { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 },
                { 1, 2, 3, 4, 0, 6, 7, 8, 9, 5 },
                { 2, 3, 4, 0, 1, 7, 8, 9, 5, 6 },
                { 3, 4, 0, 1, 2, 8, 9, 5, 6, 7 },
                { 4, 0, 1, 2, 3, 9, 5, 6, 7, 8 },
                { 5, 9, 8, 7, 6, 0, 4, 3, 2, 1 },
                { 6, 5, 9, 8, 7, 1, 0, 4, 3, 2 },
                { 7, 6, 5, 9, 8, 2, 1, 0, 4, 3 },
                { 8, 7, 6, 5, 9, 3, 2, 1, 0, 4 },
                { 9, 8, 7, 6, 5, 4, 3, 2, 1, 0 }
        };

        // Verhoeff permutation table
        int[][] permutationTable = {
                { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 },
                { 1, 5, 7, 6, 2, 8, 3, 0, 9, 4 },
                { 5, 8, 0, 3, 7, 9, 6, 1, 4, 2 },
                { 8, 9, 1, 6, 0, 4, 3, 5, 2, 7 },
                { 9, 4, 5, 3, 1, 2, 6, 8, 7, 0 },
                { 4, 2, 8, 6, 5, 7, 3, 9, 0, 1 },
                { 2, 7, 9, 3, 8, 0, 6, 4, 1, 5 },
                { 7, 0, 4, 6, 9, 1, 3, 2, 5, 8 }
        };

        int check = 0;
        int[] digits = new int[12];

        for (int i = 0; i < 12; i++) {
            digits[i] = Character.getNumericValue(aadhaar.charAt(i));
        }

        // Apply Verhoeff algorithm
        for (int i = 0; i < 12; i++) {
            // FIX: Changed ((i + 1) % 8) to (i % 8)
            // When i=0 (last digit), we must use permutation row 0.
            int c = digits[11 - i];
            int p = permutationTable[i % 8][c];
            check = multiplicationTable[check][p];
        }

        return check == 0;
    }

    private boolean isConsultancyHiringType(Integer empTypeHiringId) {
        if (empTypeHiringId == null) {
            return false;
        }
        return employeeTypeHiringRepository.findById(empTypeHiringId)
                .map(type -> "CONSULTANCY".equalsIgnoreCase(type.getEmp_type_hiring_name()))
                .orElse(false);
    }
}
