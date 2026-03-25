package com.employee.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.employee.dto.BasicInfoDTO;
import com.employee.dto.EmployeeOnboardingDTO;
import com.employee.dto.TempPayrollIdResponseDTO;
import com.employee.entity.Campus;
import com.employee.entity.EmpDetails;
import com.employee.entity.EmpPfDetails;
import com.employee.entity.Employee;
import com.employee.entity.EmployeeCheckListStatus;
import com.employee.exception.ResourceNotFoundException;
import com.employee.repository.CampusRepository;
import com.employee.repository.EmpDetailsRepository;
import com.employee.repository.EmpPfDetailsRepository;
import com.employee.repository.EmployeeCheckListStatusRepository;
import com.employee.repository.EmployeeRepository;
import com.employee.repository.SkillTestDetailsRepository;

@Service
@Transactional
public class EmployeeOnboardingService {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeOnboardingService.class);

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private EmpDetailsRepository empDetailsRepository;

    @Autowired
    private EmpPfDetailsRepository empPfDetailsRepository;

    @Autowired
    private SkillTestDetailsRepository skillTestDetailsRepository;

    @Autowired
    private EmployeeCheckListStatusRepository employeeCheckListStatusRepository;

    @Autowired
    private EmployeeValidationService employeeValidationService;

    @Autowired
    private EmployeeEntityPreparationService entityPreparationService;

    @Autowired
    private CampusRepository campusRepository;

    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Transactional
    public TempPayrollIdResponseDTO generateOrValidateTempPayrollId(Integer hrEmployeeId, BasicInfoDTO basicInfo) {

        logger.info("Creating NEW employee and generating/validating temp_payroll_id. HR Employee ID (created_by): {}",
                hrEmployeeId);

        if (basicInfo == null) {
            throw new ResourceNotFoundException("Basic Info is required");
        }

        try {
            EmployeeOnboardingDTO partialOnboardingDTO = new EmployeeOnboardingDTO();
            partialOnboardingDTO.setBasicInfo(basicInfo);
            employeeValidationService.validateOnboardingData(partialOnboardingDTO);
            employeeValidationService.performPreFlightChecks(partialOnboardingDTO);
        } catch (Exception e) {
            logger.error("❌ ERROR: Employee onboarding validation failed in generateOrValidateTempPayrollId. Error: {}",
                    e.getMessage(), e);
            throw e;
        }

        Integer finalHrEmpId = hrEmployeeId;
        if (finalHrEmpId == null) {
            throw new ResourceNotFoundException("hrEmployeeId is required");
        }
        Employee hrEmployee = employeeRepository.findById(finalHrEmpId.intValue())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "HR Employee not found with emp_id: " + finalHrEmpId));

        Campus campus = null;

        // PRIORITIZE SELECTED CAMPUS FROM FORM
        if (basicInfo.getCampusId() != null && basicInfo.getCampusId() > 0) {
            campus = campusRepository.findById(basicInfo.getCampusId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Selected Campus not found with ID: " + basicInfo.getCampusId()));
        } else {
            // FALLBACK TO HR EMPLOYEE'S CAMPUS
            campus = hrEmployee.getCampus_id();
        }

        if (campus == null) {
            throw new ResourceNotFoundException(
                    "No campus specified and HR Employee (emp_id: " + hrEmployeeId
                            + ") does not have a campus assigned. Cannot generate temp_payroll_id.");
        }

        if (campus.getIsActive() == null || campus.getIsActive() != 1) {
            throw new ResourceNotFoundException(
                    "Selected campus (campus_id: " + campus.getCampusId()
                            + ") is not active. Cannot generate temp_payroll_id.");
        }

        int campusCodeInt = campus.getCode();
        if (campusCodeInt == 0) {
            throw new ResourceNotFoundException(
                    "Campus code is 0 or not set for HR Employee's campus_id: " + campus.getCampusId() +
                            ". Cannot generate temp_payroll_id.");
        }

        String campusCode = String.valueOf(campusCodeInt);
        String baseKey = "TEMP" + campusCode;
        logger.info("Base key for temp_payroll_id generation using HR Employee's campus code: {}", baseKey);

        String tempPayrollIdFromFrontend = basicInfo.getTempPayrollId();
        Long aadharNum = basicInfo.getAdhaarNo();
        Long phoneNumber = basicInfo.getPrimaryMobileNo();

        logger.info("Received values - tempPayrollId: '{}', aadharNum: '{}', phoneNumber: {}",
                tempPayrollIdFromFrontend, aadharNum, phoneNumber);

        if (tempPayrollIdFromFrontend == null || tempPayrollIdFromFrontend.trim().isEmpty()) {

            if (aadharNum == null || aadharNum <= 0) {
                logger.error("❌ Validation failed: aadharNum is null or empty. Received BasicInfoDTO: {}", basicInfo);
                throw new ResourceNotFoundException(
                        "Aadhaar number (aadharNum) is required for temp_payroll_id generation when tempPayrollId is not provided. Please provide 'aadharNum' field in the request body.");
            }

            if (phoneNumber == null || phoneNumber <= 0) {
                logger.error("❌ Validation failed: primaryMobileNo is null. Received BasicInfoDTO: {}", basicInfo);
                throw new ResourceNotFoundException(
                        "Phone number (primaryMobileNo) is required for temp_payroll_id generation when tempPayrollId is not provided. Please provide 'primaryMobileNo' field in the request body.");
            }
        }

        String finalTempPayrollId = null;
        Employee existingEmployee = null;
        boolean isUpdate = false;

        if (tempPayrollIdFromFrontend != null && !tempPayrollIdFromFrontend.trim().isEmpty()) {
            tempPayrollIdFromFrontend = tempPayrollIdFromFrontend.trim();
            logger.info("tempPayrollId provided from frontend: {}", tempPayrollIdFromFrontend);

            Optional<com.employee.entity.SkillTestDetails> skillTestDetails = skillTestDetailsRepository
                    .findActiveByTempPayrollId(tempPayrollIdFromFrontend);

            Optional<Employee> existingEmployeeOpt = employeeRepository.findByTempPayrollId(tempPayrollIdFromFrontend);

            boolean foundInSkillTest = skillTestDetails.isPresent();
            boolean foundInEmployee = existingEmployeeOpt.isPresent();

            if (foundInSkillTest && foundInEmployee) {
                existingEmployee = existingEmployeeOpt.get();
                isUpdate = true;
                logger.info(
                        "✅ tempPayrollId '{}' found in BOTH SkillTestDetails and Employee table (emp_id: {}). UPDATE MODE: Will update existing employee.",
                        tempPayrollIdFromFrontend, existingEmployee.getEmp_id());
                finalTempPayrollId = tempPayrollIdFromFrontend;
            } else if (foundInSkillTest && !foundInEmployee) {
                logger.info(
                        "✅ tempPayrollId '{}' found in SkillTestDetails table but NOT in Employee table. INSERT MODE: Will create new employee.",
                        tempPayrollIdFromFrontend);
                finalTempPayrollId = tempPayrollIdFromFrontend;
            } else if (!foundInSkillTest && foundInEmployee) {
                existingEmployee = existingEmployeeOpt.get();
                isUpdate = true;
                logger.info(
                        "✅ tempPayrollId '{}' found in Employee table (emp_id: {}) but NOT in SkillTestDetails. UPDATE MODE: Will update existing employee and email if provided.",
                        tempPayrollIdFromFrontend, existingEmployee.getEmp_id());
                finalTempPayrollId = tempPayrollIdFromFrontend;
            } else {
                throw new ResourceNotFoundException(
                        "tempPayrollId '" + tempPayrollIdFromFrontend +
                                "' not found in SkillTestDetails or Employee table. Please provide a valid tempPayrollId.");
            }
        } else {
            logger.info(
                    "tempPayrollId NOT provided from frontend. Checking aadhar OR phone in SkillTestDetails, Employee, and EmpDetails tables...");

            Optional<com.employee.entity.SkillTestDetails> existingByAadhaarInSkillTest = skillTestDetailsRepository
                    .findActiveByAadhaarNo(aadharNum);

            Optional<EmpDetails> existingByAadhaarInEmployee = empDetailsRepository.findByAdhaar_no(aadharNum);

            Optional<com.employee.entity.SkillTestDetails> existingByPhoneInSkillTest = skillTestDetailsRepository
                    .findActiveByContactNumber(phoneNumber);

            Optional<Employee> existingByPhoneInEmployee = employeeRepository.findByPrimary_mobile_no(phoneNumber);

            if (existingByAadhaarInSkillTest.isPresent()) {
                String existingTempPayrollId = existingByAadhaarInSkillTest.get().getTempPayrollId();
                Long existingPhone = existingByAadhaarInSkillTest.get().getContact_number();
                throw new ResourceNotFoundException(
                        "Employee with Aadhaar number '" + aadharNum +
                                "' already exists in SkillTestDetails table (active) with tempPayrollId: '" +
                                existingTempPayrollId +
                                "' and phone number: '" + existingPhone +
                                "'. Cannot generate new tempPayrollId. Please use the existing tempPayrollId from SkillTestDetails.");
            }

            if (existingByAadhaarInEmployee.isPresent()) {
                EmpDetails empDetails = existingByAadhaarInEmployee.get();
                Employee existingEmp = empDetails.getEmployee_id();
                if (existingEmp != null) {
                    // Only prevent duplicate if employee is ACTIVE
                    if (existingEmp.getIs_active() == 1) {
                        String existingTempPayrollId = existingEmp.getTempPayrollId();
                        int existingEmpId = existingEmp.getEmp_id();
                        throw new ResourceNotFoundException(
                                "Employee with Aadhaar number '" + aadharNum +
                                        "' already exists as ACTIVE employee (emp_id: " + existingEmpId +
                                        ") with tempPayrollId: '" + existingTempPayrollId +
                                        "'. Cannot generate new tempPayrollId. Please use the existing tempPayrollId.");
                    } else {
                        // Employee is inactive - allow creating new ID (person is returning after being
                        // inactive)
                        logger.info(
                                "Employee with Aadhaar number '{}' exists but is INACTIVE (emp_id: {}). Allowing new tempPayrollId generation.",
                                aadharNum, existingEmp.getEmp_id());
                    }
                }
            }

            if (existingByPhoneInSkillTest.isPresent()) {
                String existingTempPayrollId = existingByPhoneInSkillTest.get().getTempPayrollId();
                Long existingAadhaar = existingByPhoneInSkillTest.get().getAadhaar_no();
                throw new ResourceNotFoundException(
                        "Employee with phone number '" + phoneNumber +
                                "' already exists in SkillTestDetails table (active) with tempPayrollId: '" +
                                existingTempPayrollId +
                                "' and Aadhaar number: '" + existingAadhaar +
                                "'. Cannot generate new tempPayrollId. Please use the existing tempPayrollId from SkillTestDetails.");
            }

            if (existingByPhoneInEmployee.isPresent()) {
                Employee existingEmp = existingByPhoneInEmployee.get();
                // Only prevent duplicate if employee is ACTIVE
                if (existingEmp.getIs_active() == 1) {
                    String existingTempPayrollId = existingEmp.getTempPayrollId();
                    int existingEmpId = existingEmp.getEmp_id();
                    throw new ResourceNotFoundException(
                            "Employee with phone number '" + phoneNumber +
                                    "' already exists as ACTIVE employee (emp_id: " + existingEmpId +
                                    ") with tempPayrollId: '" + existingTempPayrollId +
                                    "'. Cannot generate new tempPayrollId. Please use the existing tempPayrollId.");
                } else {
                    // Employee is inactive - allow creating new ID (person is returning after being
                    // inactive)
                    logger.info(
                            "Employee with phone number '{}' exists but is INACTIVE (emp_id: {}). Allowing new tempPayrollId generation.",
                            phoneNumber, existingEmp.getEmp_id());
                }
            }

            logger.info(
                    "Employee NOT found in SkillTestDetails, Employee, or EmpDetails tables. Generating new tempPayrollId...");

            String maxInSkillTest = skillTestDetailsRepository.findMaxTempPayrollIdByKey(baseKey + "%");
            String maxInEmployee = employeeRepository.findMaxTempPayrollIdByKey(baseKey + "%");

            logger.info("Max tempPayrollId in SkillTestDetails: {}", maxInSkillTest);
            logger.info("Max tempPayrollId in Employee: {}", maxInEmployee);

            int maxValue = 0;

            if (maxInSkillTest != null) {
                try {
                    String numberPart = maxInSkillTest.substring(baseKey.length());
                    int value = Integer.parseInt(numberPart);
                    if (value > maxValue) {
                        maxValue = value;
                    }
                } catch (NumberFormatException e) {
                    logger.warn("Could not parse number part from SkillTestDetails: {}", maxInSkillTest);
                }
            }

            if (maxInEmployee != null) {
                try {
                    String numberPart = maxInEmployee.substring(baseKey.length());
                    int value = Integer.parseInt(numberPart);
                    if (value > maxValue) {
                        maxValue = value;
                    }
                } catch (NumberFormatException e) {
                    logger.warn("Could not parse number part from Employee: {}", maxInEmployee);
                }
            }

            int nextValue = maxValue + 1;

            // QA Server Sequence Start Logic: if on QA (DB URL contains '20.21'),
            // ensure temp IDs start from 10001 (same logic as SkillTestDetailsService)
            if (dbUrl != null && dbUrl.contains("20.21")) {
                if (nextValue < 10001) {
                    nextValue = 10001;
                }
            }

            String paddedValue = String.format("%04d", nextValue);
            finalTempPayrollId = baseKey + paddedValue;

            logger.info("Generated new tempPayrollId: {} (next value: {})", finalTempPayrollId, nextValue);
        }

        logger.info("✅ All validations passed. Proceeding with entity preparation and database save...");

        Employee employee = null;

        if (isUpdate) {
            if (existingEmployee == null) {
                throw new ResourceNotFoundException("Updating employee but existingEmployee is null");
            }
            employee = existingEmployee;
            logger.info("🔄 UPDATE MODE: Updating existing employee (emp_id: {})", employee.getEmp_id());

            entityPreparationService.updateEmployeeEntity(employee, basicInfo);

            employee.setUpdated_by(hrEmployeeId);
            employee.setUpdated_date(LocalDateTime.now());
        } else {
            employee = entityPreparationService.prepareEmployeeEntity(basicInfo);

            EmployeeCheckListStatus incompletedStatus = employeeCheckListStatusRepository
                    .findByCheck_app_status_name("Incompleted")
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "EmployeeCheckListStatus with name 'Incompleted' not found"));
            employee.setEmp_check_list_status_id(incompletedStatus);
            logger.info(
                    "➕ INSERT MODE: Creating new employee with temp_payroll_id: {} (app status set to 'Incompleted')",
                    finalTempPayrollId);
        }

        employee.setTempPayrollId(finalTempPayrollId);

        if (isUpdate) {
            employee.setUpdated_by(hrEmployeeId);
            employee.setUpdated_date(LocalDateTime.now());
        }

        // Prepare EmpDetails and EmpPfDetails entities in memory FIRST (before saving
        // employee)
        Integer createdBy = basicInfo.getCreatedBy();
        EmpDetails empDetails = entityPreparationService.prepareEmpDetailsEntity(basicInfo, null, employee, createdBy);
        EmpPfDetails empPfDetails = entityPreparationService.prepareEmpPfDetailsEntity(basicInfo, employee, createdBy);

        // Validate ALL entities BEFORE saving to prevent emp_id sequence consumption on
        // failure
        employeeValidationService.validatePreparedEntities(employee, empDetails, empPfDetails);
        employeeValidationService.validateEntityConstraints(employee, empDetails, empPfDetails);

        // Save to database ONLY after all validations pass
        employee = employeeRepository.save(employee);

        Integer employeeId = employee.getEmp_id();

        if (isUpdate) {
            Optional<EmpDetails> existingDetails = empDetailsRepository.findByEmployeeId(employeeId);

            if (existingDetails.isPresent()) {
                EmpDetails existing = existingDetails.get();

                entityPreparationService.updateEmpDetailsFieldsExceptEmail(existing, empDetails);
                existing.setUpdated_by(hrEmployeeId);
                existing.setUpdated_date(LocalDateTime.now());
                empDetailsRepository.save(existing);
                logger.info("Updated existing EmpDetails for employee (emp_id: {}) - email excluded from update",
                        employeeId);
            } else {
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

                        entityPreparationService.updateEmpDetailsFieldsExceptEmail(existing, empDetails);
                        existing.setEmployee_id(employee);
                        existing.setUpdated_by(hrEmployeeId);
                        existing.setUpdated_date(LocalDateTime.now());
                        empDetailsRepository.save(existing);
                        logger.info("Updated existing EmpDetails found by email for employee (emp_id: {}), email: {}",
                                employeeId, empDetails.getPersonal_email());
                    } else {
                        empDetailsRepository.save(empDetails);
                        logger.info("Created new EmpDetails for employee (emp_id: {})", employeeId);
                    }
                } else {
                    empDetailsRepository.save(empDetails);
                    logger.info("Created new EmpDetails for employee (emp_id: {})", employeeId);
                }
            }
        } else {
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

                    entityPreparationService.updateEmpDetailsFieldsExceptEmail(existing, empDetails);
                    existing.setEmployee_id(employee);
                    existing.setUpdated_by(hrEmployeeId);
                    existing.setUpdated_date(LocalDateTime.now());
                    empDetailsRepository.save(existing);
                    logger.info(
                            "Updated existing EmpDetails found by email during INSERT for employee (emp_id: {}), email: {}",
                            employeeId, empDetails.getPersonal_email());
                } else {
                    empDetailsRepository.save(empDetails);
                    logger.info("Created new EmpDetails for employee (emp_id: {})", employeeId);
                }
            } else {
                empDetailsRepository.save(empDetails);
                logger.info("Created new EmpDetails for employee (emp_id: {})", employeeId);
            }
        }

        if (empPfDetails != null) {
            empPfDetails.setEmployee_id(employee);

            if (isUpdate) {
                Optional<EmpPfDetails> existingPfDetails = empPfDetailsRepository.findByEmployeeId(employeeId);

                if (existingPfDetails.isPresent()) {
                    EmpPfDetails existing = existingPfDetails.get();
                    existing.setPre_esi_no(empPfDetails.getPre_esi_no());
                    existing.setIs_active(empPfDetails.getIs_active());
                    existing.setUpdated_by(hrEmployeeId);
                    existing.setUpdated_date(LocalDateTime.now());
                    empPfDetailsRepository.save(existing);
                    logger.info("Updated existing EmpPfDetails for employee (emp_id: {})", employeeId);
                } else {
                    empPfDetailsRepository.save(empPfDetails);
                    logger.info("Created new EmpPfDetails for employee (emp_id: {})", employeeId);
                }
            } else {
                empPfDetailsRepository.save(empPfDetails);
                logger.info("Created new EmpPfDetails for employee (emp_id: {})", employeeId);
            }
        }

        if (isUpdate) {
            logger.info(
                    "✅ Successfully UPDATED employee (emp_id: {}) with tempPayrollId '{}'. Updated by HR Employee (emp_id: {})",
                    employeeId, finalTempPayrollId, hrEmployeeId);
        } else {
            logger.info(
                    "✅ Successfully created NEW employee (emp_id: {}) with tempPayrollId '{}'. Created by HR Employee (emp_id: {})",
                    employeeId, finalTempPayrollId, hrEmployeeId);
        }

        TempPayrollIdResponseDTO response = new TempPayrollIdResponseDTO();
        response.setTempPayrollId(finalTempPayrollId);
        response.setEmployeeId(employeeId);
        response.setMessage(isUpdate ? "Employee updated successfully with Temp Payroll ID"
                : "New employee created successfully with Temp Payroll ID");
        response.setBasicInfo(basicInfo);

        return response;
    }
}