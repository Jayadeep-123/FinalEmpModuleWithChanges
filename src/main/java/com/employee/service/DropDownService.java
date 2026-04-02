package com.employee.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.employee.exception.ResourceNotFoundException;
import com.employee.dto.CampusContactDTO;
import com.employee.dto.CampusDto;
import com.employee.dto.CmpsOrientationsDTO;
import com.employee.dto.Dgmdto;
import com.employee.dto.GenericDropdownDTO;
import com.employee.dto.OrganizationDTO;
import com.employee.entity.Building;
import com.employee.entity.BusinessType;
import com.employee.entity.Campus;
import com.employee.entity.CampusContact;
import com.employee.entity.Department;
import com.employee.entity.Designation;
import com.employee.entity.Employee;
import com.employee.entity.EmployeeType;
import com.employee.entity.EmployeeTypeHiring;
import com.employee.entity.OrgBank;
import com.employee.entity.Relation;
import com.employee.entity.Subject;
import com.employee.repository.BuildingRepository;
import com.employee.repository.CampusContactRepository;
import com.employee.repository.CampusRepository;
import com.employee.repository.CategoryRepository;
import com.employee.repository.CityRepository;
import com.employee.repository.CmpsOrgRepository;
import com.employee.repository.CmpsOrientationRepository;
import com.employee.repository.CostCenterRepository;
import com.employee.repository.CountryRepository;
import com.employee.repository.DepartmentRepository;
import com.employee.repository.DesignationRepository;
import com.employee.repository.EmpAppCheckListDetlRepository;
import com.employee.repository.EmpDocTypeRepository;
import com.employee.repository.EmpPaymentTypeRepository;
import com.employee.repository.EmployeeLevelRepository;
import com.employee.repository.EmployeeRepository;
import com.employee.repository.EmployeeTypeHiringRepository;
import com.employee.repository.EmployeeTypeRepository;
import com.employee.repository.GradeRepository;
import com.employee.repository.JoiningAsRepository;
import com.employee.repository.MaritalStatusRepository;
import com.employee.repository.ModeOfHiringRepository;
import com.employee.repository.OccupationRepository;
import com.employee.repository.OrgBankBranchRepository;
import com.employee.repository.OrgBankRepository;
import com.employee.repository.OrganizationRepository;
import com.employee.repository.OrientationGroupRepository;
import com.employee.repository.OrientationRepository;
import com.employee.repository.QualificationDegreeRepository;
import com.employee.repository.QualificationRepository;
import com.employee.repository.RelationRepository;
import com.employee.repository.StreamRepository;
import com.employee.repository.StructureRepository;
import com.employee.repository.SubjectRepository;
import com.employee.repository.WorkingModeRepository;

@Service
public class DropDownService {

	@Autowired
	MaritalStatusRepository maritalStatusRepo;
	@Autowired
	QualificationRepository qualificationRepo;
	@Autowired
	WorkingModeRepository workingModeRepo;
	@Autowired
	JoiningAsRepository joiningAsRepo;
	@Autowired
	ModeOfHiringRepository modeOfHiringRepo;
	@Autowired
	EmployeeTypeRepository employeeTypeRepo;
	@Autowired
	EmployeeTypeHiringRepository employeeTypeHiringRepo;
	@Autowired
	EmpPaymentTypeRepository employeePaymentTypeRepo;
	@Autowired
	DepartmentRepository departmentRepo;
	@Autowired
	DesignationRepository designationRepo;
	@Autowired
	CountryRepository countryRepo;
	@Autowired
	QualificationDegreeRepository qualificationDegreeRepo;
	@Autowired
	SubjectRepository subjectRepo;
	@Autowired
	GradeRepository gradeRepo;
	@Autowired
	CampusRepository campusRepository;
	@Autowired
	CostCenterRepository costCenterRepo;
	@Autowired
	StructureRepository structureRepo;
	@Autowired
	OrganizationRepository organizationRepo;
	@Autowired
	CmpsOrgRepository cmpsOrgRepository;
	@Autowired
	BuildingRepository buildingRepository;
	@Autowired
	StreamRepository streamRepository;
	@Autowired
	EmployeeLevelRepository empLevelRepository;
	@Autowired
	OrgBankRepository orgBankRepository;
	@Autowired
	OrgBankBranchRepository orgBankBranchRepository;
	@Autowired
	EmployeeRepository employeeRepository;
	@Autowired
	CampusContactRepository campusContactRepository;
	@Autowired
	CategoryRepository categoryRepository;
	@Autowired
	CityRepository cityRepository;
	@Autowired
	OccupationRepository occupationRepository;
	@Autowired
	OrientationRepository orientationRepository;
	@Autowired
	EmpAppCheckListDetlRepository empAppCheckListDetlRepository;
	@Autowired
	EmpDocTypeRepository empDocTypeRepository;

	@Autowired
	RelationRepository relationRepository;

	@Autowired
	CmpsOrientationRepository cmpsorientationRepository;

	@Autowired
	OrientationGroupRepository orientationGroupRepository;

	@Autowired
	com.employee.repository.BusinessTypeRepository businessTypeRepository;

	@Autowired
	com.employee.repository.RoleRepository roleRepository;

	private static final int ACTIVE_STATUS = 1;

	public List<GenericDropdownDTO> getMaritalStatusTypes() {
		return maritalStatusRepo.findByIsActive(ACTIVE_STATUS).stream()
				// Use the getters that match your entity's field names
				.map(t -> new GenericDropdownDTO(t.getMarital_status_id(), t.getMarital_status_type()))
				.collect(Collectors.toList());
	}

	public List<GenericDropdownDTO> getQualificationTypes() {
		return qualificationRepo.findByIsActive(ACTIVE_STATUS).stream()
				// Change the getter calls to match your entity's field names
				.map(q -> new GenericDropdownDTO(q.getQualification_id(), q.getQualification_name()))
				.collect(Collectors.toList());
	}

	/**
	 * Get qualifications filtered by qualification level.
	 * Given a qualification level, returns all active qualifications
	 * with level strictly BELOW that level.
	 * 
	 * Example: If level = 2, returns only "10th" (level 1).
	 *          If level = 3, returns "10th" (level 1) and "Inter/Diploma" (level 2).
	 * 
	 * @param qualificationLevel The qualification level to filter by
	 * @return List of qualifications below that level
	 */
	public List<GenericDropdownDTO> getQualificationsByLevel(int qualificationLevel) {
		return qualificationRepo.findByQualificationLevelLessThanAndIsActive(
				qualificationLevel, ACTIVE_STATUS)
				.stream()
				.map(q -> new GenericDropdownDTO(q.getQualification_id(), q.getQualification_name()))
				.collect(Collectors.toList());
	}

	public List<GenericDropdownDTO> getWorkModeTypes() {
		return workingModeRepo.findByIsActive(ACTIVE_STATUS).stream()
				// Change the getter calls to match your entity's field names
				.map(m -> new GenericDropdownDTO(m.getEmp_work_mode_id(), m.getWork_mode_type()))
				.collect(Collectors.toList());
	}

	public List<GenericDropdownDTO> getJoinAsTypes() {
		return joiningAsRepo.findByIsActive(ACTIVE_STATUS).stream()
				// Change the getter calls to match your entity's field names
				.map(j -> new GenericDropdownDTO(j.getJoin_type_id(), j.getJoin_type())).collect(Collectors.toList());
	}

	public List<GenericDropdownDTO> getModeOfHiringTypes() {
		return modeOfHiringRepo.findByIsActive(ACTIVE_STATUS).stream()
				// Change the getter calls to match your entity's field names
				.map(m -> new GenericDropdownDTO(m.getMode_of_hiring_id(), m.getMode_of_hiring_name()))
				.collect(Collectors.toList());
	}

	public List<GenericDropdownDTO> getEmployeeTypes() {
		List<EmployeeType> activeEmpTypes = employeeTypeRepo.findByIsActive(ACTIVE_STATUS);
		return activeEmpTypes.stream()
				// Change the getter calls to match your entity's field names
				.map(type -> new GenericDropdownDTO(type.getEmp_type_id(), type.getEmp_type()))
				.collect(Collectors.toList());
	}

	public List<GenericDropdownDTO> getEmployeeTypeHiringTypes() {
		return employeeTypeHiringRepo.findByIsActive(ACTIVE_STATUS).stream()
				.map(h -> new GenericDropdownDTO(h.getEmp_type_hiring_id(), h.getEmp_type_hiring_name()))
				.collect(Collectors.toList());
	}

	public List<GenericDropdownDTO> getEmployeePaymentTypes() {
		return employeePaymentTypeRepo.findByIsActive(ACTIVE_STATUS).stream()
				// Change the getter calls to match your entity's field names
				.map(p -> new GenericDropdownDTO(p.getEmp_payment_type_id(), p.getPayment_type()))
				.collect(Collectors.toList());
	}

	// public List<GenericDropdownDTO> getDepartments(Integer empTypeId) {
	// List<Department> departments;
	//
	// if (empTypeId == null) {
	// departments = departmentRepo.findByIsActive(1);
	// } else {
	// departments = departmentRepo.findByEmpTypeId_EmpTypeIdAndIsActive(empTypeId,
	// 1);
	// }
	//
	// return departments.stream()
	// // Change the getter calls to match your entity's field names
	// .map(dept -> new GenericDropdownDTO(dept.getDepartment_id(),
	// dept.getDepartment_name()))
	// .collect(Collectors.toList());
	// }

	public List<GenericDropdownDTO> getDepartments(Integer empTypeId, String categoryName) {
		List<Department> departments;

		if (empTypeId == null || categoryName == null) {
			throw new IllegalArgumentException("Employee Type ID and Category Name are mandatory");
		}

		BusinessType category = businessTypeRepository.findByBusinessTypeNameIgnoreCase(categoryName)
				.orElseThrow(() -> new ResourceNotFoundException("Category not found: " + categoryName));

		departments = departmentRepo.findByEmpTypeId_EmpTypeIdAndDepartmentCategory_BusinessTypeIdAndIsActive(
				empTypeId, category.getBusinessTypeId(), 1);

		// 3. Convert Entity list to DTO list
		return departments.stream()
				.map(dept -> new GenericDropdownDTO(dept.getDepartment_id(), dept.getDepartment_name()))
				.collect(Collectors.toList());
	}

	public List<GenericDropdownDTO> getDesignations(int departmentId) {
		// TODO Auto-generated method stub
		final int ACTIVE_STATUS = 1;
		return designationRepo.findByDepartmentId_DepartmentIdAndIsActive(departmentId, ACTIVE_STATUS).stream()
				// Change the getter calls to match your entity's field names
				.map(d -> new GenericDropdownDTO(d.getDesignation_id(), d.getDesignation_name()))
				.collect(Collectors.toList());
	}

	public List<GenericDropdownDTO> getActiveDesignations() {
		return designationRepo.findByIsActive(ACTIVE_STATUS).stream()
				.map(d -> new GenericDropdownDTO(d.getDesignation_id(), d.getDesignation_name()))
				.collect(Collectors.toList());
	}

	public List<GenericDropdownDTO> getCountries() {
		// TODO Auto-generated method stub
		return countryRepo.findAll().stream().map(c -> new GenericDropdownDTO(c.getCountryId(), c.getCountryName()))
				.collect(Collectors.toList());
	}

	public List<GenericDropdownDTO> getDegrees(int qualificationId) {
		final int ACTIVE_STATUS = 1;

		return qualificationDegreeRepo.findByQualification_QualificationIdAndIsActive(qualificationId, ACTIVE_STATUS)
				.stream()
				// Change the getter calls to match your entity's field names
				.map(d -> new GenericDropdownDTO(d.getQualification_degree_id(), d.getDegree_name()))
				.collect(Collectors.toList());
	}

	public List<Relation> getAll() {
		return relationRepository.findAll();
	}

	public List<GenericDropdownDTO> getSubjects(String categoryName, Integer empSubject) {
		final int ACTIVE_STATUS = 1;

		List<Subject> subjects;

		if (categoryName != null && !categoryName.trim().isEmpty()) {
			BusinessType category = businessTypeRepository.findByBusinessTypeNameIgnoreCase(categoryName.trim())
					.orElseThrow(() -> new ResourceNotFoundException("Category not found: " + categoryName));

			if (empSubject != null) {
				// 1. Fetch subjects filtered by category and type
				subjects = subjectRepo.findBySubjectCategory_BusinessTypeIdAndEmpSubjectAndIsActive(
						category.getBusinessTypeId(),
						empSubject, ACTIVE_STATUS);
			} else {
				// 2. Fetch subjects filtered by category only
				subjects = subjectRepo.findBySubjectCategory_BusinessTypeIdAndIsActive(
						category.getBusinessTypeId(), ACTIVE_STATUS);
			}
		} else {
			// 3. Fetch all active subjects (fallback)
			subjects = subjectRepo.findByIsActive(ACTIVE_STATUS);
		}

		// 4. Map to DTO
		return subjects.stream()
				.map(subject -> new GenericDropdownDTO(subject.getSubject_id(), subject.getSubject_name()))
				.collect(Collectors.toList());
	}

	// public CampusDto getActiveCampusById(int campusId) {
	// return campusRepository.findActiveCampusById(campusId)
	// .orElseThrow(() -> new RuntimeException("Active campus not found for ID: " +
	// campusId));
	// }

	public CampusDto getActiveCampusById(int campusId) {
		CampusDto campusDto = campusRepository.findActiveCampusById(campusId)
				.orElseThrow(() -> new RuntimeException("Active campus not found for ID: " + campusId));

		// Populate main building details
		buildingRepository.findMainBuildingByCampusId(campusId).ifPresent(building -> {
			campusDto.setBuildingId(building.getBuildingId());
			campusDto.setBuildingName(building.getBuildingName());
		});

		return campusDto;
	}

	public List<GenericDropdownDTO> getActiveGrades() {
		return gradeRepo.findByIsActive(1).stream()
				.map(g -> new GenericDropdownDTO(g.getEmpGradeId(), g.getGradeName())).collect(Collectors.toList());
	}

	public List<GenericDropdownDTO> getActiveStructures() {
		return structureRepo.findByIsActive(1).stream()
				.map(s -> new GenericDropdownDTO(s.getEmpStructureId(), s.getStructureName()))
				.collect(Collectors.toList());
	}

	public List<GenericDropdownDTO> getCostCenters() {
		return costCenterRepo.findAll().stream()
				.map(c -> new GenericDropdownDTO(c.getCostCenterId(), c.getCostCenterName()))
				.collect(Collectors.toList());
	}

	public List<OrganizationDTO> getOrganizationsWithPayrollDetails() {
		// 1. Fetch active orgs with valid payroll data
		return organizationRepo.findByIsActiveAndPayrollCodeIsNotNullAndPayrollMaxNoIsNotNull(1)
				.stream()
				// 2. Map Entity fields to your new simple DTO
				.map(org -> new OrganizationDTO(
						org.getOrganizationId(), // Entity field
						org.getOrganizationName() // Entity field
				))
				.collect(Collectors.toList());
	}

	public List<GenericDropdownDTO> getOrganizationsByCampusId(int campusId) {
		return cmpsOrgRepository.findOrganizationsByCampusId(campusId);
	}

	// public List<GenericDropdownDTO> getBuildingsByCampusId(int campusId) {
	// return buildingRepository.findBuildingsByCampusId(campusId);
	// }

	public List<com.employee.dto.BuildingDropdownDTO> getBuildingsByCampusId(int campusId) {
		return buildingRepository.findBuildingsByCampusId(campusId);
	}

	public List<GenericDropdownDTO> getAllActiveStreams() {
		return streamRepository.findAllActiveStreams();
	}

	public List<GenericDropdownDTO> getAllActiveEmpLevels() {
		return empLevelRepository.findAllActiveEmpLevels();
	}

	public List<GenericDropdownDTO> getAllActiveBanks() {
		List<OrgBank> activeBanks = orgBankRepository.findByIsActive(ACTIVE_STATUS);

		// Convert to GenericDto, using the correct underscore getters
		return activeBanks.stream().map(bank -> new GenericDropdownDTO(bank.getOrg_bank_id(), bank.getBank_name()))
				.collect(Collectors.toList());
	}

	public List<GenericDropdownDTO> getAllActiveBankBranches() {
		return orgBankBranchRepository.findByIsActive(ACTIVE_STATUS).stream()
				.map(branch -> new GenericDropdownDTO(branch.getOrg_bank_branch_id(), branch.getBranch_name()))
				.collect(Collectors.toList());
	}

	public List<GenericDropdownDTO> getActiveBankBranchesByBankId(Integer bankId) {
		return orgBankBranchRepository.findActiveBranchesByBankId(bankId).stream()
				.map(branch -> new GenericDropdownDTO(branch.getOrg_bank_branch_id(), branch.getBranch_name()))
				.collect(Collectors.toList());
	}

	public List<GenericDropdownDTO> getActiveChecklistDetails() {
		return empAppCheckListDetlRepository.findByIsActiveNative(ACTIVE_STATUS).stream().map(row -> {
			Integer id = ((Number) row[0]).intValue(); // emp_app_check_list_detl_id
			String name = (String) row[1]; // check_list_detl_name
			return new GenericDropdownDTO(id, name);
		}).collect(Collectors.toList());
	}

	public List<GenericDropdownDTO> getActiveDocumentTypes() {
		return empDocTypeRepository.findByIsActive(ACTIVE_STATUS).stream()
				.map(docType -> new GenericDropdownDTO(docType.getDoc_type_id(), docType.getDoc_name()))
				.collect(Collectors.toList());
	}

	public List<GenericDropdownDTO> getActiveEmployees() {
		return employeeRepository.findByIsActive(1).stream().map(emp -> new GenericDropdownDTO(emp.getEmp_id(), // Changed
																												// from
																												// getEmpId()
				emp.getFirst_name() + " " + emp.getLast_name())).collect(Collectors.toList());
	}

	// ✅ 2. Get Inactive Employees (isActive = 0)
	public List<GenericDropdownDTO> getInactiveEmployees() {
		return employeeRepository.findByIsActive(0).stream()
				.map(emp -> new GenericDropdownDTO(emp.getEmp_id(), emp.getFirst_name() + " " + emp.getLast_name()))
				.collect(Collectors.toList());
	}

	public List<CampusContactDTO> getActiveContactsByCampusId(Integer campusId) {
		// Now the service calls the repository
		return campusContactRepository.findActiveContactsByCampusId(campusId);
	}

	public List<GenericDropdownDTO> getAllEmployees() {
		List<Employee> activeEmployees = employeeRepository.findByIsActive(1);

		return activeEmployees.stream().map(employee -> new GenericDropdownDTO(employee.getEmp_id(),
				employee.getFirst_name() + " " + employee.getLast_name())).collect(Collectors.toList());
	}

	public List<GenericDropdownDTO> getAllOrganizations() {
		return organizationRepo.findAll().stream()
				.map(org -> new GenericDropdownDTO(org.getOrganizationId(), org.getOrganizationName()))
				.collect(Collectors.toList());
	}

	public List<GenericDropdownDTO> getActiveCategories() {
		return categoryRepository.findByIsActive(ACTIVE_STATUS).stream()
				.map(c -> new GenericDropdownDTO(c.getCategory_id(), c.getCategory_name()))
				.collect(Collectors.toList());
	}

	public List<GenericDropdownDTO> getActiveCampuses(String cmpsCategory) {
		// If no category is provided, return all active campuses
		if (cmpsCategory == null || cmpsCategory.trim().isEmpty()) {
			return campusRepository.findByIsActive(ACTIVE_STATUS).stream()
					.map(c -> new GenericDropdownDTO(c.getCampusId(), c.getCampusName()))
					.collect(Collectors.toList());
		}

		Integer businessId = 0;
		// Map category string to Business/Business Type ID dynamically
		java.util.Optional<com.employee.entity.BusinessType> businessType = businessTypeRepository
				.findByBusinessTypeNameIgnoreCase(cmpsCategory.trim());

		if (businessType.isPresent()) {
			businessId = businessType.get().getBusinessTypeId();
		}

		// Fetch based on ID if mapped, otherwise try matching by name (fallback or if
		// name matches other criteria)
		if (businessId > 0) {
			return campusRepository.findByIsActiveAndBusinessId(ACTIVE_STATUS, businessId);
		} else {
			return campusRepository.findByIsActiveAndBusinessName(ACTIVE_STATUS, cmpsCategory.trim());
		}
	}

	// public List<GenericDropdownDTO> getAllEmployeesByCampusId(Integer campusId) {
	//
	// // 1. Change the repository call to the one that fetches ALL (Active +
	// Inactive)
	// List<Employee> employees =
	// employeeRepository.findAllEmployeesByCampusId(campusId);
	//
	// // 2. Safety check (optional, but good practice)
	// if (employees == null || employees.isEmpty()) {
	// return new ArrayList<>();
	// }
	//
	// return employees.stream()
	// .map(emp -> {
	// // 3. (Optional Tip) visually distinguish Inactive users in the dropdown
	// String fullName = emp.getFirst_name() + " " + emp.getLast_name();
	//
	// // // Assuming you have a 'status' field. If not, remove this if-block.
	// // if (emp.getStatus() != null &&
	// !emp.getStatus().equalsIgnoreCase("Active")) {
	// // fullName += " (Inactive)";
	// // }
	//
	// return new GenericDropdownDTO(emp.getEmp_id(), fullName);
	// })
	// .collect(Collectors.toList());
	// }

	public List<GenericDropdownDTO> getCitiesByDistrictId(Integer districtId) {
		return cityRepository.findByDistrictId(districtId).stream()
				.map(city -> new GenericDropdownDTO(city.getCityId(), city.getCityName())).collect(Collectors.toList());
	}

	public List<GenericDropdownDTO> getActiveOccupations() {
		return occupationRepository.findByIsActive(ACTIVE_STATUS).stream()
				.map(o -> new GenericDropdownDTO(o.getOccupation_id(), o.getOccupation_name()))
				.collect(Collectors.toList());
	}

	public List<GenericDropdownDTO> getActiveOrientations() {
		return orientationRepository.findByIsActive(ACTIVE_STATUS).stream()
				.map(o -> new GenericDropdownDTO(o.getOrientationId(), o.getOrientationName()))
				.collect(Collectors.toList());
	}

	public List<Dgmdto> getBuildingDataBasedOnMainBuilding(int cmsid, int buildingid) {

		Building building = buildingRepository.findbyBuildingIdAndCampusId(buildingid, cmsid);

		if (building == null) {
			return null; // or throw custom exception
		}

		// if not main building -> return null (or return empty dto)
		if (building.getIsMainBuilding() != 1) {
			return null;
		}

		List<CampusContact> contacts = campusContactRepository.findByCmpsIds(cmsid);

		if (contacts == null || contacts.isEmpty()) {
			return null;
		}

		List<Dgmdto> resultdata = contacts.stream().map(e -> {
			Dgmdto dtoobj = new Dgmdto();

			if (!"PRINCIPAL".equals(e.getDesignation())) {
				dtoobj.setDegination(e.getDesignation());
				dtoobj.setName(e.getEmpName());
			}

			return dtoobj;
		}).collect(Collectors.toList());
		return resultdata != null ? resultdata : null;
	}

	public List<GenericDropdownDTO> getCampusesByCity(int cityId, String category) {
		// 1. If category provided, filter by City + Business Type
		if (category != null && !category.trim().isEmpty()) {
			java.util.Optional<com.employee.entity.BusinessType> business = businessTypeRepository
					.findByBusinessTypeNameIgnoreCase(category.trim());

			if (business.isPresent()) {
				return campusRepository.findCampusDropdownByCityAndBusinessId(cityId,
						business.get().getBusinessTypeId());
			}
		}

		// 2. Fallback: return all active campuses for the city
		List<Campus> campuses = campusRepository.findCampusesByCityId(cityId);

		return campuses.stream()
				.map(c -> new GenericDropdownDTO(c.getCampusId(), c.getCampusName()))
				.collect(Collectors.toList());
	}

	public List<CmpsOrientationsDTO> getActiveOrientationsByCampus(Integer cmpsId) {

		// 1. Fetch raw data using the Interface Projection
		List<CmpsOrientationsDTO> projections = cmpsorientationRepository.findActiveByCmpsId(cmpsId);

		// 2. Convert Projection -> DTO
		return projections.stream().map(proj -> CmpsOrientationsDTO.builder()
				.cmpsOrientationId(proj.getCmpsOrientationId()) // Mapping ID
				.orientationId(proj.getOrientationId()) // Orientation ID
				.orientationName(proj.getOrientationName()) // Orientation Name
				.build()).collect(Collectors.toList());
	}

	public List<GenericDropdownDTO> getAllGroups() {
		return orientationGroupRepository.findByIsActive(ACTIVE_STATUS).stream()
				.map(g -> new GenericDropdownDTO(g.getGroupId(), g.getGroupName()))
				.collect(Collectors.toList());
	}

	public List<GenericDropdownDTO> getAllEmployeesByCampusId(Integer campusId) {
		return employeeRepository.findAllEmployeesByCampusId(campusId).stream()
				.filter(emp -> emp.getPayRollId() != null && !emp.getPayRollId().trim().isEmpty())
				.map(emp -> {
					String pId = emp.getPayRollId().trim();
					String fullName = (emp.getFirst_name() != null ? emp.getFirst_name() : "") + " "
							+ (emp.getLast_name() != null ? emp.getLast_name() : "");
					return new GenericDropdownDTO(emp.getEmp_id(), fullName.trim() + " (" + pId + ")");
				})
				.collect(Collectors.toList());
	}

	public List<GenericDropdownDTO> getActiveEmployeesWithPayrollByCampusId(Integer campusId) {
		return employeeRepository.findActiveEmployeesWithPayrollByCampusId(campusId).stream()
				.map(emp -> new GenericDropdownDTO(
						emp.getEmp_id(),
						emp.getFirst_name() + " " + emp.getLast_name() + " (" + emp.getPayRollId() + ")"))
				.collect(Collectors.toList());
	}

	public List<GenericDropdownDTO> getActiveRoles() {
		return roleRepository.findByIsActive(ACTIVE_STATUS).stream()
				.map(role -> new GenericDropdownDTO(role.getRoleId(), role.getRoleName()))
				.collect(Collectors.toList());
	}

	public List<GenericDropdownDTO> getDesignationsByEmployeeType(Integer empTypeId) {

		List<Designation> designations = designationRepo.findDesignationsByEmpType(empTypeId);

		return designations.stream()
				.map(d -> new GenericDropdownDTO(
						d.getDesignation_id(),
						d.getDesignation_name()))
				.toList();
	}
}
