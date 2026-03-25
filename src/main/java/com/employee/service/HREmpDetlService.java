//package com.employee.service;
//
//
//import java.math.BigDecimal;
//import java.math.RoundingMode;
//import java.util.ArrayList;
//import java.util.LinkedHashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Optional;
//import java.util.Set;
//import java.util.stream.Collectors;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import com.employee.dto.AddressResponseDTO;
//import com.employee.dto.BankInfoGetDTO;
//import com.employee.dto.CampusPrincipalDTO;
//import com.employee.dto.ChequeDetailsDto;
//import com.employee.dto.EducationalDocumentStatusDTO;
//import com.employee.dto.EmpExperienceDetailsDTO;
//import com.employee.dto.EmpFamilyDetailsDTO;
////import com.employee.dto.EmpQualificationDetailsDTO;
//import com.employee.dto.EmployeeAgreementDetailsDto;
//import com.employee.dto.EmployeeBankDetailsResponseDTO;
//import com.employee.dto.EmployeeCampusInfoDTO;
//import com.employee.dto.EmployeeCurrentInfoDTO;
////import com.employee.dto.EmployeeCurrentInfoDTO.SubjectInfo;
//import com.employee.dto.EmployeeRelationDTO;
//import com.employee.dto.FamilyMemberInOrgDTO;
//import com.employee.dto.QualificationDetailsDto;
//import com.employee.entity.BankDetails;
//import com.employee.entity.Campus;
//import com.employee.entity.CampusContact;
//import com.employee.entity.EmpChequeDetails;
//import com.employee.entity.EmpDocType;
//import com.employee.entity.EmpDocuments;
//import com.employee.entity.EmpExperienceDetails;
//import com.employee.entity.EmpFamilyDetails;
//import com.employee.entity.EmpQualification;
//import com.employee.entity.EmpSubject;
//import com.employee.entity.EmpaddressInfo;
//import com.employee.entity.Employee;
//import com.employee.entity.EmployeeBasicInfoView;
//import com.employee.entity.Organization;
//import com.employee.exception.ResourceNotFoundException;
//import com.employee.repository.BankDetailsRepository;
//import com.employee.repository.CampusContactRepository;
//import com.employee.repository.EmpBasicInfoViewRepo;
//import com.employee.repository.EmpChequeDetailsRepository;
//import com.employee.repository.EmpDocTypeRepository;
//import com.employee.repository.EmpDocumentsRepository;
//import com.employee.repository.EmpExperienceDetailsRepository;
//import com.employee.repository.EmpFamilyDetailsRepository;
//import com.employee.repository.EmpQualificationRepository;
//import com.employee.repository.EmpSubjectRepository;
//import com.employee.repository.EmpaddressInfoRepository;
//import com.employee.repository.EmployeeRepository;
//import com.employee.repository.OrganizationRepository;
//
//import jakarta.transaction.Transactional;
//
//@Service
//public class HREmpDetlService {
//	
//	
//	private static final Logger logger = LoggerFactory.getLogger(HREmpDetlService.class);
//	@Autowired
//    private EmpFamilyDetailsRepository empFamilyDetailsRepository;
//	
//	
//	@Autowired EmpaddressInfoRepository empAddressInfoRepository;
//	@Autowired EmployeeRepository employeeRepository;
//	@Autowired BankDetailsRepository bankDetailsRepository;
//	@Autowired EmpSubjectRepository empSubjectRepository;
//	@Autowired EmpExperienceDetailsRepository empExperienceDetailsRepository;
//	@Autowired CampusContactRepository campusContactRepository;
//	@Autowired EmpBasicInfoViewRepo employeeBasicInfoRepository;
//	@Autowired EmpChequeDetailsRepository empChequeDetailsRepository;
//	@Autowired EmpQualificationRepository empQualificationRepository;
//	@Autowired OrganizationRepository organizationRepository;
//	@Autowired
//	private EmpDocumentsRepository empDocumentsRepository;
//	@Autowired
//	private EmpDocTypeRepository empDocTypeRepository;
//	
//
//	public List<EmpFamilyDetailsDTO> getFamilyMembersByPayrollId(String payrollId) {
//		// Caller code (Service/Controller)
//
//		List<EmpFamilyDetails> familyList = 
//		    empFamilyDetailsRepository.findByEmp_id_PayrollIdAndIsActive(payrollId, 1);
//
//        return familyList.stream().map(fam -> {
//            EmpFamilyDetailsDTO dto = new EmpFamilyDetailsDTO();
//            dto.setEmpFamilyDetlId(fam.getEmp_family_detl_id());
////            dto.setFirstName(fam.getFirst_name());
////            dto.setLastName(fam.getLast_name());
//            dto.setFullName(fam.getFullName());
//            dto.setAdhaarNo(fam.getAdhaarNo());
//            dto.setOccupation(fam.getOccupation());
//            dto.setGender(fam.getGender_id().getGenderName());
//            dto.setBloodGroup(fam.getBlood_group_id().getBloodGroupName());
//            dto.setNationality(fam.getNationality());
//            dto.setRelation(fam.getRelation_id().getStudentRelationType());
//            dto.setIsDependent(fam.getIs_dependent());
//            dto.setIsLate(fam.getIs_late());
//            dto.setEmail(fam.getEmail());
//            dto.setContactNumber(fam.getContact_no()); // ✅ correct field name
//            return dto;
//        }).collect(Collectors.toList());
//    }
//	
//	public Map<String, List<AddressResponseDTO>> getAddressByPayrollIdGrouped(String payrollId) {
//	    List<EmpaddressInfo> addresses = empAddressInfoRepository.findByEmpId_PayrollId(payrollId);
//
//	    // Convert entity list into DTO list grouped by address type
//	    return addresses.stream()
//	            .map(address -> new AddressResponseDTO(
//	                    address.getAddrs_type(),
//	                    address.getHouse_no(),
//	                    address.getLandmark(),
//	                    address.getPostal_code(),
//	                    address.getCity_id() != null ? address.getCity_id().getCityName() : null,
//	                    address.getState_id() != null ? address.getState_id().getStateName() : null,
//	                    address.getCountry_id() != null ? address.getCountry_id().getCountryName() : null,
//	                    address.getEmrg_contact_no()
//	                    
//	            ))
//	            .collect(Collectors.groupingBy(
//	                    AddressResponseDTO::getAddressType,
//	                    LinkedHashMap::new,
//	                    Collectors.toList()
//	            ));
//	}
//	
//	 public List<FamilyMemberInOrgDTO> getFamilyMembersInOrganization(String payrollId) {
//	        // Step 1: Get the main employee
//	        Employee employee = employeeRepository.findByPayRollId(payrollId)
//	                .orElseThrow(() -> new RuntimeException("Employee not found for payrollId: " + payrollId));
//
//	        // Step 2: Get family records
//	        List<EmpFamilyDetails> familyList = empFamilyDetailsRepository.findByEmp_id_EmpId(employee.getEmp_id());
//
//	        // Step 3: Filter and map to DTO
//	        return familyList.stream()
//	                .filter(f -> f.getIs_sri_chaitanya_emp() == 1 && f.getParent_emp_id() != null)
//	                .map(f -> {
//	                    Employee parent = f.getParent_emp_id();
//	                    return new FamilyMemberInOrgDTO(
//	                            parent.getFirst_name() + " " + parent.getLast_name(),
//	                            parent.getPayRollId(),
//	                            parent.getEmail(),
//	                            parent.getPrimary_mobile_no(),
//	                            parent.getDesignation() != null ? parent.getDesignation().getDesignation_name() : null
//	                    );
//	                })
//	                .collect(Collectors.toList());
//	    }
//	// 🔹 Manager Details
//	    public EmployeeRelationDTO getManagerDetails(String payrollId) {
//	        Employee employee = employeeRepository.findByPayRollId(payrollId)
//	                .orElseThrow(() -> new RuntimeException("Employee not found"));
//
//	        Employee manager = employee.getEmployee_manager_id();
//	        return mapToDTO(manager);
//	    }
//
//	    // 🔹 Reference Details
//	    public EmployeeRelationDTO getReferenceDetails(String payrollId) {
//	        Employee employee = employeeRepository.findByPayRollId(payrollId)
//	                .orElseThrow(() -> new RuntimeException("Employee not found"));
//
//	        Employee reference = employee.getEmployee_reference();
//	        return mapToDTO(reference);
//	    }
//
//	    // 🔹 Reporting Manager Details
//	    public EmployeeRelationDTO getReportingManagerDetails(String payrollId) {
//	        Employee employee = employeeRepository.findByPayRollId(payrollId)
//	                .orElseThrow(() -> new RuntimeException("Employee not found"));
//
//	        Employee reportingManager = employee.getEmployee_reporting_id();
//	        return mapToDTO(reportingManager);
//	    }
//
//	    // 🔹 Utility Mapper
//	    private EmployeeRelationDTO mapToDTO(Employee emp) {
//	        if (emp == null) return null;
//
//	        return new EmployeeRelationDTO(
//	                emp.getFirst_name() + " " + emp.getLast_name(),
//	                emp.getPayRollId(),
//	                emp.getEmail(),
//	                emp.getPrimary_mobile_no(),
//	                emp.getDesignation() != null ? emp.getDesignation().getDesignation_name(): null
//	        );
//	    }
//	    
//
//	    public EmployeeBankDetailsResponseDTO getBankDetailsByPayrollId(String payrollId) {
//
//	        // ✅ 1. Find employee
//	        Optional<Employee> employeeOpt = employeeRepository.findByPayRollId(payrollId);
//	        if (employeeOpt.isEmpty()) {
//	            throw new RuntimeException("Employee not found for payrollId: " + payrollId);
//	        }
//
//	        Employee employee = employeeOpt.get();
//
//	        // ✅ 2. Fetch active bank details
//	        List<BankDetails> bankDetailsList = bankDetailsRepository.findActiveBankDetailsByEmpId(employee.getEmp_id());
//
//	        if (bankDetailsList.isEmpty()) {
//	            throw new RuntimeException("No bank details found for payrollId: " + payrollId);
//	        }
//
//	        BankInfoGetDTO personalBankInfo = null;
//	        BankInfoGetDTO salaryAccountInfo = null;
//
//	        // ✅ 3. Map entity → DTO
//	        for (BankDetails bank : bankDetailsList) {
//	            BankInfoGetDTO dto = new BankInfoGetDTO();
//	            dto.setPaymentType(bank.getEmpPaymentType() != null ? bank.getEmpPaymentType().getPayment_type() : null);
//	            dto.setBankName(bank.getBankName());
//	            dto.setBankBranch(bank.getBankBranch());
//	            dto.setPersonalAccountHolderName(bank.getBankHolderName());
//	            dto.setPersonalAccountNumber(bank.getAccNo());
//	            dto.setIfscCode(bank.getIfscCode());
//	            // Set payableAt from BankDetails entity
//	            dto.setPayableAt(bank.getPayableAt());
//
//	            // Note: isSalaryLessThan40000 calculation removed as netPayable field no longer exists
//	            // This information should come from BankInfoDTO.salaryLessThan40000 if needed
//	            dto.setIsSalaryLessThan40000("N/A");
//
//	            // ✅ Classify based on accType
//	            if ("SALARY".equalsIgnoreCase(bank.getAccType()) || "SALARY ACCOUNT".equalsIgnoreCase(bank.getAccType())) {
//	                salaryAccountInfo = dto;
//	            } else {
//	                personalBankInfo = dto;
//	            }
//	        }
//
//	        // ✅ 4. Build final DTO response
//	        EmployeeBankDetailsResponseDTO response = new EmployeeBankDetailsResponseDTO();
//	        response.setPersonalBankInfo(personalBankInfo);
//	        response.setSalaryAccountInfo(salaryAccountInfo);
//
//	        return response;
//	    }
//	    
////	    public EmployeeCurrentInfoDTO getCurrentInfoByPayrollId(String payrollId) {
////
////	        // ✅ Use your existing Optional<Employee> method
////	        Optional<Employee> optionalEmp = employeeRepository.findByPayRollId(payrollId);
////
////	        if (optionalEmp.isEmpty()) {
////	            throw new RuntimeException("Employee not found for payrollId: " + payrollId);
////	        }
////
////	        Employee emp = optionalEmp.get();
////
////	        // Fetch subjects assigned to this employee
////	        List<EmpSubject> empSubjects = empSubjectRepository.findActiveSubjectsByEmpId(emp.getEmp_id());
////	        
////	        List<SubjectInfo> subjectInfoList = empSubjects.stream()
////	                .map(sub -> new SubjectInfo(
////	                        // 1. Subject Name
////	                        sub.getSubject_id() != null ? sub.getSubject_id().getSubject_name() : null,
////	                        
////	                        // 2. Agreed Per Week (Mapped from your single DB column)
////	                        sub.getAgree_no_period() 
////	                ))
////	                .collect(Collectors.toList());
////
////	        // Prepare final DTO
////	        EmployeeCurrentInfoDTO dto = new EmployeeCurrentInfoDTO();
////	        dto.setEmployeeName(emp.getFirst_name() + " " + emp.getLast_name());
////	        dto.setDateOfJoining(emp.getDate_of_join());
////	        dto.setHiredBy(emp.getEmployee_hired() != null
////	                ? emp.getEmployee_hired().getFirst_name() + " " + emp.getEmployee_hired().getLast_name()
////	                : null);
////	        dto.setReferredBy(emp.getEmployee_reference() != null
////	                ? emp.getEmployee_reference().getFirst_name() + " " + emp.getEmployee_reference().getLast_name()
////	                : null);
////	        dto.setSubjects(subjectInfoList);
////
////	        return dto;
////	    }
//	    
//	    public EmployeeCurrentInfoDTO getCurrentInfoByPayrollId(String payrollId) {
//
//	        // 1. Fetch Employee
//	        Optional<Employee> optionalEmp = employeeRepository.findByPayRollId(payrollId);
//	        if (optionalEmp.isEmpty()) {
//	            throw new RuntimeException("Employee not found");
//	        }
//	        Employee emp = optionalEmp.get();
//
//	        // 2. Prepare DTO
//	        EmployeeCurrentInfoDTO dto = new EmployeeCurrentInfoDTO();
//	        dto.setEmployeeName(emp.getFirst_name() + " " + emp.getLast_name());
//	        dto.setDateOfJoining(emp.getDate_of_join());
//	        dto.setHiredBy(emp.getEmployee_hired() != null ? emp.getEmployee_hired().getFirst_name() : null);
//	        dto.setReferredBy(emp.getEmployee_reference() != null ? emp.getEmployee_reference().getFirst_name() : null);
//
//	        // 3. Fetch Subjects and Set the FIRST one found
//	        List<EmpSubject> empSubjects = empSubjectRepository.findActiveSubjectsByEmpId(emp.getEmp_id());
//
//	        if (!empSubjects.isEmpty()) {
//	            EmpSubject firstSubject = empSubjects.get(0); // ✅ Get the first subject
//	            
//	            dto.setSubjectName(firstSubject.getSubject_id() != null ? firstSubject.getSubject_id().getSubject_name() : null);
//	            dto.setAgreedPerWeek(firstSubject.getAgree_no_period());
//	        } else {
//	            // Optional: Handle case where no subject exists
//	            dto.setSubjectName(null);
//	            dto.setAgreedPerWeek(0);
//	        }
//
//	        return dto;
//	    }
//	    
//	    public List<EmpExperienceDetailsDTO> getEmployeeExperienceByPayrollId(String payrollId) {
//
//	        // ✅ Step 1: Find employee
//	        Optional<Employee> empOpt = employeeRepository.findByPayRollId(payrollId);
//	        if (empOpt.isEmpty()) {
//	            throw new RuntimeException("Employee not found for payrollId: " + payrollId);
//	        }
//
//	        Employee employee = empOpt.get();
//
//	        // ✅ Step 2: Fetch experience records
//	        List<EmpExperienceDetails> expList = empExperienceDetailsRepository.findActiveByEmployeeId(employee.getEmp_id());
//
//	        if (expList.isEmpty()) {
//	            throw new RuntimeException("No experience records found for employee: " + payrollId);
//	        }
//
//	        // ✅ Step 3: Convert to DTO list
////	        return expList.stream().map(exp -> {
////	            EmpExperienceDetailsDTO dto = new EmpExperienceDetailsDTO();
////	            dto.setCompanyName(exp.getPre_organigation_name()));
////	            dto.setDesignation(exp.getDesignation());
////	            dto.setFromDate(exp.getDateOfJoin().toLocalDate());
////	            dto.setToDate(exp.getDateOfLeave().toLocalDate());
////	            dto.setLeavingReason(exp.getLeavingReason());
////	            dto.setCompanyAddress(exp.getCompanyAddr());
////	            dto.setNatureOfDuties(exp.getNatureOfDuties());
////
////	            BigDecimal ctc = BigDecimal.valueOf(exp.getGrossSalary());
////	            dto.setCtc(ctc);
////	            dto.setGrossSalaryPerMonth(ctc.divide(BigDecimal.valueOf(12), 2, BigDecimal.ROUND_HALF_UP));
////
////	            return dto;
////	        }).collect(Collectors.toList());
//	        return expList.stream().map(exp -> {
//	            EmpExperienceDetailsDTO dto = new EmpExperienceDetailsDTO();
//
//	            // --- THIS IS THE FIX ---
//	            // Changed the extra parenthesis ')' to a semicolon ';'
//	            dto.setCompanyName(exp.getPre_organigation_name());
//
//	            // These fields from your entity map to the DTO
//	            dto.setDesignation(exp.getDesignation());
//	            dto.setFromDate(exp.getDate_of_join().toLocalDate());
//	            dto.setToDate(exp.getDate_of_leave().toLocalDate());
//	            dto.setLeavingReason(exp.getLeaving_reason());
//	            dto.setCompanyAddress(exp.getCompany_addr());
//	            dto.setNatureOfDuties(exp.getNature_of_duties());
//
//	            // Handle Salary Calculation
//	            BigDecimal ctc = BigDecimal.valueOf(exp.getGross_salary());
//	            dto.setCtc(ctc);
//
//	            // Calculate per-month salary (using RoundingMode enum is preferred)
//	            dto.setGrossSalaryPerMonth(ctc.divide(BigDecimal.valueOf(12), 2, RoundingMode.HALF_UP));
//
//	            return dto;
//	        }).collect(Collectors.toList());
//	    }
//	    public Optional<EmployeeBasicInfoView> getBasicInfoByPayrollId(String payrollId) {
//	        
//	        // ==== NEW, MORE EFFICIENT LOGIC ====
//	        // We no longer need to find the Employee first.
//	        // We can query the EmployeeBasicInfoRepository directly.
//	        return employeeBasicInfoRepository.findByPayrollId(payrollId);
//	        }
//	    
////	    public EmployeeAgreementDetailsDto getChequeDetailsByPayrollId(String payrollId) {
////	        Optional<Employee> empOpt = employeeRepository.findByPayrollId(payrollId);
////	        if (empOpt.isEmpty()) {
////	            throw new RuntimeException("Employee not found for payrollId: " + payrollId);
////	        }
////
////	        Employee emp = empOpt.get();
////	        List<EmpChequeDetails> cheques = empChequeDetailsRepository.findActiveChequesByEmpId(emp.getEmpId());
////
////	        List<ChequeDetailsDto> chequeDtos = cheques.stream()
////	                .map(chq -> new ChequeDetailsDto(
////	                        chq.getChequeNo(),
////	                        chq.getChequeBankName(),
////	                        chq.getChequeBankIfscCode()
////	                ))
////	                .collect(Collectors.toList());
////
////	        EmployeeAgreementDetailsDto response = new EmployeeAgreementDetailsDto();
////	        response.setAgreementCompany(
////	                emp.getAgreementOrgId() != null ? emp.getAgreementOrgId().getOrganizationName() : "N/A"
////	        );
////	        response.setAgreementType(emp.getAgreementType());
////	        response.setNoOfCheques(chequeDtos.size());
////	        response.setCheques(chequeDtos);
////
////	        return response;
////	    }
//	    
//	    
//	    
//	    
//	    
//	    
//	    
////	    
////	  
////
////	    public EmployeeAgreementDetailsDto getChequeDetailsByPayrollId(String payrollId) {
////	        
////	        // --- FIX 1 ---
////	        // Renamed to 'findByPayRollId' to match your Employee entity's 'payRollId' field
////	        Optional<Employee> empOpt = employeeRepository.findByPayRollId(payrollId);
////	        
////	        if (empOpt.isEmpty()) {
////	            throw new RuntimeException("Employee not found for payrollId: " + payrollId);
////	        }
////
////	        Employee emp = empOpt.get();
////
////	        // --- FIX 2 ---
////	        // Changed 'emp.getEmpId()' to 'emp.getEmp_id()' to match your Employee entity
////	        List<EmpChequeDetails> cheques = empChequeDetailsRepository.findActiveChequesByEmpId(emp.getEmp_id());
////
////	        // This part was correct and matches your ChequeDetailsDto
////	        List<ChequeDetailsDto> chequeDtos = cheques.stream()
////	                .map(chq -> new ChequeDetailsDto(
////	                        chq.getChequeNo(),
////	                        chq.getChequeBankName(),
////	                        chq.getChequeBankIfscCode()
////	                ))
////	                .collect(Collectors.toList());
////
////	        EmployeeAgreementDetailsDto response = new EmployeeAgreementDetailsDto();
////	        String orgName = "N/A";
////	        Integer orgId = emp.getAgreement_org_id();
////	        if (orgId != null) {
////                // 2. Use the findById method to get the Organization
////	            Optional<Organization> orgOpt = organizationRepository.findById(orgId);
////	            
////	            if (orgOpt.isPresent()) {
////                    // 3. Get the name from the found object
////	                orgName = orgOpt.get().getOrganizationName(); 
////	            }
////	        }
////	        response.setAgreementCompany(orgName);
////
////	        // --- FIX 4 ---
////	        response.setAgreementType(emp.getAgreement_type());
////
////	        // These fields were correct
////	        response.setNoOfCheques(chequeDtos.size());
////	        response.setCheques(chequeDtos);
////
////	        return response;
////	    
////	        
////	    }
//	    
//	    
//	 // Assuming this is inside a Service class that has @Autowired employeeRepository, 
//	 // empChequeDetailsRepository, AND organizationRepository.
//	    
//	 @Transactional
//	 public EmployeeAgreementDetailsDto getChequeDetailsByPayrollId(String payrollId) {
//	     
//	     // 1. Find employee using correct field name payRollId (capital R)
//	     Optional<Employee> empOpt = employeeRepository.findByPayRollId(payrollId);
//	     
//	     if (empOpt.isEmpty()) {
//	         throw new RuntimeException("Employee not found for payrollId: " + payrollId);
//	     }
//
//	     Employee emp = empOpt.get();
//
//	     // 2. Fetch Cheques using correct getter emp.getEmp_id()
//	     List<EmpChequeDetails> cheques = empChequeDetailsRepository.findActiveChequesByEmpId(emp.getEmp_id());
//
//	     // 3. Map Cheque entities to DTOs (This logic is correct)
//	     List<ChequeDetailsDto> chequeDtos = cheques.stream()
//	             .map(chq -> new ChequeDetailsDto(
//	                     chq.getChequeNo(),
//	                     chq.getChequeBankName(),
//	                     chq.getChequeBankIfscCode()
//	             ))
//	             .collect(Collectors.toList());
//
//	     EmployeeAgreementDetailsDto response = new EmployeeAgreementDetailsDto();
//	     
//	     // 4. Resolve Organization Name using the manually fetched ID
//	     String orgName = "N/A";
//	     Integer orgId = emp.getAgreement_org_id(); // Correctly uses emp.getAgreement_org_id()
//	     
//	     if (orgId != null) {
//	         Optional<Organization> orgOpt = organizationRepository.findById(orgId);
//	         
//	         if (orgOpt.isPresent()) {
//	             orgName = orgOpt.get().getOrganizationName(); 
//	         }
//	     }
//	     
//	     // 5. Set response fields using correct getters
//	     response.setAgreementCompany(orgName);
//	     response.setAgreementType(emp.getAgreement_type()); // Correctly uses emp.getAgreement_type()
//
//	     // 6. Final DTO population
//	     response.setNoOfCheques(chequeDtos.size());
//	     response.setCheques(chequeDtos);
//
//	     return response;
//	 } 
//	    
//	    
//	    
//	    public EmployeeCampusInfoDTO getEmployeeCampusInfo(String payrollId) {
//	        
//	        // 1. Fetch the employee by payrollId
//	        Employee employee = employeeRepository.findByPayRollId(payrollId)
//	                .orElseThrow(() -> new RuntimeException("Employee not found with payrollId: " + payrollId));
//
//	        Campus campus = employee.getCampus_id();
//	        if (campus == null) {
//	            throw new RuntimeException("Employee is not associated with any campus.");
//	        }
//
//	        EmployeeCampusInfoDTO dto = new EmployeeCampusInfoDTO();
//
//	        // 2. Populate Top Card (Current Campus Info)
//	        dto.setCampusName(campus.getCampusName());
//	        dto.setCampusCode(campus.getCmps_code());
//	        dto.setCampusType(campus.getCmps_type());
//
//	        if (employee.getDesignation() != null) {
//	            dto.setDesignationName(employee.getDesignation().getDesignation_name()); // Assumes getter
//	        }
//	        if (employee.getWorkingMode_id() != null) {
//	            dto.setWorkMode(employee.getWorkingMode_id().getWork_mode_type()); // Assumes getter
//	        }
//	        if (employee.getJoin_type_id() != null) {
//	            dto.setJoiningAs(employee.getJoin_type_id().getJoin_type()); // Assumes getter
//	        }
//	        if (employee.getEmployee_replaceby_id() != null) {
//	            dto.setReplacementEmployeeName(
//	                    employee.getEmployee_replaceby_id().getFirst_name() + " " +
//	                    employee.getEmployee_replaceby_id().getLast_name()
//	            );
//	        }
//
//	        // 3. Populate Principal Card (Find by Designation "Principal")
//	        Optional<CampusContact> principalOpt = campusContactRepository
//	                .findByCmpsIdAndDesignation(campus, "PRINCIPAL"); // <-- Assuming designation is "Principal"
//
//	        if (principalOpt.isPresent()) {
//	            CampusContact principal = principalOpt.get();
//	            CampusPrincipalDTO pDto = new CampusPrincipalDTO(
//	                    principal.getEmpName(),
//	                    principal.getDesignation(),
//	                    principal.getContactNo(),
//	                    principal.getEmail()
//	            );
//	            dto.setPrincipalInfo(pDto);
//	        }
//
//	        // 4. Populate Campus Address Card
////	        CampusAddressDTO aDto = new CampusAddressDTO();
////	        aDto.setCampusName(campus.getCampusName());
////	        
////	        if (campus.getCity() != null) {
////	            aDto.setCity(campus.getCity().getName()); // Assumes getter
////	        }
////	        if (campus.getState() != null) {
////	            aDto.setState(campus.getState().getName()); // Assumes getter
////	        }
////	         if (campus.getZone() != null) {
////	            aDto.setZone(campus.getZone().getName()); // Assumes getter
////	        }
////	        dto.setAddressInfo(aDto);
//
//	        return dto;
//	    }
//	    
//	    public List<String> getQualificationNamesByPayrollId(String payrollId) {
//	        Optional<Employee> employee = employeeRepository.findByPayRollId(payrollId);
//	        
//	        List<EmpQualification> qualifications = empQualificationRepository
//	                .findActiveQualificationsByEmployee(employee);
// 
//	        return qualifications.stream()
//	                .map(eq -> eq.getQualification_id().getQualification_name()) // Assumes getQualificationName()
//	                .distinct()
//	                .collect(Collectors.toList());
//	    }
//
//	    /**
//	     * API 2: Gets the details for a specific qualification name.
//	     */
//	    public List<QualificationDetailsDto> getQualificationsByPayrollId(String payrollId) {
//	        List<EmpQualification> qualifications = empQualificationRepository.findByEmployeePayrollId(payrollId);
//
//	     // Assuming 'qualifications' is List<EmpQualification>
//	        return qualifications.stream().map(eq -> {
//	            QualificationDetailsDto dto = new QualificationDetailsDto();
//	            
//	            // --- FIX 1 (Syntax Error) ---
//	            // The getter method call was incomplete and had a parenthesis error.
//	            dto.setQualificationName(eq.getQualification_id().getQualification_name());
//	            
//	            // --- FIX 2 (Field Name Mismatch) ---
//	            // Corrected getter: eq.getQualification_degree_id().getDegree_name()
//	            dto.setQualificationDegree(eq.getQualification_degree_id().getDegree_name());
//	            
//	            // --- FIX 3 (Field Name Mismatch) ---
//	            // Corrected setter: dto.setPassedoutYear()
//	            dto.setPassedoutYear(eq.getPassedout_year()); // Assuming entity uses getPassedout_year()
//	            
//	            // These fields are assumed correct (based on your entity's field names)
//	            dto.setSpecialization(eq.getSpecialization());
//	            dto.setInstitute(eq.getInstitute());
//	            dto.setUniversity(eq.getUniversity());
//	            
//	            return dto;
//	        }).collect(Collectors.toList());
//	    }
//	    
//	    
//	    /**
//	     * Retrieves the comprehensive status (Required, Uploaded, Missing) of educational documents 
//	     * for an employee based on their specific qualifications.
//	     * * @param payrollId The employee's payroll ID.
//	     * @return DTO containing lists of required, uploaded, and missing educational documents.
//	     * @throws ResourceNotFoundException if the employee or their qualifications are not found.
//	     */
//	    public EducationalDocumentStatusDTO getEducationalDocumentsStatusByPayrollId(String payrollId) {
//	        logger.info("Getting educational documents status for payroll_id: {}", payrollId);
//	        
//	        Employee employee = employeeRepository.findByPayrollId(payrollId)
//	                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with payroll_id: " + payrollId));
//	        
//	        Integer empId = employee.getEmp_id();
//	        logger.info("Found employee - emp_id: {}, payroll_id: {}, temp_payroll_id: {}", 
//	                empId, employee.getPayRollId(), employee.getTempPayrollId()); 
//	        
//	        List<EmpQualification> employeeQualifications = empQualificationRepository.findByEmpIdAndIsActive(empId);
//	        
//	        if (employeeQualifications.isEmpty()) {
//	            throw new ResourceNotFoundException("Employee (emp_id: " + empId + ") does not have any qualifications in EmpQualification table");
//	        }
//	        
//	        Set<Integer> employeeQualificationIds = employeeQualifications.stream()
//	                .map(eq -> eq.getQualification_id().getQualification_id())
//	                .collect(Collectors.toSet());
//	        
//	        logger.info("Employee has qualifications with IDs: {}", employeeQualificationIds);
//	        
//	        Integer highestQualificationId = employee.getQualification_id() != null ? 
//	                employee.getQualification_id().getQualification_id() : 
//	                employeeQualificationIds.stream().mapToInt(Integer::intValue).max().orElse(0);
//	        
//	        String qualificationName = employee.getQualification_id() != null ? 
//	                employee.getQualification_id().getQualification_name() : "Multiple Qualifications";
//	        
//	        List<EmpDocType> educationalDocTypes = empDocTypeRepository.findByDocTypeAndIsActive("Educational Document");
//	        if (educationalDocTypes.isEmpty()) {
//	            logger.warn("No active Educational Document types found, trying all records");
//	            educationalDocTypes = empDocTypeRepository.findByDocType("Educational Document");
//	        }
//	        
//	        educationalDocTypes.sort((a, b) -> Integer.compare(a.getDoc_type_id(), b.getDoc_type_id()));
//	        
//	        List<EmpDocType> requiredDocTypes = new ArrayList<>();
//	        for (Integer qualId : employeeQualificationIds) {
//	            EmpDocType matchingDoc = educationalDocTypes.stream()
//	                    .filter(dt -> dt.getDoc_type_id() == qualId)
//	                    .findFirst()
//	                    .orElse(null);
//	            if (matchingDoc != null && !requiredDocTypes.contains(matchingDoc)) {
//	                requiredDocTypes.add(matchingDoc);
//	            }
//	        }
//	        
//	        requiredDocTypes.sort((a, b) -> Integer.compare(a.getDoc_type_id(), b.getDoc_type_id()));
//	        
//	        List<Integer> requiredDocTypeIds = requiredDocTypes.stream()
//	                .mapToInt(EmpDocType::getDoc_type_id)
//	                .boxed()
//	                .collect(Collectors.toList());
//	        
//	        List<EmpDocuments> uploadedDocs = empDocumentsRepository.findByEmpIdAndDocTypeIds(empId, requiredDocTypeIds);
//	        Set<Integer> uploadedDocTypeIds = uploadedDocs.stream()
//	                .map(doc -> doc.getEmp_doc_type_id().getDoc_type_id())
//	                .collect(Collectors.toSet());
//	        
//	        EducationalDocumentStatusDTO response = new EducationalDocumentStatusDTO();
//	        response.setEmpId(empId);
//	        response.setPayrollId(employee.getPayRollId());
//	        response.setTempPayrollId(employee.getTempPayrollId());
//	        response.setQualificationId(highestQualificationId);
//	        response.setQualificationName(qualificationName);
//	        
//	        List<EducationalDocumentStatusDTO.DocumentStatusDTO> requiredDocumentsList = new ArrayList<>();
//	        List<EducationalDocumentStatusDTO.DocumentStatusDTO> uploadedDocumentsList = new ArrayList<>();
//	        List<EducationalDocumentStatusDTO.DocumentStatusDTO> missingDocumentsList = new ArrayList<>();
//	        
//	        for (EmpDocType docType : requiredDocTypes) {
//	            EducationalDocumentStatusDTO.DocumentStatusDTO docStatus = new EducationalDocumentStatusDTO.DocumentStatusDTO();
//	            docStatus.setDocTypeId(docType.getDoc_type_id());
//	            docStatus.setDocName(docType.getDoc_name());
//	            docStatus.setDocType(docType.getDoc_type());
//	            
//	            boolean isUploaded = uploadedDocTypeIds.contains(docType.getDoc_type_id());
//	            docStatus.setIsUploaded(isUploaded);
//	            
//	            if (isUploaded) {
//	                EmpDocuments uploadedDoc = uploadedDocs.stream()
//	                        .filter(doc -> doc.getEmp_doc_type_id().getDoc_type_id() == docType.getDoc_type_id())
//	                        .findFirst()
//	                        .orElse(null);
//	                if (uploadedDoc != null) {
//	                    docStatus.setDocPath(uploadedDoc.getDoc_path());
//	                }
//	                uploadedDocumentsList.add(docStatus);
//	            } else {
//	                missingDocumentsList.add(docStatus);
//	            }
//	            requiredDocumentsList.add(docStatus);
//	        }
//	        
//	        response.setRequiredDocuments(requiredDocumentsList);
//	        response.setUploadedDocuments(uploadedDocumentsList);
//	        response.setMissingDocuments(missingDocumentsList);
//	        
//	        return response;
//	    }
//
//	    /**
//	     * Retrieves only the list of educational documents that have already been uploaded by the employee.
//	     * * @param payrollId The employee's payroll ID.
//	     * @return List of uploaded educational documents.
//	     */
//	    public List<EducationalDocumentStatusDTO.DocumentStatusDTO> getUploadedEducationalDocuments(String payrollId) {
//	        logger.info("Getting uploaded educational documents for payroll_id: {}", payrollId);
//	        EducationalDocumentStatusDTO status = getEducationalDocumentsStatusByPayrollId(payrollId);
//	        return status.getUploadedDocuments();
//	    }
//
//	    /**
//	     * Retrieves only the list of educational documents that are missing and need to be uploaded.
//	     * * @param payrollId The employee's payroll ID.
//	     * @return List of missing educational documents.
//	     */
//	    public List<EducationalDocumentStatusDTO.DocumentStatusDTO> getMissingEducationalDocuments(String payrollId) {
//	        logger.info("Getting missing educational documents for payroll_id: {}", payrollId);
//	        EducationalDocumentStatusDTO status = getEducationalDocumentsStatusByPayrollId(payrollId);
//	        return status.getMissingDocuments();
//	    }
//
//	    /**
//	     * Retrieves the comprehensive status (Required, Uploaded, Missing) of ID Proof documents.
//	     * * @param payrollId The employee's payroll ID.
//	     * @return DTO containing lists of required, uploaded, and missing ID proof documents.
//	     * @throws ResourceNotFoundException if the employee is not found.
//	     */
//	    public EducationalDocumentStatusDTO getIdProofDocumentsStatusByPayrollId(String payrollId) {
//	        logger.info("Getting ID Proof documents status for payroll_id: {}", payrollId);
//	        
//	        Employee employee = employeeRepository.findByPayrollId(payrollId)
//	                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with payroll_id: " + payrollId));
//	        
//	        Integer empId = employee.getEmp_id();
//	        logger.info("Found employee - emp_id: {}, payroll_id: {}", empId, employee.getPayRollId());
//	        
//	        List<EmpDocType> idProofDocTypes = empDocTypeRepository.findByDocTypeAndIsActive("ID Proof");
//	        if (idProofDocTypes.isEmpty()) {
//	            logger.warn("No active ID Proof Document types found, trying all records");
//	            idProofDocTypes = empDocTypeRepository.findByDocType("ID Proof");
//	        }
//	        
//	        idProofDocTypes.sort((a, b) -> Integer.compare(a.getDoc_type_id(), b.getDoc_type_id()));
//	        
//	        List<Integer> idProofDocTypeIds = idProofDocTypes.stream()
//	                .mapToInt(EmpDocType::getDoc_type_id)
//	                .boxed()
//	                .collect(Collectors.toList());
//	        
//	        List<EmpDocuments> uploadedDocs = empDocumentsRepository.findByEmpIdAndDocTypeIds(empId, idProofDocTypeIds);
//	        Set<Integer> uploadedDocTypeIds = uploadedDocs.stream()
//	                .map(doc -> doc.getEmp_doc_type_id().getDoc_type_id())
//	                .collect(Collectors.toSet());
//	        
//	        EducationalDocumentStatusDTO response = new EducationalDocumentStatusDTO();
//	        response.setEmpId(empId);
//	        response.setPayrollId(employee.getPayRollId());
//	        response.setTempPayrollId(employee.getTempPayrollId());
//	        
//	        List<EducationalDocumentStatusDTO.DocumentStatusDTO> requiredDocumentsList = new ArrayList<>();
//	        List<EducationalDocumentStatusDTO.DocumentStatusDTO> uploadedDocumentsList = new ArrayList<>();
//	        List<EducationalDocumentStatusDTO.DocumentStatusDTO> missingDocumentsList = new ArrayList<>();
//	        
//	        for (EmpDocType docType : idProofDocTypes) {
//	            EducationalDocumentStatusDTO.DocumentStatusDTO docStatus = new EducationalDocumentStatusDTO.DocumentStatusDTO();
//	            docStatus.setDocTypeId(docType.getDoc_type_id());
//	            docStatus.setDocName(docType.getDoc_name());
//	            docStatus.setDocType(docType.getDoc_type());
//	            
//	            boolean isUploaded = uploadedDocTypeIds.contains(docType.getDoc_type_id());
//	            docStatus.setIsUploaded(isUploaded);
//	            
//	            if (isUploaded) {
//	                EmpDocuments uploadedDoc = uploadedDocs.stream()
//	                        .filter(doc -> doc.getEmp_doc_type_id().getDoc_type_id() == docType.getDoc_type_id())
//	                        .findFirst()
//	                        .orElse(null);
//	                if (uploadedDoc != null) {
//	                    docStatus.setDocPath(uploadedDoc.getDoc_path());
//	                }
//	                uploadedDocumentsList.add(docStatus);
//	            } else {
//	                missingDocumentsList.add(docStatus);
//	            }
//	            requiredDocumentsList.add(docStatus);
//	        }
//	        
//	        response.setRequiredDocuments(requiredDocumentsList);
//	        response.setUploadedDocuments(uploadedDocumentsList);
//	        response.setMissingDocuments(missingDocumentsList);
//	        
//	        return response;
//	    }
//
//	    /**
//	     * Retrieves only the list of ID Proof documents that have already been uploaded.
//	     * * @param payrollId The employee's payroll ID.
//	     * @return List of uploaded ID proof documents.
//	     */
//	    public List<EducationalDocumentStatusDTO.DocumentStatusDTO> getUploadedIdProofDocuments(String payrollId) {
//	        logger.info("Getting uploaded ID Proof documents for payroll_id: {}", payrollId);
//	        EducationalDocumentStatusDTO status = getIdProofDocumentsStatusByPayrollId(payrollId);
//	        return status.getUploadedDocuments();
//	    }
//
//	    /**
//	     * Retrieves only the list of ID Proof documents that are missing.
//	     * * @param payrollId The employee's payroll ID.
//	     * @return List of missing ID proof documents.
//	     */
//	    public List<EducationalDocumentStatusDTO.DocumentStatusDTO> getMissingIdProofDocuments(String payrollId) {
//	        logger.info("Getting missing ID Proof documents for payroll_id: {}", payrollId);
//	        EducationalDocumentStatusDTO status = getIdProofDocumentsStatusByPayrollId(payrollId);
//	        return status.getMissingDocuments();
//	    }
//
//	    /**
//	     * Retrieves the status of specific required documents (Personal, Previous Employment, Gratuity).
//	     * * @param payrollId The employee's payroll ID.
//	     * @return DTO containing lists of required, uploaded, and missing specific documents.
//	     * @throws ResourceNotFoundException if the employee is not found.
//	     */
//	    public EducationalDocumentStatusDTO getSpecificDocumentsStatusByPayrollId(String payrollId) {
//	        logger.info("Getting specific documents status for payroll_id: {}", payrollId);
//	        
//	        Employee employee = employeeRepository.findByPayrollId(payrollId)
//	                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with payroll_id: " + payrollId));
//	        
//	        Integer empId = employee.getEmp_id();
//	        logger.info("Found employee - emp_id: {}, payroll_id: {}", empId, employee.getPayRollId());
//	        
//	        List<EmpDocType> personalDocTypes = empDocTypeRepository.findByDocTypeAndIsActive("Personal Document");
//	        if (personalDocTypes.isEmpty()) {
//	            personalDocTypes = empDocTypeRepository.findByDocType("Personal Document");
//	        }
//	        
//	        Optional<EmpDocType> prevEmployeeDocOpt = empDocTypeRepository.findByDocNameAndIsActive("PrevEmployeeDoc", 1);
//	        if (prevEmployeeDocOpt.isEmpty()) {
//	            prevEmployeeDocOpt = empDocTypeRepository.findByDocName("PrevEmployeeDoc");
//	        }
//	        
//	        Optional<EmpDocType> gratuityDocOpt = empDocTypeRepository.findByDocNameAndIsActive("Gratuity", 1);
//	        if (gratuityDocOpt.isEmpty()) {
//	            gratuityDocOpt = empDocTypeRepository.findByDocName("Gratuity");
//	        }
//	        if (gratuityDocOpt.isEmpty()) {
//	            gratuityDocOpt = empDocTypeRepository.findByDocName("Gratuity From");
//	        }
//	        
//	        List<EmpDocType> requiredDocTypes = new ArrayList<>(personalDocTypes);
//	        if (prevEmployeeDocOpt.isPresent()) {
//	            requiredDocTypes.add(prevEmployeeDocOpt.get());
//	        }
//	        if (gratuityDocOpt.isPresent()) {
//	            requiredDocTypes.add(gratuityDocOpt.get());
//	        }
//	        
//	        requiredDocTypes.sort((a, b) -> Integer.compare(a.getDoc_type_id(), b.getDoc_type_id()));
//	        
//	        List<Integer> requiredDocTypeIds = requiredDocTypes.stream()
//	                .mapToInt(EmpDocType::getDoc_type_id)
//	                .boxed()
//	                .collect(Collectors.toList());
//	        
//	        List<EmpDocuments> uploadedDocs = empDocumentsRepository.findByEmpIdAndDocTypeIds(empId, requiredDocTypeIds);
//	        Set<Integer> uploadedDocTypeIds = uploadedDocs.stream()
//	                .map(doc -> doc.getEmp_doc_type_id().getDoc_type_id())
//	                .collect(Collectors.toSet());
//	        
//	        EducationalDocumentStatusDTO response = new EducationalDocumentStatusDTO();
//	        response.setEmpId(empId);
//	        response.setPayrollId(employee.getPayRollId());
//	        response.setTempPayrollId(employee.getTempPayrollId());
//	        
//	        List<EducationalDocumentStatusDTO.DocumentStatusDTO> requiredDocumentsList = new ArrayList<>();
//	        List<EducationalDocumentStatusDTO.DocumentStatusDTO> uploadedDocumentsList = new ArrayList<>();
//	        List<EducationalDocumentStatusDTO.DocumentStatusDTO> missingDocumentsList = new ArrayList<>();
//	        
//	        for (EmpDocType docType : requiredDocTypes) {
//	            EducationalDocumentStatusDTO.DocumentStatusDTO docStatus = new EducationalDocumentStatusDTO.DocumentStatusDTO();
//	            docStatus.setDocTypeId(docType.getDoc_type_id());
//	            docStatus.setDocName(docType.getDoc_name());
//	            docStatus.setDocType(docType.getDoc_type());
//	            
//	            boolean isUploaded = uploadedDocTypeIds.contains(docType.getDoc_type_id());
//	            docStatus.setIsUploaded(isUploaded);
//	            
//	            if (isUploaded) {
//	                EmpDocuments uploadedDoc = uploadedDocs.stream()
//	                        .filter(doc -> doc.getEmp_doc_type_id().getDoc_type_id() == docType.getDoc_type_id())
//	                        .findFirst()
//	                        .orElse(null);
//	                if (uploadedDoc != null) {
//	                    docStatus.setDocPath(uploadedDoc.getDoc_path());
//	                }
//	                uploadedDocumentsList.add(docStatus);
//	            } else {
//	                missingDocumentsList.add(docStatus);
//	            }
//	            requiredDocumentsList.add(docStatus);
//	        }
//	        
//	        response.setRequiredDocuments(requiredDocumentsList);
//	        response.setUploadedDocuments(uploadedDocumentsList);
//	        response.setMissingDocuments(missingDocumentsList);
//	        
//	        return response;
//	    }
//
//	    /**
//	     * Retrieves only the list of specific documents (Personal/Gratuity/PrevEmployee) that have been uploaded.
//	     * * @param payrollId The employee's payroll ID.
//	     * @return List of uploaded specific documents.
//	     */
//	    public List<EducationalDocumentStatusDTO.DocumentStatusDTO> getUploadedSpecificDocuments(String payrollId) {
//	        logger.info("Getting uploaded specific documents for payroll_id: {}", payrollId);
//	        EducationalDocumentStatusDTO status = getSpecificDocumentsStatusByPayrollId(payrollId);
//	        return status.getUploadedDocuments();
//	    }
//
//	    /**
//	     * Retrieves only the list of specific documents (Personal/Gratuity/PrevEmployee) that are missing.
//	     * * @param payrollId The employee's payroll ID.
//	     * @return List of missing specific documents.
//	     */
//	    public List<EducationalDocumentStatusDTO.DocumentStatusDTO> getMissingSpecificDocuments(String payrollId) {
//	        logger.info("Getting missing specific documents for payroll_id: {}", payrollId);
//	        EducationalDocumentStatusDTO status = getSpecificDocumentsStatusByPayrollId(payrollId);
//	        return status.getMissingDocuments();
//	    }
//	  
//}

package com.employee.service;

import java.math.BigDecimal;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.employee.dto.AddressResponseDTO;
import com.employee.dto.BankInfoGetDTO;
import com.employee.dto.CampusPrincipalDTO;
import com.employee.dto.ChequeDetailsDto;
import com.employee.dto.EducationalDocumentStatusDTO;
import com.employee.dto.EmpExperienceDetailsDTO;
import com.employee.dto.EmpFamilyDetailsDTO;
//import com.employee.dto.EmpQualificationDetailsDTO;
import com.employee.dto.EmployeeAgreementDetailsDto;
import com.employee.dto.EmployeeBankDetailsResponseDTO;
import com.employee.dto.EmployeeCampusInfoDTO;
import com.employee.dto.EmployeeCurrentInfoDTO;
//import com.employee.dto.EmployeeCurrentInfoDTO.SubjectInfo;
import com.employee.dto.EmployeeRelationDTO;
import com.employee.dto.FamilyMemberInOrgDTO;
import com.employee.dto.BankContactDTO;
import com.employee.dto.QualificationDetailsDto;
import com.employee.dto.PreviousEmployerInfoDTO;
import com.employee.entity.BankDetails;
import com.employee.entity.Campus;
import com.employee.entity.CampusContact;
import com.employee.entity.EmpChequeDetails;
import com.employee.entity.EmpDocType;
import com.employee.entity.EmpDocuments;
import com.employee.entity.EmpExperienceDetails;
import com.employee.entity.EmpFamilyDetails;
import com.employee.entity.EmpQualification;
import com.employee.entity.EmpSubject;
import com.employee.entity.EmpaddressInfo;
import com.employee.entity.Employee;
import com.employee.entity.EmployeeBasicInfoView;
import com.employee.entity.Organization;
import com.employee.exception.ResourceNotFoundException;
import com.employee.repository.BankDetailsRepository;
import com.employee.repository.CampusContactRepository;
import com.employee.repository.EmpBasicInfoViewRepo;
import com.employee.repository.EmpChequeDetailsRepository;
import com.employee.repository.EmpDocTypeRepository;
import com.employee.repository.EmpDocumentsRepository;
import com.employee.repository.EmpExperienceDetailsRepository;
import com.employee.repository.EmpFamilyDetailsRepository;
import com.employee.repository.EmpQualificationRepository;
import com.employee.repository.EmpSubjectRepository;
import com.employee.repository.EmpaddressInfoRepository;
import com.employee.repository.EmployeeRepository;
import com.employee.repository.OrganizationRepository;

import jakarta.transaction.Transactional;

@Service
public class HREmpDetlService {

    private static final Logger logger = LoggerFactory.getLogger(HREmpDetlService.class);
    @Autowired
    private EmpFamilyDetailsRepository empFamilyDetailsRepository;

    @Autowired
    EmpaddressInfoRepository empAddressInfoRepository;
    @Autowired
    EmployeeRepository employeeRepository;
    @Autowired
    BankDetailsRepository bankDetailsRepository;
    @Autowired
    EmpSubjectRepository empSubjectRepository;
    @Autowired
    EmpExperienceDetailsRepository empExperienceDetailsRepository;
    @Autowired
    CampusContactRepository campusContactRepository;
    @Autowired
    EmpBasicInfoViewRepo employeeBasicInfoRepository;
    @Autowired
    EmpChequeDetailsRepository empChequeDetailsRepository;
    @Autowired
    EmpQualificationRepository empQualificationRepository;
    @Autowired
    OrganizationRepository organizationRepository;
    @Autowired
    private EmpDocumentsRepository empDocumentsRepository;
    @Autowired
    private EmpDocTypeRepository empDocTypeRepository;

    public List<EmpFamilyDetailsDTO> getFamilyMembersByPayrollId(String payrollId) {
        // Step 1: Find employee to get emp_id for photo lookup
        Employee employee = employeeRepository.findByPayRollId(payrollId)
                .orElseThrow(() -> new RuntimeException("Employee not found for payrollId: " + payrollId));

        // Step 2: Fetch family photo path (Shared attribute for the employee)
        String familyPhotoPath = empDocumentsRepository
                .findByEmpIdAndDocName(employee.getEmp_id(), "Family Group Photo")
                .stream()
                .findFirst()
                .map(doc -> doc.getDoc_path())
                .orElse(null);

        // Step 3: Fetch family records
        List<EmpFamilyDetails> familyList = empFamilyDetailsRepository.findByEmp_id_PayrollIdAndIsActive(payrollId, 1);

        return familyList.stream().map(fam -> {
            EmpFamilyDetailsDTO dto = new EmpFamilyDetailsDTO();
            dto.setEmpFamilyDetlId(fam.getEmp_family_detl_id());
            dto.setFullName(fam.getFullName());
            dto.setAdhaarNo(fam.getAdhaarNo());
            dto.setOccupation(fam.getOccupation());
            if (fam.getGender_id() != null) {
                dto.setGender(fam.getGender_id().getGenderName());
                dto.setGenderId(fam.getGender_id().getGender_id());
            }
            if (fam.getBlood_group_id() != null) {
                dto.setBloodGroup(fam.getBlood_group_id().getBloodGroupName());
                dto.setBloodGroupId(fam.getBlood_group_id().getBloodGroupId());
            }
            dto.setNationality(fam.getNationality());
            if (fam.getRelation_id() != null) {
                dto.setRelation(fam.getRelation_id().getStudentRelationType());
                dto.setRelationId(fam.getRelation_id().getStudentRelationId());
            }
            dto.setIsDependent(fam.getIs_dependent());
            dto.setIsLate(fam.getIs_late());
            dto.setEmail(fam.getEmail());
            dto.setContactNumber(fam.getContact_no() != null ? fam.getContact_no() : 0L);

            // Map new fields
            dto.setDateOfBirth(fam.getDate_of_birth());
            dto.setIsSriChaitanyaEmp(fam.getIs_sri_chaitanya_emp());
            if (fam.getParent_emp_id() != null) {
                Employee parent = fam.getParent_emp_id();
                String pId = parent.getPayRollId();
                if (pId == null || pId.isEmpty()) {
                    pId = parent.getTempPayrollId();
                }
                dto.setParentEmpPayrollId(pId);
            }
            dto.setFamilyPhotoPath(familyPhotoPath);

            return dto;
        }).collect(Collectors.toList());
    }

    public Map<String, List<AddressResponseDTO>> getAddressByPayrollIdGrouped(String payrollId) {
        List<EmpaddressInfo> addresses = empAddressInfoRepository.findByEmpId_PayrollId(payrollId);

        // Convert entity list into DTO list grouped by address type
        return addresses.stream()
                .map(address -> new AddressResponseDTO(
                        address.getAddrs_type(),
                        address.getHouse_no(),
                        address.getLandmark(),
                        address.getPostal_code(),
                        address.getCity_id() != null ? address.getCity_id().getCityName() : null,
                        address.getDistrict_id() != null ? address.getDistrict_id().getDistrictName()
                                : (address.getCity_id() != null && address.getCity_id().getDistrict() != null
                                        ? address.getCity_id().getDistrict().getDistrictName()
                                        : null),
                        address.getState_id() != null ? address.getState_id().getStateName() : null,
                        address.getCountry_id() != null ? address.getCountry_id().getCountryName() : null,
                        address.getEmrg_contact_no(),
                        address.getIs_per_and_curr()))
                .collect(Collectors.groupingBy(
                        AddressResponseDTO::getAddressType,
                        LinkedHashMap::new,
                        Collectors.toList()));
    }

    public List<FamilyMemberInOrgDTO> getFamilyMembersInOrganization(String payrollId) {
        // Step 1: Get the main employee
        Employee employee = employeeRepository.findByPayRollId(payrollId)
                .orElseThrow(() -> new RuntimeException("Employee not found for payrollId: " + payrollId));

        // Step 2: Get family records
        List<EmpFamilyDetails> familyList = empFamilyDetailsRepository.findByEmp_id_EmpId(employee.getEmp_id());

        // Step 3: Filter and map to DTO
        return familyList.stream()
                .filter(f -> f.getIs_sri_chaitanya_emp() == 1 && f.getParent_emp_id() != null)
                .map(f -> {
                    Employee parent = f.getParent_emp_id();
                    return new FamilyMemberInOrgDTO(
                            parent.getFirst_name() + " " + parent.getLast_name(),
                            parent.getPayRollId(),
                            parent.getEmail(),
                            parent.getPrimary_mobile_no(),
                            parent.getDesignation() != null ? parent.getDesignation().getDesignation_name() : null);
                })
                .collect(Collectors.toList());
    }

    // 🔹 Manager Details
    public EmployeeRelationDTO getManagerDetails(String payrollId) {
        Employee employee = employeeRepository.findByPayRollId(payrollId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        Employee manager = employee.getEmployee_manager_id();
        return mapToDTO(manager);
    }

    // 🔹 Reference Details
    public EmployeeRelationDTO getReferenceDetails(String payrollId) {
        Employee employee = employeeRepository.findByPayRollId(payrollId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        Employee reference = employee.getEmployee_reference();
        return mapToDTO(reference);
    }

    // 🔹 Hired By Details
    public EmployeeRelationDTO getHiredByDetails(String payrollId) {
        Employee employee = employeeRepository.findByPayRollId(payrollId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        Employee hiredBy = employee.getEmployee_hired();
        return mapToDTO(hiredBy);
    }

    // 🔹 Reporting Manager Details
    public EmployeeRelationDTO getReportingManagerDetails(String payrollId) {
        Employee employee = employeeRepository.findByPayRollId(payrollId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        Employee reportingManager = employee.getEmployee_reporting_id();
        return mapToDTO(reportingManager);
    }

    // 🔹 Bank contacts (Bank Manager & CRO) by payrollId
    public List<BankContactDTO> getBankContactsByPayrollId(String payrollId) {
        List<BankDetails> banks = bankDetailsRepository.findByEmployeePayrollId(payrollId);
        return banks.stream().map(b -> {
            BankContactDTO dto = new BankContactDTO();
            dto.setAccType(b.getAccType());
            dto.setBankHolderName(b.getBankHolderName());
            dto.setBankManagerName(b.getBankManagerName());
            dto.setBankManagerContactNo(b.getBankManagerContactNo());
            dto.setBankManagerEmail(b.getBankManagerEmail());
            dto.setCustomerRelationshipOfficerName(b.getCustomerRelationshipOfficerName());
            dto.setCustomerRelationshipOfficerContactNo(b.getCustomerRelationshipOfficerContactNo());
            dto.setCustomerRelationshipOfficerEmail(b.getCustomerRelationshipOfficerEmail());
            return dto;
        }).collect(Collectors.toList());
    }

    // 🔹 Utility Mapper
    private EmployeeRelationDTO mapToDTO(Employee emp) {
        if (emp == null)
            return null;

        return new EmployeeRelationDTO(
                emp.getFirst_name() + " " + emp.getLast_name(),
                emp.getPayRollId(),
                emp.getEmail(),
                emp.getPrimary_mobile_no(),
                emp.getDesignation() != null ? emp.getDesignation().getDesignation_name() : null);
    }

    public EmployeeBankDetailsResponseDTO getBankDetailsByPayrollId(String payrollId) {

        // ✅ 1. Find employee
        Optional<Employee> employeeOpt = employeeRepository.findByPayRollId(payrollId);
        if (employeeOpt.isEmpty()) {
            throw new RuntimeException("Employee not found for payrollId: " + payrollId);
        }

        Employee employee = employeeOpt.get();

        // ✅ 2. Fetch active bank details
        List<BankDetails> bankDetailsList = bankDetailsRepository.findActiveBankDetailsByEmpId(employee.getEmp_id());

        if (bankDetailsList.isEmpty()) {
            throw new RuntimeException("No bank details found for payrollId: " + payrollId);
        }

        BankInfoGetDTO personalBankInfo = null;
        BankInfoGetDTO salaryAccountInfo = null;

        // ✅ 3. Map entity → DTO
        for (BankDetails bank : bankDetailsList) {
            BankInfoGetDTO dto = new BankInfoGetDTO();
            dto.setPaymentType(bank.getEmpPaymentType() != null ? bank.getEmpPaymentType().getPayment_type() : null);
            dto.setBankName(bank.getBankName());
            dto.setBankBranch(bank.getBankBranch());
            dto.setAccountHolderName(bank.getBankHolderName());
            dto.setAccountNumber(bank.getAccNo());
            dto.setIfscCode(bank.getIfscCode());
            // Set payableAt from BankDetails entity
            dto.setPayableAt(bank.getPayableAt());

            // Map contact fields to DTO

            // Note: isSalaryLessThan40000 calculation removed as netPayable field no longer
            // exists
            // This information should come from BankInfoDTO.salaryLessThan40000 if needed
            // ✅ Classify based on accType
            if ("SALARY".equalsIgnoreCase(bank.getAccType()) || "SALARY ACCOUNT".equalsIgnoreCase(bank.getAccType())) {
                // Note: isSalaryLessThan40000 calculation removed as netPayable field no longer
                // exists
                // This information should come from BankInfoDTO.salaryLessThan40000 if needed
                dto.setIsSalaryLessThan40000("Yes");

                // Map Bank Manager & CRO Details
                dto.setBankManagerName(bank.getBankManagerName());
                dto.setBankManagerContactNo(bank.getBankManagerContactNo());
                dto.setBankManagerEmail(bank.getBankManagerEmail());
                dto.setCustomerRelationshipOfficerName(bank.getCustomerRelationshipOfficerName());
                dto.setCustomerRelationshipOfficerContactNo(bank.getCustomerRelationshipOfficerContactNo());
                dto.setCustomerRelationshipOfficerEmail(bank.getCustomerRelationshipOfficerEmail());

                salaryAccountInfo = dto;
            } else {
                dto.setIsSalaryLessThan40000("N/A");
                personalBankInfo = dto;
            }
        }

        // ✅ 4. Build final DTO response
        EmployeeBankDetailsResponseDTO response = new EmployeeBankDetailsResponseDTO();
        response.setPersonalBankInfo(personalBankInfo);
        response.setSalaryAccountInfo(salaryAccountInfo);

        return response;
    }

    // public EmployeeCurrentInfoDTO getCurrentInfoByPayrollId(String payrollId) {
    //
    // // ✅ Use your existing Optional<Employee> method
    // Optional<Employee> optionalEmp =
    // employeeRepository.findByPayRollId(payrollId);
    //
    // if (optionalEmp.isEmpty()) {
    // throw new RuntimeException("Employee not found for payrollId: " + payrollId);
    // }
    //
    // Employee emp = optionalEmp.get();
    //
    // // Fetch subjects assigned to this employee
    // List<EmpSubject> empSubjects =
    // empSubjectRepository.findActiveSubjectsByEmpId(emp.getEmp_id());
    //
    // List<SubjectInfo> subjectInfoList = empSubjects.stream()
    // .map(sub -> new SubjectInfo(
    // // 1. Subject Name
    // sub.getSubject_id() != null ? sub.getSubject_id().getSubject_name() : null,
    //
    // // 2. Agreed Per Week (Mapped from your single DB column)
    // sub.getAgree_no_period()
    // ))
    // .collect(Collectors.toList());
    //
    // // Prepare final DTO
    // EmployeeCurrentInfoDTO dto = new EmployeeCurrentInfoDTO();
    // dto.setEmployeeName(emp.getFirst_name() + " " + emp.getLast_name());
    // dto.setDateOfJoining(emp.getDate_of_join());
    // dto.setHiredBy(emp.getEmployee_hired() != null
    // ? emp.getEmployee_hired().getFirst_name() + " " +
    // emp.getEmployee_hired().getLast_name()
    // : null);
    // dto.setReferredBy(emp.getEmployee_reference() != null
    // ? emp.getEmployee_reference().getFirst_name() + " " +
    // emp.getEmployee_reference().getLast_name()
    // : null);
    // dto.setSubjects(subjectInfoList);
    //
    // return dto;
    // }

    public EmployeeCurrentInfoDTO getCurrentInfoByPayrollId(String payrollId) {

        // 1. Fetch Employee
        Optional<Employee> optionalEmp = employeeRepository.findByPayRollId(payrollId);
        if (optionalEmp.isEmpty()) {
            throw new RuntimeException("Employee not found");
        }
        Employee emp = optionalEmp.get();

        // 2. Prepare DTO
        EmployeeCurrentInfoDTO dto = new EmployeeCurrentInfoDTO();
        dto.setEmployeeName(emp.getFirst_name() + " " + emp.getLast_name());
        dto.setDateOfJoining(emp.getDate_of_join());
        dto.setHiredBy(emp.getEmployee_hired() != null ? emp.getEmployee_hired().getFirst_name() : null);
        dto.setReferredBy(emp.getEmployee_reference() != null ? emp.getEmployee_reference().getFirst_name() : null);

        // 3. Fetch Subjects and Set the FIRST one found
        List<EmpSubject> empSubjects = empSubjectRepository.findActiveSubjectsByEmpId(emp.getEmp_id());

        if (!empSubjects.isEmpty()) {
            EmpSubject firstSubject = empSubjects.get(0); // ✅ Get the first subject

            dto.setSubjectName(
                    firstSubject.getSubject_id() != null ? firstSubject.getSubject_id().getSubject_name() : null);
            dto.setAgreedPerWeek(firstSubject.getAgree_no_period());
        } else {
            // Optional: Handle case where no subject exists
            dto.setSubjectName(null);
            dto.setAgreedPerWeek(0);
        }

        return dto;
    }

    public List<EmpExperienceDetailsDTO> getEmployeeExperienceByPayrollId(String payrollId) {

        // ✅ Step 1: Find employee
        Optional<Employee> empOpt = employeeRepository.findByPayRollId(payrollId);
        if (empOpt.isEmpty()) {
            throw new RuntimeException("Employee not found for payrollId: " + payrollId);
        }

        Employee employee = empOpt.get();

        // ✅ Step 2: Fetch experience records
        List<EmpExperienceDetails> expList = empExperienceDetailsRepository
                .findActiveByEmployeeId(employee.getEmp_id());

        if (expList.isEmpty()) {
            throw new RuntimeException("No experience records found for employee: " + payrollId);
        }

        // ✅ Step 3: Convert to DTO list
        // return expList.stream().map(exp -> {
        // EmpExperienceDetailsDTO dto = new EmpExperienceDetailsDTO();
        // dto.setCompanyName(exp.getPre_organigation_name()));
        // dto.setDesignation(exp.getDesignation());
        // dto.setFromDate(exp.getDateOfJoin().toLocalDate());
        // dto.setToDate(exp.getDateOfLeave().toLocalDate());
        // dto.setLeavingReason(exp.getLeavingReason());
        // dto.setCompanyAddress(exp.getCompanyAddr());
        // dto.setNatureOfDuties(exp.getNatureOfDuties());
        //
        // BigDecimal ctc = BigDecimal.valueOf(exp.getGrossSalary());
        // dto.setCtc(ctc);
        // dto.setGrossSalaryPerMonth(ctc.divide(BigDecimal.valueOf(12), 2,
        // BigDecimal.ROUND_HALF_UP));
        //
        // return dto;
        // }).collect(Collectors.toList());
        return expList.stream().map(exp -> {
            EmpExperienceDetailsDTO dto = new EmpExperienceDetailsDTO();

            dto.setCompanyName(exp.getPre_organigation_name());
            dto.setDesignation(exp.getDesignation());
            dto.setFromDate(exp.getDate_of_join().toLocalDate());
            dto.setToDate(exp.getDate_of_leave().toLocalDate());
            dto.setLeavingReason(exp.getLeaving_reason());
            dto.setCompanyAddressLine1(exp.getCompany_addr()); // Set the new field
            dto.setCompanyAddress(exp.getCompany_addr());
            dto.setNatureOfDuties(exp.getNature_of_duties());

            // Interpreting gross_salary as monthly salary to match POST structure
            BigDecimal monthlySalary = BigDecimal.valueOf(exp.getGross_salary());
            dto.setGrossSalaryPerMonth(monthlySalary);
            dto.setCtc(monthlySalary.multiply(BigDecimal.valueOf(12))); // Annual CTC

            // Fetch and map associated documents
            List<EmpDocuments> docEntities = empDocumentsRepository.findByExperienceId(exp.getEmp_exp_detl_id());
            List<PreviousEmployerInfoDTO.ExperienceDocumentDTO> docDTOs = docEntities.stream()
                    .map(doc -> {
                        PreviousEmployerInfoDTO.ExperienceDocumentDTO d = new PreviousEmployerInfoDTO.ExperienceDocumentDTO();
                        d.setDocPath(doc.getDoc_path());
                        if (doc.getEmp_doc_type_id() != null) {
                            d.setDocTypeId(doc.getEmp_doc_type_id().getDoc_type_id());
                        }
                        return d;
                    })
                    .collect(Collectors.toList());
            dto.setDocuments(docDTOs);

            return dto;
        }).collect(Collectors.toList());
    }

    public Optional<EmployeeBasicInfoView> getBasicInfoByPayrollId(String payrollId) {

        // ==== NEW, MORE EFFICIENT LOGIC ====
        // We no longer need to find the Employee first.
        // We can query the EmployeeBasicInfoRepository directly.
        return employeeBasicInfoRepository.findByPayrollId(payrollId);
    }

    // public EmployeeAgreementDetailsDto getChequeDetailsByPayrollId(String
    // payrollId) {
    // Optional<Employee> empOpt = employeeRepository.findByPayrollId(payrollId);
    // if (empOpt.isEmpty()) {
    // throw new RuntimeException("Employee not found for payrollId: " + payrollId);
    // }
    //
    // Employee emp = empOpt.get();
    // List<EmpChequeDetails> cheques =
    // empChequeDetailsRepository.findActiveChequesByEmpId(emp.getEmpId());
    //
    // List<ChequeDetailsDto> chequeDtos = cheques.stream()
    // .map(chq -> new ChequeDetailsDto(
    // chq.getChequeNo(),
    // chq.getChequeBankName(),
    // chq.getChequeBankIfscCode()
    // ))
    // .collect(Collectors.toList());
    //
    // EmployeeAgreementDetailsDto response = new EmployeeAgreementDetailsDto();
    // response.setAgreementCompany(
    // emp.getAgreementOrgId() != null ?
    // emp.getAgreementOrgId().getOrganizationName() : "N/A"
    // );
    // response.setAgreementType(emp.getAgreementType());
    // response.setNoOfCheques(chequeDtos.size());
    // response.setCheques(chequeDtos);
    //
    // return response;
    // }

    //
    //
    //
    // public EmployeeAgreementDetailsDto getChequeDetailsByPayrollId(String
    // payrollId) {
    //
    // // --- FIX 1 ---
    // // Renamed to 'findByPayRollId' to match your Employee entity's 'payRollId'
    // field
    // Optional<Employee> empOpt = employeeRepository.findByPayRollId(payrollId);
    //
    // if (empOpt.isEmpty()) {
    // throw new RuntimeException("Employee not found for payrollId: " + payrollId);
    // }
    //
    // Employee emp = empOpt.get();
    //
    // // --- FIX 2 ---
    // // Changed 'emp.getEmpId()' to 'emp.getEmp_id()' to match your Employee
    // entity
    // List<EmpChequeDetails> cheques =
    // empChequeDetailsRepository.findActiveChequesByEmpId(emp.getEmp_id());
    //
    // // This part was correct and matches your ChequeDetailsDto
    // List<ChequeDetailsDto> chequeDtos = cheques.stream()
    // .map(chq -> new ChequeDetailsDto(
    // chq.getChequeNo(),
    // chq.getChequeBankName(),
    // chq.getChequeBankIfscCode()
    // ))
    // .collect(Collectors.toList());
    //
    // EmployeeAgreementDetailsDto response = new EmployeeAgreementDetailsDto();
    // String orgName = "N/A";
    // Integer orgId = emp.getAgreement_org_id();
    // if (orgId != null) {
    // // 2. Use the findById method to get the Organization
    // Optional<Organization> orgOpt = organizationRepository.findById(orgId);
    //
    // if (orgOpt.isPresent()) {
    // // 3. Get the name from the found object
    // orgName = orgOpt.get().getOrganizationName();
    // }
    // }
    // response.setAgreementCompany(orgName);
    //
    // // --- FIX 4 ---
    // response.setAgreementType(emp.getAgreement_type());
    //
    // // These fields were correct
    // response.setNoOfCheques(chequeDtos.size());
    // response.setCheques(chequeDtos);
    //
    // return response;
    //
    //
    // }

    // Assuming this is inside a Service class that has @Autowired
    // employeeRepository,
    // empChequeDetailsRepository, AND organizationRepository.

    @Transactional
    public EmployeeAgreementDetailsDto getChequeDetailsByPayrollId(String payrollId) {

        // 1. Find employee using correct field name payRollId (capital R)
        Optional<Employee> empOpt = employeeRepository.findByPayRollId(payrollId);

        if (empOpt.isEmpty()) {
            throw new RuntimeException("Employee not found for payrollId: " + payrollId);
        }

        Employee emp = empOpt.get();

        // 2. Fetch Cheques using correct getter emp.getEmp_id()
        List<EmpChequeDetails> cheques = empChequeDetailsRepository.findActiveChequesByEmpId(emp.getEmp_id());

        // 3. Map Cheque entities to DTOs
        List<ChequeDetailsDto> chequeDtos = cheques.stream()
                .map(chq -> {
                    ChequeDetailsDto d = new ChequeDetailsDto();
                    d.setChequeNo(chq.getChequeNo());
                    d.setChequeBank(chq.getChequeBankName());
                    d.setIfscCode(chq.getChequeBankIfscCode());

                    // Fetch path from EmpDocuments linked to this cheque via path prefix
                    String linkPrefix = "CHEQUE_LINK_" + chq.getEmpChequeDetailsId() + "_";
                    String searchPattern = "%" + linkPrefix + "%";
                    empDocumentsRepository.findByEmpIdAndPathPattern(emp.getEmp_id(), searchPattern)
                            .ifPresent(doc -> {
                                String path = doc.getDoc_path();
                                if (path != null && path.startsWith(linkPrefix)) {
                                    d.setChequePath(path.substring(linkPrefix.length()));
                                } else {
                                    d.setChequePath(path);
                                }
                            });

                    return d;
                })
                .collect(Collectors.toList());

        EmployeeAgreementDetailsDto response = new EmployeeAgreementDetailsDto();

        // 4. Resolve Organization Name using the relationship
        String orgName = "N/A";
        Organization org = emp.getAgreement_org_id(); // Now returns Organization entity

        if (org != null) {
            orgName = org.getOrganizationName();
        }

        // 5. Set response fields using correct getters
        response.setAgreementCompany(orgName);
        response.setAgreementType(emp.getAgreement_type()); // Correctly uses emp.getAgreement_type()

        // Fetch path for the agreement document
        empDocumentsRepository.findByEmpIdAndDocName(emp.getEmp_id(), "Agreement")
                .stream()
                .findFirst()
                .ifPresent(doc -> response.setAgreementPath(doc.getDoc_path()));

        // 6. Final DTO population
        response.setNoOfCheques(chequeDtos.size());
        response.setCheques(chequeDtos);

        return response;
    }

    public EmployeeCampusInfoDTO getEmployeeCampusInfo(String payrollId) {

        // 1. Fetch the employee by payrollId
        Employee employee = employeeRepository.findByPayRollId(payrollId)
                .orElseThrow(() -> new RuntimeException("Employee not found with payrollId: " + payrollId));

        Campus campus = employee.getCampus_id();
        if (campus == null) {
            throw new RuntimeException("Employee is not associated with any campus.");
        }

        EmployeeCampusInfoDTO dto = new EmployeeCampusInfoDTO();

        // 2. Populate Top Card (Current Campus Info)
        dto.setCampusName(campus.getCampusName());
        dto.setCampusCode(campus.getCmps_code());
        dto.setCampusType(campus.getCmps_type());

        if (employee.getDesignation() != null) {
            dto.setDesignationName(employee.getDesignation().getDesignation_name()); // Assumes getter
        }
        if (employee.getWorkingMode_id() != null) {
            dto.setWorkMode(employee.getWorkingMode_id().getWork_mode_type()); // Assumes getter
        }
        if (employee.getJoin_type_id() != null) {
            dto.setJoiningAs(employee.getJoin_type_id().getJoin_type()); // Assumes getter
        }
        if (employee.getEmployee_replaceby_id() != null) {
            dto.setReplacementEmployeeName(
                    employee.getEmployee_replaceby_id().getFirst_name() + " " +
                            employee.getEmployee_replaceby_id().getLast_name());
        }

        // 3. Populate Principal Card (Find by Designation "Principal")
        Optional<CampusContact> principalOpt = campusContactRepository
                .findByCmpsIdAndDesignation(campus, "PRINCIPAL"); // <-- Assuming designation is "Principal"

        if (principalOpt.isPresent()) {
            CampusContact principal = principalOpt.get();
            CampusPrincipalDTO pDto = new CampusPrincipalDTO(
                    principal.getEmpName(),
                    principal.getDesignation(),
                    principal.getContactNo(),
                    principal.getEmail());
            dto.setPrincipalInfo(pDto);
        }

        // 4. Populate Campus Address Card
        // CampusAddressDTO aDto = new CampusAddressDTO();
        // aDto.setCampusName(campus.getCampusName());
        //
        // if (campus.getCity() != null) {
        // aDto.setCity(campus.getCity().getName()); // Assumes getter
        // }
        // if (campus.getState() != null) {
        // aDto.setState(campus.getState().getName()); // Assumes getter
        // }
        // if (campus.getZone() != null) {
        // aDto.setZone(campus.getZone().getName()); // Assumes getter
        // }
        // dto.setAddressInfo(aDto);

        return dto;
    }

    public List<String> getQualificationNamesByPayrollId(String payrollId) {
        Optional<Employee> employee = employeeRepository.findByPayRollId(payrollId);

        List<EmpQualification> qualifications = empQualificationRepository
                .findActiveQualificationsByEmployee(employee);

        return qualifications.stream()
                .map(eq -> eq.getQualification_id().getQualification_name()) // Assumes getQualificationName()
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * API 2: Gets the details for a specific qualification name.
     */

    private String formatQualificationName(String dbValue) {
        if (dbValue == null)
            return null;

        switch (dbValue) {
            case "10TH_SSC_MATRICULATION":
                return "SSC";
            case "+2_INTERMEDIATE_PUC":
                return "Intermediate";
            case "DEGREE_GRADUATE":
                return "Degree";
            case "UNDER GRADUATE":
                return "Under Graduate";
            case "MASTER DEGREE_PG":
                return "Master Degree";
            case "DIPLOMA":
                return "Diploma";
            case "M.PHIL":
                return "M.Phil";
            case "P.H.D":
                return "Ph.D";
            case "DOCTORATE":
                return "Doctorate";
            case "OTHERS":
                return "Others";
            default:
                // fallback – convert underscores to spaces
                return dbValue.replace("_", " ");
        }
    }

    public List<QualificationDetailsDto> getQualificationsByPayrollId(String payrollId) {

        List<EmpQualification> qualifications = empQualificationRepository.findByEmployeePayrollId(payrollId);

        return qualifications.stream().map(eq -> {

            QualificationDetailsDto dto = new QualificationDetailsDto();

            dto.setEmpQualificationId(eq.getEmp_qualification_id());

            if (eq.getQualification_id() != null) {
                dto.setQualificationId(eq.getQualification_id().getQualification_id());
                // Convert qualification name to user-friendly text
                String rawQualificationName = eq.getQualification_id().getQualification_name();

                dto.setQualificationName(
                        formatQualificationName(rawQualificationName));
            }

            if (eq.getQualification_degree_id() != null) {
                dto.setQualificationDegreeId(eq.getQualification_degree_id().getQualification_degree_id());
                // Degree name (assuming already readable)
                dto.setQualificationDegree(
                        eq.getQualification_degree_id().getDegree_name());
            }

            dto.setPassedoutYear(eq.getPassedout_year());
            dto.setSpecialization(eq.getSpecialization());
            dto.setInstitute(eq.getInstitute());
            dto.setUniversity(eq.getUniversity());
            dto.setIsActive(eq.getIs_active());

            // Fetch certificate path from EmpDocuments based on doc_type_id
            if (eq.getEmp_id() != null && eq.getQualification_id() != null) {
                Integer qId = eq.getQualification_id().getQualification_id();

                // Find documents for this employee and qualification ID
                List<EmpDocuments> docs = empDocumentsRepository.findByEmpIdAndDocTypeId(
                        eq.getEmp_id().getEmp_id(), qId);

                // Find the first document that is categorized as an "Educational Document"
                String path = docs.stream()
                        .filter(d -> d.getEmp_doc_type_id() != null &&
                                "Educational Document".equalsIgnoreCase(d.getEmp_doc_type_id().getDoc_type()))
                        .map(EmpDocuments::getDoc_path)
                        .findFirst()
                        .orElse(null);

                dto.setCertificatePath(path);
            } else {
                dto.setCertificatePath(null);
            }

            return dto;

        }).collect(Collectors.toList());
    }

    /**
     * Retrieves the comprehensive status (Required, Uploaded, Missing) of
     * educational documents
     * for an employee based on their specific qualifications.
     * * @param payrollId The employee's payroll ID.
     * 
     * @return DTO containing lists of required, uploaded, and missing educational
     *         documents.
     * @throws ResourceNotFoundException if the employee or their qualifications are
     *                                   not found.
     */
    public EducationalDocumentStatusDTO getEducationalDocumentsStatusByPayrollId(String payrollId) {
        logger.info("Getting educational documents status for payroll_id: {}", payrollId);

        Employee employee = employeeRepository.findByPayrollId(payrollId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with payroll_id: " + payrollId));

        Integer empId = employee.getEmp_id();
        logger.info("Found employee - emp_id: {}, payroll_id: {}, temp_payroll_id: {}",
                empId, employee.getPayRollId(), employee.getTempPayrollId());

        List<EmpQualification> employeeQualifications = empQualificationRepository.findByEmpIdAndIsActive(empId);

        if (employeeQualifications.isEmpty()) {
            throw new ResourceNotFoundException(
                    "Employee (emp_id: " + empId + ") does not have any qualifications in EmpQualification table");
        }

        Set<Integer> employeeQualificationIds = employeeQualifications.stream()
                .map(eq -> eq.getQualification_id().getQualification_id())
                .collect(Collectors.toSet());

        logger.info("Employee has qualifications with IDs: {}", employeeQualificationIds);

        Integer highestQualificationId = employee.getQualification_id() != null
                ? employee.getQualification_id().getQualification_id()
                : employeeQualificationIds.stream().mapToInt(Integer::intValue).max().orElse(0);

        String qualificationName = employee.getQualification_id() != null
                ? employee.getQualification_id().getQualification_name()
                : "Multiple Qualifications";

        List<EmpDocType> educationalDocTypes = empDocTypeRepository.findByDocTypeAndIsActive("Educational Document");
        if (educationalDocTypes.isEmpty()) {
            logger.warn("No active Educational Document types found, trying all records");
            educationalDocTypes = empDocTypeRepository.findByDocType("Educational Document");
        }

        educationalDocTypes.sort((a, b) -> Integer.compare(a.getDoc_type_id(), b.getDoc_type_id()));

        List<EmpDocType> requiredDocTypes = new ArrayList<>();
        for (Integer qualId : employeeQualificationIds) {
            EmpDocType matchingDoc = educationalDocTypes.stream()
                    .filter(dt -> dt.getDoc_type_id() == qualId)
                    .findFirst()
                    .orElse(null);
            if (matchingDoc != null && !requiredDocTypes.contains(matchingDoc)) {
                requiredDocTypes.add(matchingDoc);
            }
        }

        requiredDocTypes.sort((a, b) -> Integer.compare(a.getDoc_type_id(), b.getDoc_type_id()));

        List<Integer> requiredDocTypeIds = requiredDocTypes.stream()
                .mapToInt(EmpDocType::getDoc_type_id)
                .boxed()
                .collect(Collectors.toList());

        List<EmpDocuments> uploadedDocs = empDocumentsRepository.findByEmpIdAndDocTypeIds(empId, requiredDocTypeIds);
        Set<Integer> uploadedDocTypeIds = uploadedDocs.stream()
                .map(doc -> doc.getEmp_doc_type_id().getDoc_type_id())
                .collect(Collectors.toSet());

        EducationalDocumentStatusDTO response = new EducationalDocumentStatusDTO();
        response.setEmpId(empId);
        response.setPayrollId(employee.getPayRollId());
        response.setTempPayrollId(employee.getTempPayrollId());
        response.setQualificationId(highestQualificationId);
        response.setQualificationName(qualificationName);

        List<EducationalDocumentStatusDTO.DocumentStatusDTO> requiredDocumentsList = new ArrayList<>();
        List<EducationalDocumentStatusDTO.DocumentStatusDTO> uploadedDocumentsList = new ArrayList<>();
        List<EducationalDocumentStatusDTO.DocumentStatusDTO> missingDocumentsList = new ArrayList<>();

        for (EmpDocType docType : requiredDocTypes) {
            EducationalDocumentStatusDTO.DocumentStatusDTO docStatus = new EducationalDocumentStatusDTO.DocumentStatusDTO();
            docStatus.setDocTypeId(docType.getDoc_type_id());
            docStatus.setDocName(docType.getDoc_name());
            docStatus.setDocType(docType.getDoc_type());

            boolean isUploaded = uploadedDocTypeIds.contains(docType.getDoc_type_id());
            docStatus.setIsUploaded(isUploaded);

            if (isUploaded) {
                EmpDocuments uploadedDoc = uploadedDocs.stream()
                        .filter(doc -> doc.getEmp_doc_type_id().getDoc_type_id() == docType.getDoc_type_id())
                        .findFirst()
                        .orElse(null);
                if (uploadedDoc != null) {
                    docStatus.setDocPath(uploadedDoc.getDoc_path());
                    docStatus.setCreatedBy(uploadedDoc.getCreated_by());
                    docStatus.setCreatedDate(uploadedDoc.getCreated_date());
                    docStatus.setUpdatedBy(uploadedDoc.getUpdated_by());
                    docStatus.setUpdatedDate(uploadedDoc.getUpdated_date());
                }
                uploadedDocumentsList.add(docStatus);
            } else {
                missingDocumentsList.add(docStatus);
            }
            requiredDocumentsList.add(docStatus);
        }

        response.setRequiredDocuments(requiredDocumentsList);
        response.setUploadedDocuments(uploadedDocumentsList);
        response.setMissingDocuments(missingDocumentsList);

        return response;
    }

    /**
     * Retrieves only the list of educational documents that have already been
     * uploaded by the employee.
     * * @param payrollId The employee's payroll ID.
     * 
     * @return List of uploaded educational documents.
     */
    public List<EducationalDocumentStatusDTO.DocumentStatusDTO> getUploadedEducationalDocuments(String payrollId) {
        logger.info("Getting uploaded educational documents for payroll_id: {}", payrollId);
        EducationalDocumentStatusDTO status = getEducationalDocumentsStatusByPayrollId(payrollId);
        return status.getUploadedDocuments();
    }

    /**
     * Retrieves only the list of educational documents that are missing and need to
     * be uploaded.
     * * @param payrollId The employee's payroll ID.
     * 
     * @return List of missing educational documents.
     */
    public List<EducationalDocumentStatusDTO.DocumentStatusDTO> getMissingEducationalDocuments(String payrollId) {
        logger.info("Getting missing educational documents for payroll_id: {}", payrollId);
        EducationalDocumentStatusDTO status = getEducationalDocumentsStatusByPayrollId(payrollId);
        return status.getMissingDocuments();
    }

    /**
     * Retrieves the comprehensive status (Required, Uploaded, Missing) of ID Proof
     * documents.
     * * @param payrollId The employee's payroll ID.
     * 
     * @return DTO containing lists of required, uploaded, and missing ID proof
     *         documents.
     * @throws ResourceNotFoundException if the employee is not found.
     */
    public EducationalDocumentStatusDTO getIdProofDocumentsStatusByPayrollId(String payrollId) {
        logger.info("Getting ID Proof documents status for payroll_id: {}", payrollId);

        Employee employee = employeeRepository.findByPayrollId(payrollId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with payroll_id: " + payrollId));

        Integer empId = employee.getEmp_id();
        logger.info("Found employee - emp_id: {}, payroll_id: {}", empId, employee.getPayRollId());

        List<EmpDocType> idProofDocTypes = empDocTypeRepository.findByDocTypeAndIsActive("ID Proof");
        if (idProofDocTypes.isEmpty()) {
            logger.warn("No active ID Proof Document types found, trying all records");
            idProofDocTypes = empDocTypeRepository.findByDocType("ID Proof");
        }

        idProofDocTypes.sort((a, b) -> Integer.compare(a.getDoc_type_id(), b.getDoc_type_id()));

        List<Integer> idProofDocTypeIds = idProofDocTypes.stream()
                .mapToInt(EmpDocType::getDoc_type_id)
                .boxed()
                .collect(Collectors.toList());

        List<EmpDocuments> uploadedDocs = empDocumentsRepository.findByEmpIdAndDocTypeIds(empId, idProofDocTypeIds);
        Set<Integer> uploadedDocTypeIds = uploadedDocs.stream()
                .map(doc -> doc.getEmp_doc_type_id().getDoc_type_id())
                .collect(Collectors.toSet());

        EducationalDocumentStatusDTO response = new EducationalDocumentStatusDTO();
        response.setEmpId(empId);
        response.setPayrollId(employee.getPayRollId());
        response.setTempPayrollId(employee.getTempPayrollId());

        List<EducationalDocumentStatusDTO.DocumentStatusDTO> requiredDocumentsList = new ArrayList<>();
        List<EducationalDocumentStatusDTO.DocumentStatusDTO> uploadedDocumentsList = new ArrayList<>();
        List<EducationalDocumentStatusDTO.DocumentStatusDTO> missingDocumentsList = new ArrayList<>();

        for (EmpDocType docType : idProofDocTypes) {
            EducationalDocumentStatusDTO.DocumentStatusDTO docStatus = new EducationalDocumentStatusDTO.DocumentStatusDTO();
            docStatus.setDocTypeId(docType.getDoc_type_id());
            docStatus.setDocName(docType.getDoc_name());
            docStatus.setDocType(docType.getDoc_type());

            boolean isUploaded = uploadedDocTypeIds.contains(docType.getDoc_type_id());
            docStatus.setIsUploaded(isUploaded);

            if (isUploaded) {
                EmpDocuments uploadedDoc = uploadedDocs.stream()
                        .filter(doc -> doc.getEmp_doc_type_id().getDoc_type_id() == docType.getDoc_type_id())
                        .findFirst()
                        .orElse(null);
                if (uploadedDoc != null) {
                    docStatus.setDocPath(uploadedDoc.getDoc_path());
                    docStatus.setCreatedBy(uploadedDoc.getCreated_by());
                    docStatus.setCreatedDate(uploadedDoc.getCreated_date());
                    docStatus.setUpdatedBy(uploadedDoc.getUpdated_by());
                    docStatus.setUpdatedDate(uploadedDoc.getUpdated_date());
                }
                uploadedDocumentsList.add(docStatus);
            } else {
                missingDocumentsList.add(docStatus);
            }
            requiredDocumentsList.add(docStatus);
        }

        response.setRequiredDocuments(requiredDocumentsList);
        response.setUploadedDocuments(uploadedDocumentsList);
        response.setMissingDocuments(missingDocumentsList);

        return response;
    }

    /**
     * Retrieves only the list of ID Proof documents that have already been
     * uploaded.
     * * @param payrollId The employee's payroll ID.
     * 
     * @return List of uploaded ID proof documents.
     */
    public List<EducationalDocumentStatusDTO.DocumentStatusDTO> getUploadedIdProofDocuments(String payrollId) {
        logger.info("Getting uploaded ID Proof documents for payroll_id: {}", payrollId);
        EducationalDocumentStatusDTO status = getIdProofDocumentsStatusByPayrollId(payrollId);
        return status.getUploadedDocuments();
    }

    /**
     * Retrieves only the list of ID Proof documents that are missing.
     * * @param payrollId The employee's payroll ID.
     * 
     * @return List of missing ID proof documents.
     */
    public List<EducationalDocumentStatusDTO.DocumentStatusDTO> getMissingIdProofDocuments(String payrollId) {
        logger.info("Getting missing ID Proof documents for payroll_id: {}", payrollId);
        EducationalDocumentStatusDTO status = getIdProofDocumentsStatusByPayrollId(payrollId);
        return status.getMissingDocuments();
    }

    /**
     * Retrieves the status of specific required documents (Personal, Previous
     * Employment, Gratuity).
     * * @param payrollId The employee's payroll ID.
     * 
     * @return DTO containing lists of required, uploaded, and missing specific
     *         documents.
     * @throws ResourceNotFoundException if the employee is not found.
     */
    public EducationalDocumentStatusDTO getSpecificDocumentsStatusByPayrollId(String payrollId) {
        logger.info("Getting specific documents status for payroll_id: {}", payrollId);

        Employee employee = employeeRepository.findByPayrollId(payrollId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with payroll_id: " + payrollId));

        Integer empId = employee.getEmp_id();
        logger.info("Found employee - emp_id: {}, payroll_id: {}", empId, employee.getPayRollId());

        List<EmpDocType> personalDocTypes = empDocTypeRepository.findByDocTypeAndIsActive("Personal Document");
        if (personalDocTypes.isEmpty()) {
            personalDocTypes = empDocTypeRepository.findByDocType("Personal Document");
        }

        Optional<EmpDocType> prevEmployeeDocOpt = empDocTypeRepository.findByDocNameAndIsActive("PrevEmployeeDoc", 1);
        if (prevEmployeeDocOpt.isEmpty()) {
            prevEmployeeDocOpt = empDocTypeRepository.findByDocName("PrevEmployeeDoc");
        }

        Optional<EmpDocType> gratuityDocOpt = empDocTypeRepository.findByDocNameAndIsActive("Gratuity", 1);
        if (gratuityDocOpt.isEmpty()) {
            gratuityDocOpt = empDocTypeRepository.findByDocName("Gratuity");
        }
        if (gratuityDocOpt.isEmpty()) {
            gratuityDocOpt = empDocTypeRepository.findByDocName("Gratuity From");
        }

        List<EmpDocType> requiredDocTypes = new ArrayList<>(personalDocTypes);
        if (prevEmployeeDocOpt.isPresent()) {
            requiredDocTypes.add(prevEmployeeDocOpt.get());
        }
        if (gratuityDocOpt.isPresent()) {
            requiredDocTypes.add(gratuityDocOpt.get());
        }

        requiredDocTypes.sort((a, b) -> Integer.compare(a.getDoc_type_id(), b.getDoc_type_id()));

        List<Integer> requiredDocTypeIds = requiredDocTypes.stream()
                .mapToInt(EmpDocType::getDoc_type_id)
                .boxed()
                .collect(Collectors.toList());

        List<EmpDocuments> uploadedDocs = empDocumentsRepository.findByEmpIdAndDocTypeIds(empId, requiredDocTypeIds);
        Set<Integer> uploadedDocTypeIds = uploadedDocs.stream()
                .map(doc -> doc.getEmp_doc_type_id().getDoc_type_id())
                .collect(Collectors.toSet());

        EducationalDocumentStatusDTO response = new EducationalDocumentStatusDTO();
        response.setEmpId(empId);
        response.setPayrollId(employee.getPayRollId());
        response.setTempPayrollId(employee.getTempPayrollId());

        List<EducationalDocumentStatusDTO.DocumentStatusDTO> requiredDocumentsList = new ArrayList<>();
        List<EducationalDocumentStatusDTO.DocumentStatusDTO> uploadedDocumentsList = new ArrayList<>();
        List<EducationalDocumentStatusDTO.DocumentStatusDTO> missingDocumentsList = new ArrayList<>();

        for (EmpDocType docType : requiredDocTypes) {
            EducationalDocumentStatusDTO.DocumentStatusDTO docStatus = new EducationalDocumentStatusDTO.DocumentStatusDTO();
            docStatus.setDocTypeId(docType.getDoc_type_id());
            docStatus.setDocName(docType.getDoc_name());
            docStatus.setDocType(docType.getDoc_type());

            boolean isUploaded = uploadedDocTypeIds.contains(docType.getDoc_type_id());
            docStatus.setIsUploaded(isUploaded);

            if (isUploaded) {
                EmpDocuments uploadedDoc = uploadedDocs.stream()
                        .filter(doc -> doc.getEmp_doc_type_id().getDoc_type_id() == docType.getDoc_type_id())
                        .findFirst()
                        .orElse(null);
                if (uploadedDoc != null) {
                    docStatus.setDocPath(uploadedDoc.getDoc_path());
                    docStatus.setCreatedBy(uploadedDoc.getCreated_by());
                    docStatus.setCreatedDate(uploadedDoc.getCreated_date());
                    docStatus.setUpdatedBy(uploadedDoc.getUpdated_by());
                    docStatus.setUpdatedDate(uploadedDoc.getUpdated_date());
                }
                uploadedDocumentsList.add(docStatus);
            } else {
                missingDocumentsList.add(docStatus);
            }
            requiredDocumentsList.add(docStatus);
        }

        response.setRequiredDocuments(requiredDocumentsList);
        response.setUploadedDocuments(uploadedDocumentsList);
        response.setMissingDocuments(missingDocumentsList);

        return response;
    }

    /**
     * Retrieves only the list of specific documents
     * (Personal/Gratuity/PrevEmployee) that have been uploaded.
     * * @param payrollId The employee's payroll ID.
     * 
     * @return List of uploaded specific documents.
     */
    public List<EducationalDocumentStatusDTO.DocumentStatusDTO> getUploadedSpecificDocuments(String payrollId) {
        logger.info("Getting uploaded specific documents for payroll_id: {}", payrollId);
        EducationalDocumentStatusDTO status = getSpecificDocumentsStatusByPayrollId(payrollId);
        return status.getUploadedDocuments();
    }

    /**
     * Retrieves only the list of specific documents
     * (Personal/Gratuity/PrevEmployee) that are missing.
     * * @param payrollId The employee's payroll ID.
     * 
     * @return List of missing specific documents.
     */
    public List<EducationalDocumentStatusDTO.DocumentStatusDTO> getMissingSpecificDocuments(String payrollId) {
        logger.info("Getting missing specific documents for payroll_id: {}", payrollId);
        EducationalDocumentStatusDTO status = getSpecificDocumentsStatusByPayrollId(payrollId);
        return status.getMissingDocuments();
    }

}