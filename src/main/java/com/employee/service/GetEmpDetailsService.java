
package com.employee.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.employee.dto.EmpPfEsiResponseDTO;
import com.employee.dto.AddressResponseDTO;
import com.employee.dto.AllDocumentsDTO;
import com.employee.dto.BankInfoGetDTO;
import com.employee.dto.CategoryInfoDTO1;
import com.employee.dto.ChequeDetailsDto;
import com.employee.dto.EmpExperienceDetailsDTO;
import com.employee.dto.EmpFamilyDetailsDTO;
import com.employee.dto.EmployeeAgreementDetailsDto;
import com.employee.dto.EmployeeBankDetailsResponseDTO;
import com.employee.dto.FamilyDetailsResponseDTO;
import com.employee.dto.FamilyInfoResponseDTO;
import com.employee.dto.FullBasicInfoDto;
import com.employee.dto.ManagerDTO;
import com.employee.dto.PreviousEmployerInfoDTO;
import com.employee.dto.QualificationDetailsDto;
import com.employee.dto.QualificationInfoDTO;
import com.employee.dto.ReferenceDTO;
import com.employee.dto.SameInstituteEmployeesDTO;
import com.employee.dto.WorkingInfoDTO;
import com.employee.entity.BankDetails;
import com.employee.entity.EmpChequeDetails;
import com.employee.entity.EmpDetails;
import com.employee.entity.EmpDocuments;
import com.employee.entity.EmpExperienceDetails;
import com.employee.entity.EmpFamilyDetails;
import com.employee.entity.EmpOnboardingStatusView;
import com.employee.entity.EmpProfileView;
import com.employee.entity.EmpQualification;
import com.employee.entity.EmpaddressInfo;
import com.employee.entity.Employee;
import com.employee.entity.EmployeeOnboardingView;
import com.employee.entity.SkillTestApproval;
import com.employee.exception.ResourceNotFoundException;
import com.employee.repository.*;

@Service
public class GetEmpDetailsService {

	private final EmpQualificationRepository empQualificationRepository;

	@Autowired
	EmpFamilyDetailsRepository empFamilyDetailsRepo;
	@Autowired
	EmpExperienceDetailsRepository empExperienceDetailsRepo;
	@Autowired
	EmployeeRepository employeeRepo;
	@Autowired
	BankDetailsRepository bankDetailsRepository;
	@Autowired
	EmpSubjectRepository empSubjectRepository;
	@Autowired
	EmployeeProfileViewRepository profileRepo;
	@Autowired
	EmployeeOnboardingRepository employeeOnboardingRepo;
	@Autowired
	SkillTestResultRepository skillTestResultRepository;
	@Autowired
	EmpChequeDetailsRepository empChequeDetailsRepository;
	@Autowired
	SkillTestApprovalRepository skillTestApprovalRepository;
	@Autowired
	EmpDocumentsRepository empDocumentsRepository;
	@Autowired
	EmpaddressInfoRepository empAddressInfoRepo;
	@Autowired
	EmpOnboardingStatusViewRepository empOnboardingStatusViewRepository;

	@Autowired
	EmpPfDetailsRepository empPfDetailsRepository;

	@Autowired
	EmpDetailsRepository empDetailsRepository;

	GetEmpDetailsService(EmpQualificationRepository empQualificationRepository) {
		this.empQualificationRepository = empQualificationRepository;
	}

	public List<EmpFamilyDetailsDTO> getFamilyDetailsByEmpId(int empId) {
		List<EmpFamilyDetails> familyEntities = empFamilyDetailsRepo.findFamilyDetailsByEmpId(empId);

		// Fetch family photo path (shared for the employee)
		String photoPath = empDocumentsRepository.findByEmpIdAndDocName(empId, "Family Group Photo").stream()
				.findFirst().map(doc -> doc.getDoc_path()).orElse(null);

		return familyEntities.stream().map(fam -> {
			EmpFamilyDetailsDTO dto = new EmpFamilyDetailsDTO();
			dto.setEmpFamilyDetlId(fam.getEmp_family_detl_id());
			dto.setFullName(fam.getFullName());
			dto.setAdhaarNo(fam.getAdhaarNo());
			dto.setOccupation(fam.getOccupation());
			dto.setGender(fam.getGender_id() != null ? fam.getGender_id().getGenderName() : null);
			dto.setBloodGroup(fam.getBlood_group_id() != null ? fam.getBlood_group_id().getBloodGroupName() : null);
			dto.setNationality(fam.getNationality());
			dto.setRelation(fam.getRelation_id() != null ? fam.getRelation_id().getStudentRelationType() : null);
			dto.setIsDependent(fam.getIs_dependent());
			dto.setIsLate(fam.getIs_late());
			dto.setEmail(fam.getEmail());
			dto.setContactNumber(fam.getContact_no());

			// New fields
			dto.setDateOfBirth(fam.getDate_of_birth());
			dto.setIsSriChaitanyaEmp(fam.getIs_sri_chaitanya_emp());
			dto.setParentEmpPayrollId(fam.getParent_emp_id() != null ? fam.getParent_emp_id().getPayRollId() : null);
			dto.setFamilyPhotoPath(photoPath);

			return dto;
		}).collect(Collectors.toList());
	}

	// public List<EmpExperienceDetailsDTO> getExperienceByTempPayrollId(String
	// tempPayrollId) {
	//
	//
	// List<EmpExperienceDetails> experiences =
	// empExperienceDetailsRepo.findByEmployeeTempPayrollId(tempPayrollId);
	//
	// // 2. Convert entities to DTOs (same logic as before)
	// return experiences.stream()
	// .map(entity -> {
	// EmpExperienceDetailsDTO dto = new EmpExperienceDetailsDTO();
	//
	// dto.setCompanyName(entity.getPreOrzanigationName());
	// dto.setDesignation(entity.getDesignation());
	// dto.setLeavingReason(entity.getLeavingReason());
	// dto.setNatureOfDuties(entity.getNatureOfDuties());
	// dto.setCompanyAddress(entity.getCompanyAddr());
	//
	// if (entity.getDateOfJoin() != null) {
	// dto.setFromDate(entity.getDateOfJoin().toLocalDate());
	// }
	// if (entity.getDateOfLeave() != null) {
	// dto.setToDate(entity.getDateOfLeave().toLocalDate());
	// }
	//
	// BigDecimal grossSalary = new BigDecimal(entity.getGrossSalary());
	// dto.setCtc(grossSalary);
	// dto.setGrossSalaryPerMonth(
	// grossSalary.divide(TWELVE, 2, RoundingMode.HALF_UP)
	// );
	//
	// return dto;
	// })
	// .collect(Collectors.toList());
	// }

	public List<EmpExperienceDetailsDTO> getExperienceByTempPayrollId(String tempPayrollId) {

		List<EmpExperienceDetails> experienceList = empExperienceDetailsRepo
				.findExperienceByTempPayrollId(tempPayrollId);

		return experienceList.stream().map(this::convertToDTO).collect(Collectors.toList());
	}

	private EmpExperienceDetailsDTO convertToDTO(EmpExperienceDetails entity) {
		EmpExperienceDetailsDTO dto = new EmpExperienceDetailsDTO();

		dto.setEmpExpDetlId(entity.getEmp_exp_detl_id());
		dto.setCompanyName(entity.getPre_organigation_name());
		dto.setDesignation(entity.getDesignation());
		dto.setFromDate(entity.getDate_of_join().toLocalDate());
		dto.setToDate(entity.getDate_of_leave().toLocalDate());
		dto.setLeavingReason(entity.getLeaving_reason());
		dto.setCompanyAddressLine1(entity.getCompany_addr()); // Map to new field
		dto.setCompanyAddress(entity.getCompany_addr()); // Kept for compatibility
		dto.setNatureOfDuties(entity.getNature_of_duties());

		// Interpreting gross_salary as monthly salary as per POST structure
		BigDecimal monthlySalary = BigDecimal.valueOf(entity.getGross_salary());
		dto.setGrossSalaryPerMonth(monthlySalary);

		// Use stored CTC directly
		if (entity.getCtc() != null) {
			dto.setCtc(BigDecimal.valueOf(entity.getCtc()));
		} else {
			dto.setCtc(null); // No calculation, return null if not stored
		}

		// Fetch and map associated documents
		List<EmpDocuments> docEntities = empDocumentsRepository.findByExperienceId(entity.getEmp_exp_detl_id());
		List<PreviousEmployerInfoDTO.ExperienceDocumentDTO> docDTOs = docEntities.stream().map(doc -> {
			PreviousEmployerInfoDTO.ExperienceDocumentDTO d = new PreviousEmployerInfoDTO.ExperienceDocumentDTO();
			d.setDocPath(doc.getDoc_path());
			if (doc.getEmp_doc_type_id() != null) {
				d.setDocTypeId(doc.getEmp_doc_type_id().getDoc_type_id());
			}
			return d;
		}).collect(Collectors.toList());
		dto.setDocuments(docDTOs);

		return dto;
	}

	public List<EmpExperienceDetailsDTO> getExperienceByPayrollId(String payrollId) {

		// 1. Fetch data using the NEW repository method
		List<EmpExperienceDetails> experienceList = empExperienceDetailsRepo.findExperienceByPayrollId(payrollId);

		// 2. Convert to DTOs using your existing helper method
		return experienceList.stream().map(this::convertToDTO).collect(Collectors.toList());
	}

	// Change the return type from CategoryInfoDTO to CategoryInfoDTO1
	public List<CategoryInfoDTO1> getCategoryInfo(String TemppayrollId) {
		return empSubjectRepository.findCategoryInfoByPayrollId(TemppayrollId);
	}

	public EmployeeBankDetailsResponseDTO getBankDetailsByTempPayrollId(String tempPayrollId) {

		// Step 1: Get employee by tempPayrollId

		Employee employee = employeeRepo.findByTempPayrollId(tempPayrollId)

				.orElseThrow(() -> new RuntimeException("Employee not found for tempPayrollId: " + tempPayrollId));

		// Step 2: Get all active bank details for that employee

		List<BankDetails> bankDetailsList = bankDetailsRepository.findActiveBankDetailsByEmpId(employee.getEmp_id());

		if (bankDetailsList == null || bankDetailsList.isEmpty()) {

			throw new RuntimeException("No bank details found for employee ID: " + employee.getEmp_id());

		}

		// Step 3: Create response object

		EmployeeBankDetailsResponseDTO response = new EmployeeBankDetailsResponseDTO();

		// Step 4: Loop through bank details

		for (BankDetails bd : bankDetailsList) {
			BankInfoGetDTO dto = new BankInfoGetDTO();
			dto.setBankName(bd.getBankName());
			dto.setBankBranch(bd.getBankBranch());
			dto.setAccountHolderName(bd.getBankHolderName()); // Updated
			dto.setAccountNumber(bd.getAccNo()); // Updated
			dto.setIfscCode(bd.getIfscCode());

			String accType = bd.getAccType();

			// --- For PERSONAL account ---
			if ("PERSONAL".equalsIgnoreCase(accType)) {
				response.setPersonalBankInfo(dto);
			}

			// --- For SALARY account ---
			else if ("SALARY".equalsIgnoreCase(accType)) {

				// Here we need paymentType (foreign key from EmpPaymentType)
				if (bd.getEmpPaymentType() != null) {
					dto.setPaymentType(bd.getEmpPaymentType().getPayment_type());
				} else {
					dto.setPaymentType("N/A");
				}

				// Set extra salary-related fields
				dto.setIsSalaryLessThan40000("Yes"); // Optional logic based on salary amount
				dto.setPayableAt(bd.getPayableAt());

				// New Salary Info Fields
				dto.setBankManagerName(bd.getBankManagerName());
				dto.setBankManagerContactNo(bd.getBankManagerContactNo());
				dto.setBankManagerEmail(bd.getBankManagerEmail());
				dto.setCustomerRelationshipOfficerName(bd.getCustomerRelationshipOfficerName());
				dto.setCustomerRelationshipOfficerContactNo(bd.getCustomerRelationshipOfficerContactNo());
				dto.setCustomerRelationshipOfficerEmail(bd.getCustomerRelationshipOfficerEmail());

				response.setSalaryAccountInfo(dto);
			}
		}

		return response;

	}

	// EmployeeProfileView
	public Optional<EmpProfileView> getProfileByPayrollId(String payrollId) {
		return profileRepo.findByPayrollId(payrollId);
	}

	// EmployeeOnboardingView
	public Optional<EmployeeOnboardingView> getEmployeeOnboardingByTempPayrollId(String tempPayrollId) {
		return employeeOnboardingRepo.findByTempPayrollId(tempPayrollId);
	}

	// MangerDetailsOf the employee
	public ManagerDTO getManagerDetailsByTempPayrollId(String tempPayrollId) {
		return employeeRepo.findByTempPayrollId(tempPayrollId).map(emp -> {
			Employee manager = emp.getEmployee_manager_id();
			if (manager == null) {
				return null; // or throw custom exception
			}

			return new ManagerDTO(manager.getFirst_name() + " " + manager.getLast_name(), manager.getEmail(),
					manager.getPrimary_mobile_no(),
					manager.getDesignation() != null ? manager.getDesignation().getDesignation_name() : null,
					manager.getTempPayrollId());
		}).orElse(null);
	}

	public ReferenceDTO getReferenceDetailsByTempPayrollId(String tempPayrollId) {
		return employeeRepo.findByTempPayrollId(tempPayrollId).map(emp -> {
			Employee reference = emp.getEmployee_reference();
			if (reference == null) {
				return null; // or throw an exception like new EntityNotFoundException("No reference found");
			}

			return new ReferenceDTO(reference.getFirst_name() + " " + reference.getLast_name(), reference.getEmail(),
					reference.getPrimary_mobile_no(),

					// --- THIS IS THE FIX ---
					// Use the correct getter with an underscore
					reference.getDesignation() != null ? reference.getDesignation().getDesignation_name() : null,

					reference.getTempPayrollId());
		}).orElse(null);
	}

	// public List<SkillTestResultDTO> getSkillTestResultsByPayrollId(String
	// tempPayrollId) {
	// return
	// skillTestResultRepository.findSkillTestDetailsByPayrollId(tempPayrollId);
	// }

	// @Transactional(readOnly = true)
	public EmployeeAgreementDetailsDto getAgreementChequeInfo(String tempPayrollId) {
		Optional<Employee> employeeOpt = employeeRepo.findByTempPayrollId(tempPayrollId);

		if (employeeOpt.isEmpty()) {
			throw new RuntimeException("Employee not found for tempPayrollId: " + tempPayrollId);
		}

		Employee employee = employeeOpt.get();

		// Fetch all active cheques linked to this employee
		List<EmpChequeDetails> cheques = empChequeDetailsRepository.findActiveChequesByEmpId(employee.getEmp_id());
		// Map cheque details to DTO list
		List<ChequeDetailsDto> chequeDtos = cheques.stream().map(ch -> {
			ChequeDetailsDto d = new ChequeDetailsDto();
			d.setChequeId(ch.getEmpChequeDetailsId());
			d.setChequeNo(ch.getChequeNo());
			d.setChequeBank(ch.getChequeBankName());
			d.setIfscCode(ch.getChequeBankIfscCode());

			// Fetch path from EmpDocuments linked to this cheque via path prefix
			String linkPrefix = "CHEQUE_LINK_" + ch.getEmpChequeDetailsId() + "_";
			String searchPattern = "%" + linkPrefix + "%";
			empDocumentsRepository.findByEmpIdAndPathPattern(employee.getEmp_id(), searchPattern).ifPresent(doc -> {
				String path = doc.getDoc_path();
				if (path != null && path.startsWith(linkPrefix)) {
					d.setChequePath(path.substring(linkPrefix.length()));
				} else {
					d.setChequePath(path);
				}
			});

			return d;
		}).collect(Collectors.toList());

		// Build DTO
		EmployeeAgreementDetailsDto dto = new EmployeeAgreementDetailsDto();
		if (employee.getAgreement_org_id() != null) {
			dto.setAgreementCompany(employee.getAgreement_org_id().getOrganizationName());
		}
		dto.setAgreementType(employee.getAgreement_type());

		// Fetch path for the agreement document
		List<EmpDocuments> agreementDocs = empDocumentsRepository.findByEmpIdAndDocName(employee.getEmp_id(),
				"Agreement");
		if (agreementDocs.isEmpty()) {
			// Fallback: search for any document containing "Agreement" in its type name
			agreementDocs = empDocumentsRepository.findByEmpIdAndIsActive(employee.getEmp_id()).stream()
					.filter(doc -> doc.getEmp_doc_type_id() != null && doc.getEmp_doc_type_id().getDoc_name() != null
							&& doc.getEmp_doc_type_id().getDoc_name().toUpperCase().contains("AGREEMENT"))
					.sorted((d1, d2) -> {
						if (d1.getCreated_date() == null || d2.getCreated_date() == null)
							return 0;
						return d2.getCreated_date().compareTo(d1.getCreated_date());
					}).collect(Collectors.toList());
		}

		if (!agreementDocs.isEmpty()) {
			dto.setAgreementPath(agreementDocs.get(0).getDoc_path());
		}

		dto.setNoOfCheques(chequeDtos.size());
		dto.setCheques(chequeDtos);

		return dto;
	}

	public Optional<SkillTestApproval> getSkillTestApprovalDetails(String tempEmployeeId) {
		// Just call the built-in findById method from the JpaRepository
		return skillTestApprovalRepository.findById(tempEmployeeId);
	}

	public AllDocumentsDTO getAllDocumentsByTempPayrollId(String tempPayrollId) {

		// Step 1: Find employee by temp_payroll_id
		Employee employee = employeeRepo.findByTempPayrollId(tempPayrollId).orElseThrow(
				() -> new ResourceNotFoundException("Employee not found with temp_payroll_id: " + tempPayrollId));

		Integer empId = employee.getEmp_id();

		// Step 2: Get all documents from EmpDocuments table for that emp_id
		List<EmpDocuments> allDocuments = empDocumentsRepository.findByEmpIdAndIsActive(empId);

		// Step 3: Build response DTO
		AllDocumentsDTO response = new AllDocumentsDTO();
		response.setEmpId(empId);
		response.setPayrollId(employee.getPayRollId());
		response.setTempPayrollId(employee.getTempPayrollId());
		// Build documents list
		List<AllDocumentsDTO.DocumentDetailsDTO> documentsList = new ArrayList<>();

		for (EmpDocuments empDoc : allDocuments) {
			AllDocumentsDTO.DocumentDetailsDTO docDetails = new AllDocumentsDTO.DocumentDetailsDTO();
			docDetails.setEmpDocId(empDoc.getEmp_doc_id());

			// Get document type information
			if (empDoc.getEmp_doc_type_id() != null) {
				docDetails.setDocTypeId(empDoc.getEmp_doc_type_id().getDoc_type_id());
				docDetails.setDocName(empDoc.getEmp_doc_type_id().getDoc_name());
				docDetails.setDocType(empDoc.getEmp_doc_type_id().getDoc_type());
			}

			String path = empDoc.getDoc_path();
			if (path != null) {
				if (path.startsWith("QUAL_LINK_")) {
					// Format: QUAL_LINK_[ID]_[ActualPath]
					int firstUnderscore = path.indexOf("_");
					int secondUnderscore = path.indexOf("_", firstUnderscore + 1);
					if (secondUnderscore != -1) {
						path = path.substring(secondUnderscore + 1);
					}
				} else if (path.startsWith("CHEQUE_LINK_")) {
					// Format: CHEQUE_LINK_[ID]_[ActualPath]
					int firstUnderscore = path.indexOf("_");
					int secondUnderscore = path.indexOf("_", firstUnderscore + 1);
					if (secondUnderscore != -1) {
						path = path.substring(secondUnderscore + 1);
					}
				}
			}
			docDetails.setDocPath(path);
			docDetails.setIsVerified(empDoc.getIs_verified());
			docDetails.setIsActive(empDoc.getIs_active());

			documentsList.add(docDetails);
		}

		response.setDocuments(documentsList);

		return response;
	}

	// public WorkingInfoDTO getWorkingInfoByTempPayrollId(String tempPayrollId) {
	// // 1. Fetch the Employee entity with all required joins
	// Employee employee =
	// employeeRepo.findWorkingInfoByTempPayrollId(tempPayrollId)
	// .orElseThrow(() -> new RuntimeException("Employee not found with
	// tempPayrollId: " + tempPayrollId)); // Use
	// // a
	// // custom
	// // Exception
	// // later
	//
	// // 2. Map the Employee entity to the DTO
	// return mapToWorkingInfoDTO(employee);
	// }
	//
	// private WorkingInfoDTO mapToWorkingInfoDTO(Employee e) {
	// WorkingInfoDTO dto = new WorkingInfoDTO();
	//
	// // Employee entity fields
	// dto.setTempPayrollId(e.getTempPayrollId());
	// dto.setJoiningDate(e.getDate_of_join());
	//
	// // Campus related fields (via campus_id)
	// if (e.getCampus_id() != null) {
	// dto.setCampusName(e.getCampus_id().getCampusName());
	// dto.setCampusCode(e.getCampus_id().getCmps_code());
	// dto.setCampusType(e.getCampus_id().getCmps_type());
	//
	// // Location (assuming it's derived from the Campus's City)
	// if (e.getCampus_id().getCity() != null) {
	// dto.setLocation(e.getCampus_id().getCity().getCityName());
	// }
	// }
	//
	// // Note: Building Name is not directly mapped in your Employee entity.
	// // You would need a separate fetch or a direct relationship to fill this.
	// // For now, it is left null/empty.
	// // dto.setBuildingName(...);
	//
	// // Related Employee entities (Manager, Hired By, Replacement)
	// dto.setManagerName(formatEmployeeName(e.getEmployee_manager_id()));
	// dto.setReplacementEmployeeName(formatEmployeeName(e.getEmployee_replaceby_id()));
	// dto.setHiredByName(formatEmployeeName(e.getEmployee_hired()));
	//
	// // Working Mode, Joining As, Mode of Hiring
	// if (e.getWorkingMode_id() != null) {
	// dto.setWorkingModeType(e.getWorkingMode_id().getWork_mode_type());
	// }
	// if (e.getJoin_type_id() != null) {
	// dto.setJoiningAsType(e.getJoin_type_id().getJoin_type());
	// }
	//
	// // Assuming ModeOfHiring has a getType() method (based on entity naming
	// pattern)
	// if (e.getModeOfHiring_id() != null) {
	// // Note: Assuming 'ModeOfHiring' entity exists and has a method to get the
	// type.
	// // Placeholder: Replace with actual method if different.
	// // dto.setModeOfHiringType(e.getModeOfHiring_id().getModeOfHiringType());
	// }
	//
	// return dto;
	// }

	public WorkingInfoDTO getWorkingInfoByTempPayrollId(String tempPayrollId) {
		// 1. Fetch the Employee entity with all required joins
		Employee employee = employeeRepo.findWorkingInfoByTempPayrollId(tempPayrollId)
				.orElseThrow(() -> new RuntimeException("Employee not found with tempPayrollId: " + tempPayrollId)); // Use
																														// a
																														// custom
																														// Exception
																														// later

		// 2. Map the Employee entity to the DTO
		return mapToWorkingInfoDTO(employee);
	}

	private WorkingInfoDTO mapToWorkingInfoDTO(Employee e) {
		WorkingInfoDTO dto = new WorkingInfoDTO();

		// Employee entity fields
		dto.setTempPayrollId(e.getTempPayrollId());
		dto.setJoiningDate(e.getDate_of_join());

		// Campus related fields (via campus_id)
		if (e.getCampus_id() != null) {
			dto.setCampusName(e.getCampus_id().getCampusName());
			dto.setCampusCode(e.getCampus_id().getCmps_code());
			dto.setCampusType(e.getCampus_id().getCmps_type());

			// Location (assuming it's derived from the Campus's City)
			if (e.getCampus_id().getCity() != null) {
				dto.setLocation(e.getCampus_id().getCity().getCityName());
			}
		}

		// Note: Building Name is not directly mapped in your Employee entity.
		// You would need a separate fetch or a direct relationship to fill this.
		if (e.getBuilding_id() != null) {
			dto.setBuildingName(e.getBuilding_id().getBuildingName());
		}

		// Related Employee entities (Manager, Hired By, Replacement)
		dto.setManagerName(formatEmployeeName(e.getEmployee_manager_id()));
		dto.setReplacementEmployeeName(formatEmployeeName(e.getEmployee_replaceby_id()));
		dto.setHiredByName(formatEmployeeName(e.getEmployee_hired()));

		// Working Mode, Joining As, Mode of Hiring
		if (e.getWorkingMode_id() != null) {
			dto.setWorkingModeType(e.getWorkingMode_id().getWork_mode_type());
		}
		if (e.getJoin_type_id() != null) {
			dto.setJoiningAsType(e.getJoin_type_id().getJoin_type());
		}

		// Assuming ModeOfHiring has a getType() method (based on entity naming pattern)
		if (e.getModeOfHiring_id() != null) {
			dto.setModeOfHiringType(e.getModeOfHiring_id().getMode_of_hiring_name());
		}

		return dto;
	}

	private String formatEmployeeName(Employee emp) {
		if (emp != null) {
			// Basic concatenation for display name
			return emp.getFirst_name() + " " + emp.getLast_name();
		}
		return null; // or "N/A"
	}

	public QualificationInfoDTO getHighestQualificationDetails(String tempPayrollId) {

		// 1. Fetch the Employee record with the highest qualification type
		Employee employee = employeeRepo.findHighestQualificationDetailsByTempPayrollId(tempPayrollId)
				.orElseThrow(() -> new RuntimeException("Employee not found with tempPayrollId: " + tempPayrollId));

		// 2. Fetch the corresponding detailed EmpQualification record
		EmpQualification empQualification = employeeRepo.findHighestEmpQualificationRecord(tempPayrollId)
				.orElseThrow(() -> new RuntimeException("Detailed qualification record not found."));

		// 3. Map the entities to the DTO
		return mapToQualificationInfoDTO(employee, empQualification);
	}

	private QualificationInfoDTO mapToQualificationInfoDTO(Employee e, EmpQualification eq) {
		QualificationInfoDTO dto = new QualificationInfoDTO();

		// Qualification (Sourced from Employee.qualification_id)
		if (e.getQualification_id() != null) {
			dto.setQualification(e.getQualification_id().getQualification_name());
		}

		// Degree, Specialisation, Passed Out Year (Sourced from EmpQualification)
		// Note: Assuming 'QualificationDegree' entity has a 'getDegree_name()' method
		if (eq.getQualification_degree_id() != null) {
			// Placeholder for Degree name
			// dto.setDegree(eq.getQualification_degree_id().getDegree_name());
			// Using the Qualification Name for Degree, matching the image example ("B.Tech"
			// for both)
			if (e.getQualification_id() != null) {
				dto.setDegree(e.getQualification_id().getQualification_name());
			}
		}

		dto.setSpecialisation(eq.getSpecialization());
		dto.setPassedOutYear(eq.getPassedout_year());

		// Academic Details (Sourced from EmpQualification)
		dto.setUniversity(eq.getUniversity());
		dto.setInstitute(eq.getInstitute());

		// Placeholder for certificate status
		dto.setCertificateStatus("Available");

		return dto;
	}

	public List<EmpOnboardingStatusView> getEmpStatus() {
		return empOnboardingStatusViewRepository.findAllDistinct();
	}

	public List<EmpOnboardingStatusView> getEmpStatusByCategoryName(String categoryName) {
		return empOnboardingStatusViewRepository.findByCategoryName(categoryName);
	}

	public FamilyInfoResponseDTO getFamilyDetailsWithAddressInfo(String tempPayrollId) {

		// 1. Find the Employee
		Employee employee = employeeRepo.findByTempPayrollId(tempPayrollId).orElseThrow(
				() -> new RuntimeException("Employee with tempPayrollId: " + tempPayrollId + " not found."));

		// 2. Fetch Family Details
		List<EmpFamilyDetails> familyDetailsList = empFamilyDetailsRepo.findByEmployeeEntity(employee);

		// 3. Determine State and Country from Address Info
		List<EmpaddressInfo> addresses = empAddressInfoRepo.findByEmployeeEntity(employee);

		String stateName = null;
		Integer stateId = null;
		String countryName = null;
		Integer countryId = null;

		Optional<EmpaddressInfo> permanentAddress = addresses.stream()
				.filter(a -> "PERM".equalsIgnoreCase(a.getAddrs_type())).findFirst();

		Optional<EmpaddressInfo> currentAddress = addresses.stream()
				.filter(a -> "CURR".equalsIgnoreCase(a.getAddrs_type())).findFirst();

		if (permanentAddress.isPresent()) {
			stateName = permanentAddress.get().getState_id().getStateName();
			stateId = permanentAddress.get().getState_id().getStateId();
			countryName = permanentAddress.get().getCountry_id().getCountryName();
			countryId = permanentAddress.get().getCountry_id().getCountryId();
		} else if (currentAddress.isPresent()) {
			stateName = currentAddress.get().getState_id().getStateName();
			stateId = currentAddress.get().getState_id().getStateId();
			countryName = currentAddress.get().getCountry_id().getCountryName();
			countryId = currentAddress.get().getCountry_id().getCountryId();
		}

		// Use final variables for stream mapping
		final String finalStateName = stateName;
		final Integer finalStateId = stateId;
		final String finalCountryName = countryName;
		final Integer finalCountryId = countryId;

		// Fetch family photo path once
		String photoPath = empDocumentsRepository.findByEmpIdAndDocName(employee.getEmp_id(), "Family Group Photo")
				.stream().findFirst().map(doc -> doc.getDoc_path()).orElse(null);

		// 4. Map the entity list to the DTO response list
		List<FamilyDetailsResponseDTO> members = familyDetailsList.stream().map(familyDetail -> {
			FamilyDetailsResponseDTO dto = new FamilyDetailsResponseDTO();

			dto.setEmpFamilyDetlId(familyDetail.getEmp_family_detl_id());
			dto.setName(familyDetail.getFullName());
			dto.setFullName(familyDetail.getFullName());
			dto.setAdhaarNo(familyDetail.getAdhaarNo());

			// Safe mapping with null checks
			if (familyDetail.getRelation_id() != null) {
				dto.setRelation(familyDetail.getRelation_id().getStudentRelationType());
				dto.setRelationId(familyDetail.getRelation_id().getStudentRelationId());
			}
			if (familyDetail.getBlood_group_id() != null) {
				dto.setBloodGroup(familyDetail.getBlood_group_id().getBloodGroupName());
				dto.setBloodGroupId(familyDetail.getBlood_group_id().getBloodGroupId());
			}
			if (familyDetail.getGender_id() != null) {
				dto.setGenderId(familyDetail.getGender_id().getGender_id());
			}

			dto.setOccupation(familyDetail.getOccupation());
			dto.setEmailId(familyDetail.getEmail());
			dto.setEmail(familyDetail.getEmail());
			dto.setPhoneNumber(familyDetail.getContact_no());
			dto.setNationality(familyDetail.getNationality());

			// New fields
			dto.setDateOfBirth(familyDetail.getDate_of_birth());
			dto.setIsLate("Y".equalsIgnoreCase(familyDetail.getIs_late()));
			dto.setIsDependent(familyDetail.getIs_dependent() != null && familyDetail.getIs_dependent() == 1);
			dto.setIsSriChaitanyaEmp(
					familyDetail.getIs_sri_chaitanya_emp() != null && familyDetail.getIs_sri_chaitanya_emp() == 1);

			if (familyDetail.getParent_emp_id() != null) {
				Employee parent = familyDetail.getParent_emp_id();
				String pId = parent.getPayRollId();
				if (pId == null || pId.isEmpty()) {
					pId = parent.getTempPayrollId();
				}
				dto.setParentEmpId(pId);
				dto.setParentEmpPayrollId(pId);
			}

			// Set the derived address info
			dto.setState(finalStateName);
			dto.setStateId(finalStateId);
			dto.setCountry(finalCountryName);
			dto.setCountryId(finalCountryId);

			return dto;
		}).collect(Collectors.toList());

		return new FamilyInfoResponseDTO(photoPath, members);
	}

	public FullBasicInfoDto getEmployeeDetailsByTempPayrollId(String tempPayrollId) {
		return employeeRepo.findFullEmployeeDetailsByTempPayrollId(tempPayrollId)
				.orElseThrow(() -> new RuntimeException("Employee not found for tempPayrollId: " + tempPayrollId));
	}

	public Map<String, List<AddressResponseDTO>> getEmployeeAddresses(String tempPayrollId) {

		List<AddressResponseDTO> addresses = empAddressInfoRepo.findAddressesByTempPayrollId(tempPayrollId);

		return addresses.stream().collect(Collectors.groupingBy(AddressResponseDTO::getAddressType));
	}

	public List<QualificationDetailsDto> getQualificationsByTempPayrollId(

			String tempPayrollId) {

		List<EmpQualification> qualifications = empQualificationRepository

				.findByEmp_id_TempPayrollIdAndIsActive(tempPayrollId, 1);

		return qualifications.stream().map(q -> {

			QualificationDetailsDto dto = new QualificationDetailsDto();

			dto.setEmpQualificationId(q.getEmp_qualification_id());

			if (q.getQualification_id() != null) {

				dto.setQualificationId(q.getQualification_id().getQualification_id());

				dto.setQualificationName(q.getQualification_id().getQualification_name());

			}

			if (q.getQualification_degree_id() != null) {

				dto.setQualificationDegreeId(q.getQualification_degree_id().getQualification_degree_id());

				dto.setQualificationDegree(q.getQualification_degree_id().getDegree_name());

			}

			dto.setSpecialization(q.getSpecialization());

			dto.setInstitute(q.getInstitute());

			dto.setUniversity(q.getUniversity());

			dto.setPassedoutYear(q.getPassedout_year());

			dto.setIsActive(q.getIs_active());

			// Fetch certificate path from EmpDocuments based on doc_type_id

			if (q.getEmp_id() != null && q.getQualification_id() != null) {

				Integer qId = q.getQualification_id().getQualification_id();

				// Find documents for this employee and qualification ID

				List<EmpDocuments> docs = empDocumentsRepository.findByEmpIdAndDocTypeId(

						q.getEmp_id().getEmp_id(), qId);

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
	 * Get employees with same institute based on highest qualification
	 * 
	 * @param payrollId Payroll ID of the employee
	 * @return SameInstituteEmployeesDTO containing institute, qualification info,
	 *         and list of employees (minimum 4)
	 */
	public SameInstituteEmployeesDTO getEmployeesWithSameInstitute(String payrollId) {
		// 1. Find employee by payrollId
		Employee employee = employeeRepo.findByPayRollId(payrollId)
				.orElseThrow(() -> new ResourceNotFoundException("Employee not found with payrollId: " + payrollId));

		// 2. Get the highest qualification_id from Employee
		if (employee.getQualification_id() == null) {
			throw new ResourceNotFoundException("Employee does not have a highest qualification set");
		}

		Integer qualificationId = employee.getQualification_id().getQualification_id();
		String qualificationName = employee.getQualification_id().getQualification_name();

		// 3. Find the EmpQualification record for that employee with that
		// qualification_id to get the institute
		List<EmpQualification> empQualifications = empQualificationRepository
				.findByEmpIdAndIsActive(employee.getEmp_id());

		EmpQualification highestQualification = empQualifications.stream()
				.filter(eq -> eq.getQualification_id() != null
						&& eq.getQualification_id().getQualification_id() == qualificationId)
				.findFirst().orElseThrow(() -> new ResourceNotFoundException(
						"Qualification record not found for employee with qualification_id: " + qualificationId));

		if (highestQualification.getInstitute() == null || highestQualification.getInstitute().trim().isEmpty()) {
			throw new ResourceNotFoundException(
					"Employee does not have an institute set for the highest qualification");
		}

		String institute = highestQualification.getInstitute().trim();

		// 4. Find minimum 4 employees with the same institute and same qualification_id
		List<Employee> employeesWithSameInstitute = empQualificationRepository
				.findEmployeesByInstituteAndQualification(institute, qualificationId, employee.getEmp_id());

		// Return minimum 4 employees if available, otherwise return all available
		// If there are 4 or more, return first 4; if less than 4, return all
		int limitCount = employeesWithSameInstitute.size() >= 4 ? 4 : employeesWithSameInstitute.size();
		List<Employee> limitedEmployees = employeesWithSameInstitute.stream().limit(limitCount)
				.collect(Collectors.toList());

		// 5. Map to DTO
		SameInstituteEmployeesDTO dto = new SameInstituteEmployeesDTO();
		dto.setInstitute(institute);
		dto.setQualificationId(qualificationId);
		dto.setQualificationName(qualificationName);

		List<SameInstituteEmployeesDTO.EmployeeInfo> employeeInfoList = limitedEmployees.stream().map(emp -> {
			SameInstituteEmployeesDTO.EmployeeInfo info = new SameInstituteEmployeesDTO.EmployeeInfo();
			info.setEmpId(emp.getEmp_id());
			info.setFirstName(emp.getFirst_name());
			info.setLastName(emp.getLast_name());
			info.setPayrollId(emp.getPayRollId());
			info.setTempPayrollId(emp.getTempPayrollId());
			info.setEmail(emp.getEmail());
			info.setPrimaryMobileNo(emp.getPrimary_mobile_no());

			// Set designation information
			if (emp.getDesignation() != null) {
				info.setDesignationId(emp.getDesignation().getDesignation_id());
				info.setDesignationName(emp.getDesignation().getDesignation_name());
			}

			return info;
		}).collect(Collectors.toList());

		dto.setEmployees(employeeInfoList);

		return dto;
	}

	public EmpPfEsiResponseDTO getPfEsiDetailsByTempPayrollId(String tempPayrollId) {
		Employee employee = employeeRepo.findByTempPayrollId(tempPayrollId)
				.orElseThrow(
						() -> new ResourceNotFoundException("Employee not found for tempPayrollId: " + tempPayrollId));

		// Fetch UAN from EmpDetails table
		Long uanNo = null;
		Optional<EmpDetails> empDetailsOpt = empDetailsRepository.findByEmployeeId(employee.getEmp_id());
		if (empDetailsOpt.isPresent()) {
			uanNo = empDetailsOpt.get().getUanNo();
		}

		// Fetch PF/ESI details from EmpPfDetails table
		Optional<com.employee.entity.EmpPfDetails> pfDetailsOpt = empPfDetailsRepository
				.findByEmployeeIdAndIsActive(employee.getEmp_id(), 1);

		if (pfDetailsOpt.isEmpty()) {
			// Return DTO with only UAN if PF/ESI details not found
			return new EmpPfEsiResponseDTO(uanNo, null, null, null);
		}

		com.employee.entity.EmpPfDetails pfDetails = pfDetailsOpt.get();
		return new EmpPfEsiResponseDTO(
				uanNo, // UAN from emp_details table
				pfDetails.getEsi_no(), // Current ESI from pf_esi_uan table
				pfDetails.getPre_esi_no(), // Previous ESI from pf_esi_uan table
				pfDetails.getPf_no()); // PF number from pf_esi_uan table
	}

}
