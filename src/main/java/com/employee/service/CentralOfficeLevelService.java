
package com.employee.service;

import java.text.SimpleDateFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import com.employee.dto.CentralOfficeChecklistDTO;
import com.employee.dto.IncompletedStatusDTO;
import com.employee.dto.RejectBackToDODTO;
import com.employee.dto.RejectEmployeeByRoleDTO;
import com.employee.dto.RejectEmployeeDTO;
import com.employee.entity.Campus;
import com.employee.entity.City;
import com.employee.entity.Employee;
import com.employee.entity.EmployeeCheckListStatus;
import com.employee.entity.EmployeeTypeHiring;
import com.employee.entity.Organization;
import com.employee.exception.ResourceNotFoundException;
import com.employee.repository.CampusRepository;
import com.employee.repository.CityRepository;
import com.employee.repository.EmpAppCheckListDetlRepository;
import com.employee.repository.EmpSalaryInfoRepository;
import com.employee.repository.EmployeeCheckListStatusRepository;
import com.employee.repository.EmployeeRepository;
import com.employee.repository.OrganizationRepository;

/**
 * Service for Central Office Level operations
 * Handles employee rejection and sending back to DO (Demand Officer)
 */
@Service
@Transactional
public class CentralOfficeLevelService {

    private static final Logger logger = LoggerFactory.getLogger(CentralOfficeLevelService.class);

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private EmpAppCheckListDetlRepository empAppCheckListDetlRepository;

    @Autowired
    private EmployeeCheckListStatusRepository employeeCheckListStatusRepository;

    @Autowired
    private CampusRepository campusrepository;

    @Autowired
    private CityRepository cityrepository;

    @Autowired
    private OrganizationRepository organizationrepository;

    @Autowired
    private EmpSalaryInfoRepository empSalaryInfoRepository;

    @Autowired
    private org.springframework.transaction.PlatformTransactionManager transactionManager;

    public CentralOfficeChecklistDTO updateChecklist(CentralOfficeChecklistDTO checklistDTO) {
        // Validation: Check if tempPayrollId is provided
        if (checklistDTO.getTempPayrollId() == null || checklistDTO.getTempPayrollId().trim().isEmpty()) {
            System.out.println("[ERROR] Update failed: tempPayrollId is missing.");
            throw new ResourceNotFoundException("tempPayrollId is required. Please provide a valid temp_payroll_id.");
        }

        // Validation: Check if checkListIds is provided
        if (checklistDTO.getCheckListIds() == null || checklistDTO.getCheckListIds().trim().isEmpty()) {
            throw new ResourceNotFoundException(
                    "checkListIds is required. Please provide checklist IDs (comma-separated string).");
        }

        logger.info("Updating checklist for temp_payroll_id: {}", checklistDTO.getTempPayrollId());

        // Step 1: Validate tempPayrollId exists in Employee table
        validateTempPayrollId(checklistDTO.getTempPayrollId());

        // Step 2: Find employee by temp_payroll_id
        Employee employee = employeeRepository.findByTempPayrollId(checklistDTO.getTempPayrollId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Employee not found with temp_payroll_id: " + checklistDTO.getTempPayrollId()));

        Integer empId = employee.getEmp_id();
        logger.info("Found employee with emp_id: {} for temp_payroll_id: {}", empId, checklistDTO.getTempPayrollId());

        // Step 3: Always update status to "Confirm" (ID: 4) when updating checklist
        Integer oldStatusId = employee.getEmp_check_list_status_id() != null
                ? employee.getEmp_check_list_status_id().getEmp_app_status_id()
                : null;
        String oldStatusName = employee.getEmp_check_list_status_id() != null
                ? employee.getEmp_check_list_status_id().getCheck_app_status_name()
                : "null";

        EmployeeCheckListStatus confirmStatus = employeeCheckListStatusRepository
                .findByCheck_app_status_name("Confirm").orElseThrow(() -> new ResourceNotFoundException(
                        "EmployeeCheckListStatus with name 'Confirm' not found"));

        employee.setEmp_check_list_status_id(confirmStatus);
        logger.info("Updated employee (emp_id: {}) status from '{}' (ID: {}) to 'Confirm' (ID: {})",
                empId, oldStatusName, oldStatusId, confirmStatus.getEmp_app_status_id());

        // Clear remarks when confirming
        employee.setRemarks(null);
        logger.info("Cleared remarks for employee (emp_id: {}) when confirming", empId);

        // Step 4: Validate checklist IDs before saving
        validateCheckListIds(checklistDTO.getCheckListIds());

        // Step 5: Update emp_app_check_list_detl_id in Employee table
        employee.setEmp_app_check_list_detl_id(checklistDTO.getCheckListIds());

        // Step 6: Update notice_period AND Generate Permanent ID (if provided)
        if (checklistDTO.getNoticePeriod() != null && !checklistDTO.getNoticePeriod().trim().isEmpty()) {
            employee.setNotice_period(checklistDTO.getNoticePeriod().trim());
            logger.info("Updated notice period for employee (emp_id: {}): {}", empId, checklistDTO.getNoticePeriod());

            // Check if a permanent ID already exists. If not, generate one.
            if (employee.getPayRollId() == null || employee.getPayRollId().trim().isEmpty()) {

                // --- NOTE: We removed the try-catch block here to let errors show ---

                // This method now saves to BOTH tables
                String permanentId = generateAndSetPermanentId(employee);
                System.out.println("[SERVICE] Permanent ID generated: " + permanentId);
                logger.info("Successfully generated and set permanent payroll ID: {} for emp_id: {}", permanentId,
                        empId);

                // 1. Set Username (it's the permanent payroll ID)
                employee.setUser_name(permanentId);

                // 2. Get data for password
                String firstName = employee.getFirst_name();
                java.util.Date dateOfJoin = employee.getDate_of_join();

                // 3. Safety checks to prevent errors
                if (firstName == null || firstName.length() < 3) {
                    logger.error(
                            "CRITICAL: Cannot generate password for emp_id: {}. First name is missing or shorter than 3 letters.",
                            empId);
                    throw new RuntimeException(
                            "Cannot generate password. Employee first name is missing or < 3 letters.");
                }
                if (dateOfJoin == null) {
                    logger.error("CRITICAL: Cannot generate password for emp_id: {}. Date of join is missing.", empId);
                    throw new RuntimeException("Cannot generate password. Employee date of join is missing.");
                }

                // 4. Create password (First 3 letters of first_name + date_of_join)
                String namePart = firstName.substring(0, 3);
                String finalNamePart = namePart.substring(0, 1).toUpperCase() + namePart.substring(1);

                SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");
                String dateOfJoinString = sdf.format(dateOfJoin);

                String plainTextPassword = finalNamePart + dateOfJoinString;

                // 5. Set PLAIN TEXT Password
                employee.setPassword(plainTextPassword);

                logger.info("Successfully generated default username ({}) and PLAIN TEXT password for emp_id: {}",
                        permanentId, empId);

            } else {
                System.out.println(
                        "[WARN] Skipping generation. Employee already has permanent ID: " + employee.getPayRollId());
                logger.warn("Employee (emp_id: {}) already has a permanent payroll ID ({}). Skipping generation.",
                        empId, employee.getPayRollId());
            }

        } else {
            logger.warn("Notice period is null or empty. Permanent ID will not be generated on this update.");
        }

        // Step 7: Save all changes to the Employee table
        employee.setUpdated_by(checklistDTO.getUpdatedBy());
        employee.setUpdated_date(java.time.LocalDateTime.now());
        employeeRepository.save(employee);

        logger.info("Successfully updated checklist for employee (emp_id: {}, temp_payroll_id: '{}')", empId,
                checklistDTO.getTempPayrollId());

        return checklistDTO;
    }

    /**
     * Validate checklist IDs
     */
    private void validateCheckListIds(String checkListIds) {
        if (checkListIds == null || checkListIds.trim().isEmpty()) {
            return;
        }

        String[] idArray = checkListIds.split(",");

        for (String idStr : idArray) {
            idStr = idStr.trim();

            if (idStr.isEmpty()) {
                continue;
            }

            try {
                Integer checklistId = Integer.parseInt(idStr);

                // Using exists check to avoid loading entity (prevents check_list_id column
                // error)
                if (!empAppCheckListDetlRepository.existsByIdAndIsActive(checklistId, 1)) {
                    throw new ResourceNotFoundException("Checklist ID " + checklistId
                            + " not found or inactive. Provided IDs: " + checkListIds);
                }

                logger.debug("Validated checklist ID: {} exists and is active", checklistId);

            } catch (NumberFormatException e) {
                throw new ResourceNotFoundException("Invalid checklist ID format: '" + idStr
                        + "'. Provided IDs: " + checkListIds);
            }
        }

        logger.info("✅ All checklist IDs validated successfully: {}", checkListIds);
    }

    /**
     * Validate tempPayrollId
     */
    private void validateTempPayrollId(String tempPayrollId) {
        if (tempPayrollId == null || tempPayrollId.trim().isEmpty()) {
            throw new ResourceNotFoundException("tempPayrollId is required.");
        }

        employeeRepository.findByTempPayrollId(tempPayrollId.trim())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Employee not found with temp_payroll_id: " + tempPayrollId));

        logger.info("✅ Validated temp_payroll_id exists: {}", tempPayrollId);
    }

    /**
     * Generates a permanent payroll ID and saves it to BOTH the Employee
     * and the EmpSalaryInfo tables.
     *
     * @param employee The employee object to update (must be fully loaded).
     * @return The generated permanent payroll ID.
     * @throws ResourceNotFoundException if related Campus, City, Organization, or
     *                                   EmpSalaryInfo is not found.
     */
    private String generateAndSetPermanentId(Employee employee) {
        logger.info("Attempting to generate permanent payroll ID for emp_id: {}", employee.getEmp_id());

        // --- START OF FIX ---
        // 1. Get Campus object directly from the employee
        // We no longer call employee.getCmps_id()
        Campus campusdata = employee.getCampus_id();

        // 1a. Add a null check
        if (campusdata == null) {
            logger.error("CRITICAL: Cannot generate permanent ID for emp_id: {}. Campus ID (campus_id) is missing.",
                    employee.getEmp_id());
            throw new ResourceNotFoundException(
                    "Cannot generate permanent ID: The Campus ID (campus_id) is missing for employee: "
                            + employee.getEmp_id());
        }
        // --- END OF FIX ---

        // --- USE ORGANIZATION MASTER AS A BASE FOR ID GENERATION ---

        // 1. Get Organization (Master Table)
        Organization org_data = employee.getOrg_id();
        if (org_data == null) {
            logger.error("CRITICAL: Organization missing for emp_id: {}", employee.getEmp_id());
            throw new ResourceNotFoundException("Organization missing for employee: " + employee.getEmp_id());
        }

        // 2. Determine Prefix based on Hiring Type
        EmployeeTypeHiring hiringType = employee.getEmployee_type_hiring_id();
        String prefix;
        boolean isConsultancyOrAgency = hiringType != null &&
                ("CONSULTANCY".equalsIgnoreCase(hiringType.getEmp_type_hiring_name()) ||
                        "AGENCY".equalsIgnoreCase(hiringType.getEmp_type_hiring_name()));

        if (isConsultancyOrAgency) {
            long pay_role_code = org_data.getPayrollCode();
            prefix = "CON" + pay_role_code;
            logger.info("Using '{}' prefix for hiring type: {}", prefix, hiringType.getEmp_type_hiring_name());
        } else {
            // Default logic: City Code + Organization Code
            if (campusdata.getCity() == null) {
                logger.error("CRITICAL: City missing for campus_id: {}", campusdata.getCampusId());
                throw new ResourceNotFoundException("City missing for campus: " + campusdata.getCampusId());
            }
            int city_id = campusdata.getCity().getCityId();
            City citydata = cityrepository.findById(city_id)
                    .orElseThrow(() -> new ResourceNotFoundException("City not found for city_id: " + city_id));

            String payrole_city_code = citydata.getPayroll_city_code();
            long pay_role_code = org_data.getPayrollCode();
            prefix = (payrole_city_code != null ? payrole_city_code : "") + pay_role_code;
            logger.info("Using standard prefix '{}' (City: {}, Org: {})", prefix, payrole_city_code, pay_role_code);
        }

        // 3. Get Base Number from Organization (Master) based on Prefix
        Long masterBase;
        if (isConsultancyOrAgency) {
            masterBase = org_data.getConPayrollMaxNo();
            logger.info("Using 'CON' master base: {}", masterBase);
        } else {
            masterBase = org_data.getPayrollMaxNo();
            logger.info("Using standard master base: {}", masterBase);
        }

        if (masterBase == null)
            masterBase = 0L;

        // 4. Find Max Number from Employee Table (Actual assigned)
        long currentEmployeeMax = 0;
        String maxExistingId = employeeRepository.findMaxPayrollIdByKey(prefix + "%");
        if (maxExistingId != null && maxExistingId.startsWith(prefix)) {
            try {
                String numberPart = maxExistingId.substring(prefix.length());
                currentEmployeeMax = Long.parseLong(numberPart);
            } catch (Exception e) {
                logger.warn("Could not parse number from existing ID: {}", maxExistingId);
            }
        }

        // 5. Next Number is MAX(MasterBase, CurrentEmployeeMax) + 1
        long nextNumber = Math.max(masterBase, currentEmployeeMax) + 1;

        logger.info("Generated next number: {} (Master Base: {}, Employee Max: {})", nextNumber, masterBase,
                currentEmployeeMax);
        System.out.println("[SERVICE] Generating Payroll ID. Base: " + masterBase + ", EmpMax: " + currentEmployeeMax
                + ", Next: " + nextNumber);

        // 6. Generate final ID
        String permanentId = prefix + nextNumber;

        // --- SAVE TO EMPLOYEE TABLE ONLY (Do NOT update Organization Master) ---
        employee.setPayRollId(permanentId);
        logger.info("Set PayRollId on Employee entity for emp_id: {}", employee.getEmp_id());

        // --- SAVE TO EMP_SALARY_INFO TABLE ---
        updateEmpSalaryInfoPayrollId(employee.getEmp_id(), employee.getTempPayrollId(), permanentId);

        logger.info("Generated permanent payroll ID: {} for emp_id: {} (Organization table not modified)", permanentId,
                employee.getEmp_id());

        return permanentId;
    }

    private void updateEmpSalaryInfoPayrollId(Integer empId, String tempId, String payrollId) {
        // Step 1: Diagnostics - Check why record might be missing
        long activeCount = empSalaryInfoRepository.countActiveByEmpId(empId);
        long totalCount = empSalaryInfoRepository.countAllByEmpId(empId);
        long activeTempCount = (tempId != null) ? empSalaryInfoRepository.countActiveByTempId(tempId) : 0;
        long totalTempCount = (tempId != null) ? empSalaryInfoRepository.countAllByTempId(tempId) : 0;

        System.out.println(
                "[DIAGNOSTIC] Records for emp_id " + empId + ": Active=" + activeCount + ", Total=" + totalCount);
        if (tempId != null) {
            System.out.println("[DIAGNOSTIC] Records for temp_id " + tempId + ": Active=" + activeTempCount + ", Total="
                    + totalTempCount);
        }

        if (activeCount == 0 && activeTempCount == 0) {
            System.out.println("[DIAGNOSTIC] ERROR: No active salary record found by emp_id OR temp_id.");
            logger.warn("No active salary record found for emp_id: {} or temp_id: {}. Skipping payroll_id patch.",
                    empId, tempId);
            return;
        }

        System.out.println("[DIAGNOSTIC] Attempting update to " + payrollId);

        TransactionTemplate txNew = new TransactionTemplate(transactionManager);
        txNew.setPropagationBehavior(org.springframework.transaction.TransactionDefinition.PROPAGATION_REQUIRES_NEW);

        try {
            // ATTEMPT 1: Try the update in a fresh transaction
            txNew.executeWithoutResult(status -> {
                int updatedRows = 0;
                if (activeCount > 0) {
                    updatedRows = empSalaryInfoRepository.updatePayrollIdOnly(empId, payrollId);
                } else if (activeTempCount > 0) {
                    updatedRows = empSalaryInfoRepository.updatePayrollIdByTempId(tempId, payrollId);
                }

                if (updatedRows > 0) {
                    System.out.println("[DIAGNOSTIC] SUCCESS: Updated via ATTEMPT 1.");
                } else {
                    throw new RuntimeException("Zero rows updated in ATTEMPT 1");
                }
            });
        } catch (Exception e) {
            String msg = e.getMessage() != null ? e.getMessage().toLowerCase() : "";

            if (msg.contains("invalid byte sequence") || msg.contains("tfn_encrypt_sal")) {
                System.out.println("[DIAGNOSTIC] Trigger conflict detected. Attempting repair...");

                try {
                    // ATTEMPT 2: Trigger Optimization
                    txNew.executeWithoutResult(status -> {
                        empSalaryInfoRepository.optimizeTriggerScope();
                    });

                    // ATTEMPT 3: Retry
                    txNew.executeWithoutResult(status -> {
                        int retryRows = 0;
                        if (activeCount > 0) {
                            retryRows = empSalaryInfoRepository.updatePayrollIdOnly(empId, payrollId);
                        } else if (activeTempCount > 0) {
                            retryRows = empSalaryInfoRepository.updatePayrollIdByTempId(tempId, payrollId);
                        }

                        if (retryRows > 0) {
                            System.out.println("[DIAGNOSTIC] SUCCESS: Update successful after trigger optimization.");
                        }
                    });
                } catch (Exception fatal) {
                    if (fatal.getMessage() != null && fatal.getMessage().toLowerCase().contains("permission denied")) {
                        System.out.println("[DIAGNOSTIC] ERROR: Permission denied for trigger fix.");
                    } else {
                        System.out.println("[DIAGNOSTIC] ERROR: Auto-fix failed: " + fatal.getMessage());
                    }
                }
            } else {
                System.out.println("[DIAGNOSTIC] ABORTED: " + e.getMessage());
            }
        }
    }

    /**
     * Reject and send employee back to DO (Demand Officer)
     * This method is called when Central Office rejects an employee application
     *
     * Flow:
     * 1. Validate temp_payroll_id exists in Employee table
     * 2. Find employee by temp_payroll_id
     * 3. Validate that current status is "Pending at CO" (required)
     * 4. Update status to "Back to DO"
     * 5. Update remarks (if remarks already exist, update them; if not, set new
     * remarks)
     *
     * @param rejectDTO DTO containing tempPayrollId and remarks
     * @return Updated DTO with the saved data
     * @throws ResourceNotFoundException if employee not found or status is not
     *                                   "Pending at CO"
     */
    public RejectBackToDODTO rejectBackToDO(RejectBackToDODTO rejectDTO) {
        // Validation: Check if tempPayrollId is provided
        if (rejectDTO.getTempPayrollId() == null || rejectDTO.getTempPayrollId().trim().isEmpty()) {
            throw new ResourceNotFoundException("tempPayrollId is required. Please provide a valid temp_payroll_id.");
        }

        // Validation: Check if remarks is provided
        if (rejectDTO.getRemarks() == null || rejectDTO.getRemarks().trim().isEmpty()) {
            throw new ResourceNotFoundException(
                    "remarks is required. Please provide a reason for rejecting and sending back to DO.");
        }

        // Validation: Check remarks length (max 250 characters)
        if (rejectDTO.getRemarks().length() > 250) {
            throw new IllegalArgumentException(
                    "remarks cannot exceed 250 characters. Current length: " + rejectDTO.getRemarks().length());
        }

        logger.info("Rejecting employee and sending back to DO - temp_payroll_id: {}, remarks: {}",
                rejectDTO.getTempPayrollId(), rejectDTO.getRemarks());

        // Step 1: Validate tempPayrollId exists in Employee table
        validateTempPayrollId(rejectDTO.getTempPayrollId());

        // Step 2: Find employee by temp_payroll_id
        Employee employee = employeeRepository.findByTempPayrollId(rejectDTO.getTempPayrollId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Employee not found with temp_payroll_id: " + rejectDTO.getTempPayrollId()));

        Integer empId = employee.getEmp_id();
        logger.info("Found employee with emp_id: {} for temp_payroll_id: {}", empId, rejectDTO.getTempPayrollId());

        // Step 3: Validate that current status is "Pending at CO" - this method only
        // works for "Pending at CO" status
        if (employee.getEmp_check_list_status_id() == null) {
            throw new ResourceNotFoundException(
                    "Cannot reject employee: Employee (emp_id: " + empId +
                            ", temp_payroll_id: '" + rejectDTO.getTempPayrollId() +
                            "') does not have a status set. This method only works when employee status is 'Pending at CO'.");
        }

        String currentStatusName = employee.getEmp_check_list_status_id().getCheck_app_status_name();
        if (!"Pending at CO".equals(currentStatusName)) {
            throw new ResourceNotFoundException(
                    "Cannot reject employee: Current employee status is '" + currentStatusName +
                            "' (emp_id: " + empId + ", temp_payroll_id: '" + rejectDTO.getTempPayrollId() +
                            "'). This method only works when employee status is 'Pending at CO'.");
        }

        logger.info("Employee (emp_id: {}) current status is 'Pending at CO', proceeding with reject back to DO",
                empId);

        // Step 4: Update status to "Back to DO"
        EmployeeCheckListStatus backToDOStatus = employeeCheckListStatusRepository
                .findByCheck_app_status_name("Back to DO")
                .orElseThrow(() -> new ResourceNotFoundException(
                        "EmployeeCheckListStatus with name 'Back to DO' not found"));
        employee.setEmp_check_list_status_id(backToDOStatus);
        logger.info("Updated employee (emp_id: {}) status from 'Pending at CO' to 'Back to DO' (ID: {})",
                empId, backToDOStatus.getEmp_app_status_id());

        // Step 5: Update remarks (if remarks already exist, update them; if not, set
        // new remarks)
        String existingRemarks = employee.getRemarks();
        if (existingRemarks != null && !existingRemarks.trim().isEmpty()) {
            // Update existing remarks (append or replace based on business logic - here
            // we're replacing)
            employee.setRemarks(rejectDTO.getRemarks().trim());
            logger.info("Updated existing remarks for employee (emp_id: {}). Previous remarks: '{}', New remarks: '{}'",
                    empId, existingRemarks, rejectDTO.getRemarks());
        } else {
            // Set new remarks
            employee.setRemarks(rejectDTO.getRemarks().trim());
            logger.info("Set new remarks for employee (emp_id: {}): {}", empId, rejectDTO.getRemarks());
        }

        // Save employee updates (status and remarks)
        employee.setUpdated_by(rejectDTO.getUpdatedBy());
        employee.setUpdated_date(java.time.LocalDateTime.now());
        employeeRepository.save(employee);

        logger.info(
                "Successfully rejected employee (emp_id: {}, temp_payroll_id: '{}') and sent back to DO with remarks",
                empId, rejectDTO.getTempPayrollId());

        // Return the DTO with saved data
        return rejectDTO;
    }

    /**
     * Reject an employee by DO and set status to "Rejected by DO" (ID 5)
     *
     * @param rejectDTO DTO containing tempPayrollId and remarks
     * @return Updated RejectEmployeeDTO
     */
    public RejectEmployeeDTO rejectByDO(RejectEmployeeDTO rejectDTO) {
        validateRejectionRequest(rejectDTO);

        Employee employee = getEmployeeByTempId(rejectDTO.getTempPayrollId());
        validateNotConfirmed(employee);

        // Validation: Only "Pending at DO" can be rejected by DO
        if (employee.getEmp_check_list_status_id() == null ||
                !"Pending at DO".equals(employee.getEmp_check_list_status_id().getCheck_app_status_name())) {

            String currentStatus = (employee.getEmp_check_list_status_id() != null)
                    ? employee.getEmp_check_list_status_id().getCheck_app_status_name()
                    : "null";

            throw new ResourceNotFoundException("Cannot reject employee by DO. Current status is '" + currentStatus +
                    "'. Only employees with 'Pending at DO' status can be rejected by the Divisional Office.");
        }

        updateEmployeeStatusAndRemarks(employee, "Rejected by DO", rejectDTO.getRemarks(), rejectDTO.getUpdatedBy());
        return rejectDTO;
    }

    /**
     * Reject an employee by CO and set status to "Rejected by CO" (ID 11)
     *
     * @param rejectDTO DTO containing tempPayrollId and remarks
     * @return Updated RejectEmployeeDTO
     */
    public RejectEmployeeDTO rejectByCO(RejectEmployeeDTO rejectDTO) {
        validateRejectionRequest(rejectDTO);

        Employee employee = getEmployeeByTempId(rejectDTO.getTempPayrollId());
        validateNotConfirmed(employee);

        // Validation: Only "Pending at CO" can be rejected by CO
        if (employee.getEmp_check_list_status_id() == null ||
                !"Pending at CO".equals(employee.getEmp_check_list_status_id().getCheck_app_status_name())) {

            String currentStatus = (employee.getEmp_check_list_status_id() != null)
                    ? employee.getEmp_check_list_status_id().getCheck_app_status_name()
                    : "null";

            throw new ResourceNotFoundException("Cannot reject employee by CO. Current status is '" + currentStatus +
                    "'. Only employees with 'Pending at CO' status can be rejected by the Central Office.");
        }

        updateEmployeeStatusAndRemarks(employee, "Rejected by CO", rejectDTO.getRemarks(), rejectDTO.getUpdatedBy());
        return rejectDTO;
    }

    private void validateRejectionRequest(RejectEmployeeDTO rejectDTO) {
        if (rejectDTO.getTempPayrollId() == null || rejectDTO.getTempPayrollId().trim().isEmpty()) {
            throw new ResourceNotFoundException("tempPayrollId is required.");
        }
        if (rejectDTO.getRemarks() == null || rejectDTO.getRemarks().trim().isEmpty()) {
            throw new ResourceNotFoundException("remarks is required.");
        }
    }

    private Employee getEmployeeByTempId(String tempPayrollId) {
        return employeeRepository.findByTempPayrollId(tempPayrollId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Employee not found with temp_payroll_id: " + tempPayrollId));
    }

    private void validateNotConfirmed(Employee employee) {
        if (employee.getEmp_check_list_status_id() != null &&
                "Confirm".equals(employee.getEmp_check_list_status_id().getCheck_app_status_name())) {

            throw new ResourceNotFoundException("Cannot reject employee. Employee is already 'Confirm'. " +
                    "Rejection is not allowed for confirmed employees.");
        }
    }

    private void updateEmployeeStatusAndRemarks(Employee employee, String statusName, String remarks,
            Integer updatedBy) {
        EmployeeCheckListStatus status = employeeCheckListStatusRepository.findByCheck_app_status_name(statusName)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "EmployeeCheckListStatus with name '" + statusName + "' not found"));

        employee.setEmp_check_list_status_id(status);
        employee.setRemarks(remarks.trim());
        employee.setUpdated_by(updatedBy);
        employee.setUpdated_date(java.time.LocalDateTime.now());
        employeeRepository.save(employee);

        logger.info("Successfully updated employee (temp_payroll_id: '{}') to status '{}'",
                employee.getTempPayrollId(), statusName);
    }

    /**
     * Change employee status to "Incompleted"
     * This method is called to change status from "Reject" to "Incompleted"
     *
     * @param statusDTO DTO containing tempPayrollId and remarks
     * @return Updated IncompletedStatusDTO
     */
    public IncompletedStatusDTO markAsIncompleted(IncompletedStatusDTO statusDTO) {
        // Validation
        if (statusDTO.getTempPayrollId() == null || statusDTO.getTempPayrollId().trim().isEmpty()) {
            throw new ResourceNotFoundException("tempPayrollId is required.");
        }
        if (statusDTO.getRemarks() == null || statusDTO.getRemarks().trim().isEmpty()) {
            throw new ResourceNotFoundException("remarks is required.");
        }

        logger.info("Changing status to Incompleted - temp_payroll_id: {}, remarks: {}",
                statusDTO.getTempPayrollId(), statusDTO.getRemarks());

        // Find employee
        Employee employee = employeeRepository.findByTempPayrollId(statusDTO.getTempPayrollId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Employee not found with temp_payroll_id: " + statusDTO.getTempPayrollId()));

        // Validation: Check if current status is "Rejected by DO" or "Rejected by CO"
        if (employee.getEmp_check_list_status_id() == null ||
                (!"Rejected by DO".equals(employee.getEmp_check_list_status_id().getCheck_app_status_name()) &&
                        !"Rejected by CO".equals(employee.getEmp_check_list_status_id().getCheck_app_status_name()))) {

            String currentStatus = (employee.getEmp_check_list_status_id() != null)
                    ? employee.getEmp_check_list_status_id().getCheck_app_status_name()
                    : "null";

            throw new ResourceNotFoundException("Cannot mark as Incompleted. Current status is '" + currentStatus +
                    "'. This method only works for employees with 'Rejected by DO' or 'Rejected by CO' status.");
        }

        // Retrieve "Incompleted" status by name
        EmployeeCheckListStatus incompletedStatus = employeeCheckListStatusRepository
                .findByCheck_app_status_name("Incompleted")
                .orElseThrow(() -> new ResourceNotFoundException(
                        "EmployeeCheckListStatus with name 'Incompleted' not found"));

        employee.setEmp_check_list_status_id(incompletedStatus);
        employee.setRemarks(statusDTO.getRemarks().trim());

        // Save
        employee.setUpdated_by(statusDTO.getUpdatedBy());
        employee.setUpdated_date(java.time.LocalDateTime.now());
        employeeRepository.save(employee);

        logger.info("Successfully updated status to 'Incompleted' for temp_payroll_id: '{}'",
                statusDTO.getTempPayrollId());

        return statusDTO;
    }

    /**
     * Reject employee based on role ("DO" or "CO")
     * Delegates to rejectByDO or rejectByCO based on the role provided.
     * 
     * @param dto DTO containing tempPayrollId, remarks, and role
     * @return The same DTO if successful
     */
    public RejectEmployeeByRoleDTO rejectEmployeeByRole(RejectEmployeeByRoleDTO dto) {
        // Validation
        if (dto.getRole() == null || dto.getRole().trim().isEmpty()) {
            throw new ResourceNotFoundException("Role is required. Valid values are 'DO' or 'CO'.");
        }

        String role = dto.getRole().trim().toUpperCase();
        RejectEmployeeDTO rejectDto = new RejectEmployeeDTO(dto.getTempPayrollId(), dto.getRemarks(),
                dto.getUpdatedBy());

        if ("DO".equals(role)) {
            logger.info("Delegating to rejectByDO for temp_payroll_id: {}", dto.getTempPayrollId());
            rejectByDO(rejectDto);
        } else if ("CO".equals(role)) {
            logger.info("Delegating to rejectByCO for temp_payroll_id: {}", dto.getTempPayrollId());
            rejectByCO(rejectDto);
        } else {
            throw new ResourceNotFoundException("Invalid role: '" + dto.getRole() + "'. Valid roles are 'DO' or 'CO'.");
        }

        return dto;
    }
}
