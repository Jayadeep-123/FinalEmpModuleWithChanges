package com.employee.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.employee.dto.AddressInfoDTO;
import com.employee.dto.BasicInfoDTO;
import com.employee.entity.Building;
import com.employee.entity.EmpDetails;
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
import com.employee.repository.EmployeeCheckListStatusRepository;
import com.employee.repository.EmployeeRepository;
import com.employee.repository.EmployeeStatusRepository;
import com.employee.repository.EmployeeTypeHiringRepository;
import com.employee.repository.EmployeeTypeRepository;
import com.employee.repository.GenderRepository;
import com.employee.repository.JoiningAsRepository;
import com.employee.repository.MaritalStatusRepository;
import com.employee.repository.ModeOfHiringRepository;
import com.employee.repository.QualificationRepository;
import com.employee.repository.RelationRepository;
import com.employee.repository.RelegionRepository;
import com.employee.repository.WorkingModeRepository;

/**
 * Service for Employee Entity Preparation operations.
 * Contains entity creation/preparation methods extracted from EmployeeService
 * for better organization.
 */
@Service
public class EmployeeEntityPreparationService {

    @Autowired
    private RelationRepository relationRepository;
    @Autowired
    private GenderRepository genderRepository;
    @Autowired
    private BloodGroupRepository bloodGroupRepository;
    @Autowired
    private QualificationRepository qualificationRepository;
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private CampusRepository campusRepository;
    @Autowired
    private EmployeeTypeRepository employeeTypeRepository;
    @Autowired
    private EmployeeCheckListStatusRepository employeeCheckListStatusRepository;
    @Autowired
    private EmployeeStatusRepository employeeStatusRepository;
    @Autowired
    private CasteRepository casteRepository;
    @Autowired
    private RelegionRepository relegionRepository;
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
    private BuildingRepository buildingRepository;
    @Autowired
    private EmployeeTypeHiringRepository employeeTypeHiringRepository;

    public EmpPfDetails prepareEmpPfDetailsEntity(BasicInfoDTO basicInfo, Employee employee, Integer createdBy) {
        if (basicInfo == null)
            return null;
        if (basicInfo.getPreUanNum() == null && basicInfo.getPreEsiNum() == null) {
            return null;
        }
        EmpPfDetails empPfDetails = new EmpPfDetails();
        empPfDetails.setEmployee_id(employee);
        empPfDetails.setPre_esi_no(basicInfo.getPreEsiNum());
        empPfDetails.setIs_active(1);

        // Set created_by and created_date - required fields (NOT NULL constraint)
        if (createdBy != null && createdBy > 0) {
            empPfDetails.setCreated_by(createdBy);
            empPfDetails.setCreated_date(LocalDateTime.now());
        } else {
            // If createdBy is not provided, use employee's created_by as fallback
            if (employee != null && employee.getCreated_by() != null && employee.getCreated_by() > 0) {
                empPfDetails.setCreated_by(employee.getCreated_by());
                empPfDetails.setCreated_date(LocalDateTime.now());
            } else {
                throw new ResourceNotFoundException(
                        "Created By is required for EmpPfDetails (NOT NULL column). Please provide createdBy in BasicInfoDTO or ensure Employee has created_by set.");
            }
        }

        return empPfDetails;
    }

    public Employee prepareEmployeeEntity(BasicInfoDTO basicInfo) {
        if (basicInfo == null)
            throw new ResourceNotFoundException("Basic Info is required");

        Employee employee = new Employee();
        employee.setFirst_name(basicInfo.getFirstName());
        employee.setLast_name(basicInfo.getLastName());
        employee.setDate_of_join(basicInfo.getDateOfJoin());
        employee.setPrimary_mobile_no(basicInfo.getPrimaryMobileNo());
        employee.setSecondary_mobile_no(basicInfo.getSecondaryMobileNo());
        employee.setEmail(null);
        if (basicInfo.getTotalExperience() != null)
            employee.setTotal_experience(basicInfo.getTotalExperience().doubleValue());
        if (basicInfo.getAge() != null)
            employee.setAge(basicInfo.getAge());
        if (Boolean.TRUE.equals(basicInfo.getSscNotAvailable())) {
            employee.setSsc_no(null);
        } else if (basicInfo.getSscNo() != null) {
            employee.setSsc_no(basicInfo.getSscNo());
        }
        employee.setIs_active(1);
        if (basicInfo.getTempPayrollId() != null && !basicInfo.getTempPayrollId().trim().isEmpty()) {
            employee.setTempPayrollId(basicInfo.getTempPayrollId());
        }
        if (basicInfo.getCreatedBy() != null && basicInfo.getCreatedBy() > 0) {
            employee.setCreated_by(basicInfo.getCreatedBy());
        }
        // Set created_date - required field (NOT NULL constraint)
        employee.setCreated_date(LocalDateTime.now());
        Integer campusId = basicInfo.getCampusId();
        if (campusId != null) {
            employee.setCampus_id(campusRepository.findByCampusIdAndIsActive(campusId, 1)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Active Campus not found with ID: " + campusId)));
        }
        // Update building_id - optional field
        Integer buildingId = basicInfo.getBuildingId();
        if (buildingId != null && buildingId > 0) {
            Building building = buildingRepository.findById(buildingId)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Building not found with ID: " + buildingId));
            // Validate building is active
            if (building.getIsActive() != 1) {
                throw new ResourceNotFoundException(
                        "Building with ID: " + buildingId + " is not active");
            }
            employee.setBuilding_id(building);
        } else if (buildingId != null && buildingId == 0) {
            // If buildingId is 0, it means no building is selected, so set to null
            employee.setBuilding_id(null);
        } else {
            employee.setBuilding_id(null);
        }
        Integer genderId = basicInfo.getGenderId();
        if (genderId != null) {
            employee.setGender(genderRepository.findByIdAndIsActive(genderId, 1)
                    .orElseThrow(() -> new ResourceNotFoundException("Active Gender not found")));
        }
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
                Integer replacedId = basicInfo.getReplacedByEmpId();
                if (replacedId == null || replacedId <= 0) {
                    throw new ResourceNotFoundException(
                            "replacedByEmpId is required when joinTypeId is 3 (Replacement).");
                }
                employee.setEmployee_replaceby_id(employeeRepository
                        .findById(replacedId)
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Replacement Employee not found with ID: " + replacedId)));
            } else if (basicInfo.getReplacedByEmpId() != null && basicInfo.getReplacedByEmpId() > 0) {
                Integer replacedId = basicInfo.getReplacedByEmpId();
                employee.setEmployee_replaceby_id(
                        employeeRepository.findById(replacedId).orElse(null));
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
            } else {
                employee.setContract_start_date(basicInfo.getDateOfJoin());
            }
            if (basicInfo.getContractEndDate() != null) {
                employee.setContract_end_date(basicInfo.getContractEndDate());
            } else {
                java.sql.Date startDate = basicInfo.getContractStartDate() != null ? basicInfo.getContractStartDate()
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
                    .orElseThrow(() -> new ResourceNotFoundException("EmployeeTypeHiring not found")));
        }
        EmployeeCheckListStatus pendingAtDOStatus = employeeCheckListStatusRepository
                .findByCheck_app_status_name("Pending at DO")
                .orElseThrow(() -> new ResourceNotFoundException(
                        "EmployeeCheckListStatus with name 'Pending at DO' not found"));
        employee.setEmp_check_list_status_id(pendingAtDOStatus);

        // Set emp_status_id from EmployeeStatus - always use "Current"
        EmployeeStatus employeeStatus = employeeStatusRepository.findByStatusNameAndIsActive("Current", 1)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Active EmployeeStatus with name 'Current' not found"));
        employee.setEmp_status_id(employeeStatus);

        if (basicInfo.getReferenceEmpId() != null && basicInfo.getReferenceEmpId() > 0) {
            employee.setEmployee_reference(employeeRepository.findByIdAndIs_active(basicInfo.getReferenceEmpId(), 1)
                    .orElseThrow(() -> new ResourceNotFoundException("Active Reference Employee not found")));
        } else {
            employee.setEmployee_reference(null);
        }
        if (basicInfo.getHiredByEmpId() != null && basicInfo.getHiredByEmpId() > 0) {
            employee.setEmployee_hired(employeeRepository.findByIdAndIs_active(basicInfo.getHiredByEmpId(), 1)
                    .orElseThrow(() -> new ResourceNotFoundException("Active Hired By Employee not found")));
        } else {
            employee.setEmployee_hired(null);
        }
        if (basicInfo.getManagerId() != null && basicInfo.getManagerId() > 0) {
            employee.setEmployee_manager_id(employeeRepository.findByIdAndIs_active(basicInfo.getManagerId(), 1)
                    .orElseThrow(() -> new ResourceNotFoundException("Active Manager not found")));
        } else {
            employee.setEmployee_manager_id(null);
        }
        if (basicInfo.getReportingManagerId() != null && basicInfo.getReportingManagerId() > 0) {
            employee.setEmployee_reporting_id(
                    employeeRepository.findByIdAndIs_active(basicInfo.getReportingManagerId(), 1)
                            .orElseThrow(() -> new ResourceNotFoundException("Active Reporting Manager not found")));
        } else {
            employee.setEmployee_reporting_id(null);
        }
        if (basicInfo.getPreChaitanyaId() != null && !basicInfo.getPreChaitanyaId().trim().isEmpty()
                && !"0".equals(basicInfo.getPreChaitanyaId().trim())) {
            Employee preChaitanyaEmp = employeeRepository
                    .findByPayRollIdAndIs_active(basicInfo.getPreChaitanyaId().trim(), 0)
                    .orElseThrow(
                            () -> new ResourceNotFoundException("Previous Chaitanya Employee not found or active"));
            employee.setPre_chaitanya_id(preChaitanyaEmp.getPayRollId());
        } else {
            employee.setPre_chaitanya_id(null);
        }
        return employee;
    }

    public EmpDetails prepareEmpDetailsEntity(BasicInfoDTO basicInfo, AddressInfoDTO addressInfo, Employee employee,
            Integer createdBy) {
        if (basicInfo == null)
            throw new ResourceNotFoundException("Basic Info is required");
        EmpDetails empDetails = new EmpDetails();
        empDetails.setEmployee_id(employee);
        empDetails.setAdhaar_name(basicInfo.getAdhaarName());
        empDetails.setDate_of_birth(basicInfo.getDateOfBirth());
        empDetails.setPersonal_email(basicInfo.getEmail());
        if (basicInfo.getEmergencyPhNo() == null || basicInfo.getEmergencyPhNo().trim().isEmpty()) {
            throw new ResourceNotFoundException("Emergency contact phone number is required");
        }
        empDetails.setEmergency_ph_no(basicInfo.getEmergencyPhNo().trim());
        Integer emergencyRelationId = basicInfo.getEmergencyRelationId();
        if (emergencyRelationId != null && emergencyRelationId > 0) {
            empDetails.setRelation_id(relationRepository.findById(emergencyRelationId)
                    .orElseThrow(() -> new ResourceNotFoundException("Emergency Relation not found")));
        } else {
            empDetails.setRelation_id(null);
        }
        empDetails.setAdhaar_no(basicInfo.getAdhaarNo());
        empDetails.setPancard_no(basicInfo.getPancardNum());
        empDetails.setAdhaar_enrolment_no(basicInfo.getAadharEnrolmentNum());
        empDetails.setPassout_year(0);
        empDetails.setIs_active(1);
        empDetails.setStatus("ACTIVE");
        if (basicInfo.getBloodGroupId() == null)
            throw new ResourceNotFoundException("BloodGroup ID is required");
        empDetails.setBloodGroup_id(bloodGroupRepository.findByIdAndIsActive(basicInfo.getBloodGroupId(), 1)
                .orElseThrow(() -> new ResourceNotFoundException("Active BloodGroup not found")));
        Integer casteId = basicInfo.getCasteId();
        if (casteId == null)
            throw new ResourceNotFoundException("Caste ID is required");
        empDetails.setCaste_id(casteRepository.findById(casteId)
                .orElseThrow(() -> new ResourceNotFoundException("Caste not found")));
        Integer religionId = basicInfo.getReligionId();
        if (religionId == null)
            throw new ResourceNotFoundException("Religion ID is required");
        empDetails.setReligion_id(relegionRepository.findById(religionId)
                .orElseThrow(() -> new ResourceNotFoundException("Religion not found")));
        if (basicInfo.getMaritalStatusId() == null)
            throw new ResourceNotFoundException("MaritalStatus ID is required");
        empDetails.setMarital_status_id(maritalStatusRepository.findByIdAndIsActive(basicInfo.getMaritalStatusId(), 1)
                .orElseThrow(() -> new ResourceNotFoundException("Active MaritalStatus not found")));
        // Set created_by and created_date - required fields (NOT NULL constraint)
        if (createdBy != null && createdBy > 0) {
            empDetails.setCreated_by(createdBy);
            empDetails.setCreated_date(LocalDateTime.now());
        } else {
            // If createdBy is not provided, use employee's created_by as fallback
            if (employee != null && employee.getCreated_by() != null && employee.getCreated_by() > 0) {
                empDetails.setCreated_by(employee.getCreated_by());
                empDetails.setCreated_date(LocalDateTime.now());
            } else {
                throw new ResourceNotFoundException(
                        "Created By is required for EmpDetails (NOT NULL column). Please provide createdBy in BasicInfoDTO or ensure Employee has created_by set.");
            }
        }

        // Correctly mapped fields - Required fields with @NotNull constraint
        if (basicInfo.getFatherName() == null || basicInfo.getFatherName().trim().isEmpty()) {
            throw new ResourceNotFoundException("Father Name is required");
        }
        empDetails.setFatherName(basicInfo.getFatherName().trim());

        if (basicInfo.getUanNo() == null) {
            throw new ResourceNotFoundException("UAN Number is required");
        }
        empDetails.setUanNo(basicInfo.getUanNo());

        return empDetails;
    }

    public void updateEmployeeEntity(Employee employee, BasicInfoDTO basicInfo) {
        if (basicInfo == null)
            return;
        if (basicInfo.getFirstName() != null && !basicInfo.getFirstName().trim().isEmpty())
            employee.setFirst_name(basicInfo.getFirstName());
        if (basicInfo.getLastName() != null && !basicInfo.getLastName().trim().isEmpty())
            employee.setLast_name(basicInfo.getLastName());
        if (basicInfo.getDateOfJoin() != null)
            employee.setDate_of_join(basicInfo.getDateOfJoin());
        if (basicInfo.getPrimaryMobileNo() != null && basicInfo.getPrimaryMobileNo() > 0)
            employee.setPrimary_mobile_no(basicInfo.getPrimaryMobileNo());
        if (basicInfo.getSecondaryMobileNo() != null && basicInfo.getSecondaryMobileNo() > 0)
            employee.setSecondary_mobile_no(basicInfo.getSecondaryMobileNo());
        employee.setEmail(null);
        if (basicInfo.getTotalExperience() != null)
            employee.setTotal_experience(basicInfo.getTotalExperience().doubleValue());
        if (basicInfo.getAge() != null)
            employee.setAge(basicInfo.getAge());
        if (Boolean.TRUE.equals(basicInfo.getSscNotAvailable())) {
            employee.setSsc_no(null);
        } else if (basicInfo.getSscNo() != null) {
            employee.setSsc_no(basicInfo.getSscNo());
        }
        if (basicInfo.getTempPayrollId() != null && !basicInfo.getTempPayrollId().trim().isEmpty())
            employee.setTempPayrollId(basicInfo.getTempPayrollId());
        if (basicInfo.getCreatedBy() != null && basicInfo.getCreatedBy() > 0)
            employee.setCreated_by(basicInfo.getCreatedBy());
        if (basicInfo.getCampusId() != null) {
            employee.setCampus_id(campusRepository.findByCampusIdAndIsActive(basicInfo.getCampusId(), 1)
                    .orElseThrow(() -> new ResourceNotFoundException("Active Campus not found")));
        }
        Integer buildingId = basicInfo.getBuildingId();
        if (buildingId != null && buildingId > 0) {
            Building building = buildingRepository.findById(buildingId)
                    .orElseThrow(() -> new ResourceNotFoundException("Building not found"));
            if (building.getIsActive() != 1)
                throw new ResourceNotFoundException("Building is not active");
            employee.setBuilding_id(building);
        } else {
            employee.setBuilding_id(null);
        }
        if (basicInfo.getGenderId() != null) {
            employee.setGender(genderRepository.findByIdAndIsActive(basicInfo.getGenderId(), 1)
                    .orElseThrow(() -> new ResourceNotFoundException("Active Gender not found")));
        }
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
                    throw new ResourceNotFoundException("replacedByEmpId is required when joinTypeId is 3.");
                }
                employee.setEmployee_replaceby_id(employeeRepository
                        .findById(basicInfo.getReplacedByEmpId())
                        .orElseThrow(() -> new ResourceNotFoundException("Replacement Employee not found")));
            } else if (basicInfo.getReplacedByEmpId() != null && basicInfo.getReplacedByEmpId() > 0) {
                employee.setEmployee_replaceby_id(
                        employeeRepository.findById(basicInfo.getReplacedByEmpId()).orElse(null));
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
                java.sql.Date startDate = basicInfo.getContractStartDate() != null ? basicInfo.getContractStartDate()
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
        Integer empTypeHiringId = basicInfo.getEmpTypeHiringId();
        if (empTypeHiringId != null) {
            employee.setEmployee_type_hiring_id(employeeTypeHiringRepository.findById(empTypeHiringId)
                    .orElseThrow(() -> new ResourceNotFoundException("EmployeeTypeHiring not found")));
        }
        if (basicInfo.getReferenceEmpId() != null && basicInfo.getReferenceEmpId() > 0) {
            employee.setEmployee_reference(employeeRepository.findByIdAndIs_active(basicInfo.getReferenceEmpId(), 1)
                    .orElseThrow(() -> new ResourceNotFoundException("Active Reference Employee not found")));
        } else {
            employee.setEmployee_reference(null);
        }
        if (basicInfo.getHiredByEmpId() != null && basicInfo.getHiredByEmpId() > 0) {
            employee.setEmployee_hired(employeeRepository.findByIdAndIs_active(basicInfo.getHiredByEmpId(), 1)
                    .orElseThrow(() -> new ResourceNotFoundException("Active Hired By Employee not found")));
        } else {
            employee.setEmployee_hired(null);
        }
        if (basicInfo.getManagerId() != null && basicInfo.getManagerId() > 0) {
            employee.setEmployee_manager_id(employeeRepository.findByIdAndIs_active(basicInfo.getManagerId(), 1)
                    .orElseThrow(() -> new ResourceNotFoundException("Active Manager not found")));
        } else {
            employee.setEmployee_manager_id(null);
        }
        if (basicInfo.getReportingManagerId() != null && basicInfo.getReportingManagerId() > 0) {
            employee.setEmployee_reporting_id(
                    employeeRepository.findByIdAndIs_active(basicInfo.getReportingManagerId(), 1)
                            .orElseThrow(() -> new ResourceNotFoundException("Active Reporting Manager not found")));
        } else {
            employee.setEmployee_reporting_id(null);
        }
        if (basicInfo.getPreChaitanyaId() != null && !basicInfo.getPreChaitanyaId().trim().isEmpty()
                && !"0".equals(basicInfo.getPreChaitanyaId().trim())) {
            Employee preChaitanyaEmp = employeeRepository
                    .findByPayRollIdAndIs_active(basicInfo.getPreChaitanyaId().trim(), 0)
                    .orElseThrow(
                            () -> new ResourceNotFoundException("Previous Chaitanya Employee not found or active"));
            employee.setPre_chaitanya_id(preChaitanyaEmp.getPayRollId());
        } else {
            employee.setPre_chaitanya_id(null);
        }

        // Set emp_status_id from EmployeeStatus - always use "Current"
        EmployeeStatus employeeStatus = employeeStatusRepository.findByStatusNameAndIsActive("Current", 1)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Active EmployeeStatus with name 'Current' not found"));
        employee.setEmp_status_id(employeeStatus);
    }

    // === UPDATED METHOD: Added FatherName and UAN setters ===
    public void updateEmpDetailsFields(EmpDetails target, EmpDetails source) {
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

        if (source.getFatherName() != null)
            target.setFatherName(source.getFatherName());
        if (source.getUanNo() != null)
            target.setUanNo(source.getUanNo());

        if (source.getBloodGroup_id() != null)
            target.setBloodGroup_id(source.getBloodGroup_id());
        if (source.getCaste_id() != null)
            target.setCaste_id(source.getCaste_id());
        if (source.getReligion_id() != null)
            target.setReligion_id(source.getReligion_id());
        if (source.getMarital_status_id() != null)
            target.setMarital_status_id(source.getMarital_status_id());
        target.setIs_active(source.getIs_active());
        if (source.getStatus() != null)
            target.setStatus(source.getStatus());
    }

    public void updateEmpDetailsFieldsExceptEmail(EmpDetails target, EmpDetails source) {
        if (source.getAdhaar_name() != null)
            target.setAdhaar_name(source.getAdhaar_name());
        if (source.getDate_of_birth() != null)
            target.setDate_of_birth(source.getDate_of_birth());
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

        if (source.getFatherName() != null)
            target.setFatherName(source.getFatherName());
        if (source.getUanNo() != null)
            target.setUanNo(source.getUanNo());

        if (source.getBloodGroup_id() != null)
            target.setBloodGroup_id(source.getBloodGroup_id());
        if (source.getCaste_id() != null)
            target.setCaste_id(source.getCaste_id());
        if (source.getReligion_id() != null)
            target.setReligion_id(source.getReligion_id());
        if (source.getMarital_status_id() != null)
            target.setMarital_status_id(source.getMarital_status_id());
        target.setIs_active(source.getIs_active());
        if (source.getStatus() != null)
            target.setStatus(source.getStatus());
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
