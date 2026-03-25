package com.employee.service;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.employee.dto.EmployeeDropdownDTO;
import com.employee.dto.EmployeeFullDetailsDTO;
import com.employee.dto.GenericDropdownDTO;
import com.employee.entity.BusinessType;
import com.employee.entity.CampusProfileView;
import com.employee.entity.EmpDetails;
import com.employee.entity.EmpSubject;
import com.employee.entity.Employee;
import com.employee.repository.BusinessTypeRepository;
import com.employee.repository.CampusProfileViewRepository;
import com.employee.repository.CampusRepository;
import com.employee.repository.DepartmentRepository;
import com.employee.repository.EmpDetailsRepository;
import com.employee.repository.EmpSubjectRepository;
import com.employee.repository.EmployeeRepository;

@Service
public class CampusFlowService {

    @Autowired
    private CampusRepository campusRepository;
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private CampusProfileViewRepository campusProfileViewRepository;
    @Autowired
    private EmpDetailsRepository empDetailsRepository;
    @Autowired
    private EmpSubjectRepository empSubjectRepository;
    @Autowired
    private BusinessTypeRepository businessTypeRepository;
    @Autowired
    private DepartmentRepository departmentRepository;

    public List<BusinessType> getAllBusinessTypes() {
        return businessTypeRepository.findAll();
    }

    public List<GenericDropdownDTO> getCampusesByCityAndBusinessForDropdown(int cityId, int businessId) {
        // Calls the new repository method with both parameters
        return campusRepository.findCampusDropdownByCityAndBusinessId(cityId, businessId);
    }

    public List<EmployeeDropdownDTO> getActiveEmployeesByDepartmentAndCampusForDropdown(
            int departmentId, int campusId) {

        // 1. Fetch the full Employee entities using the custom repository method
        List<Employee> employees = employeeRepository.findActiveEmployeesByDepartmentAndCampus(departmentId, campusId); // <---
                                                                                                                        // PASSING
                                                                                                                        // BOTH
                                                                                                                        // IDS

        // 2. Map the entities to the DTOs in the service layer - Filter to only include
        // employees with payroll ID
        return employees.stream()
                .filter(emp -> emp.getPayRollId() != null && !emp.getPayRollId().isEmpty())
                .map(this::convertToDropdownDTO)
                .collect(Collectors.toList());
    }

    /**
     * Helper method to map a single Employee entity to EmployeeDropdownDTO.
     */
    private EmployeeDropdownDTO convertToDropdownDTO(Employee employee) {
        String fullName = employee.getFirst_name() + " " + employee.getLast_name();

        int designationId = employee.getDesignation().getDesignation_id();
        String designationName = employee.getDesignation().getDesignation_name();

        return new EmployeeDropdownDTO(
                employee.getEmp_id(),
                fullName,
                employee.getPayRollId(),
                employee.getPrimary_mobile_no(),
                employee.getEmail(),
                designationId,
                designationName);
    }

    public List<CampusProfileView> getCampusDetailsByCmpsId(int cmpsId) {
        // Calls the repository method created above
        return campusProfileViewRepository.findCampusDetailsByCmpsId(cmpsId);
    }

    public List<EmployeeFullDetailsDTO> getEmployeeFullDetailsByCampusId(int campusId) {

        // 1. Fetch primary employee data (already filters by e.is_active = 1)
        List<Employee> employees = employeeRepository.findActiveEmployeesByCampusId(campusId);
        if (employees.isEmpty()) {
            return List.of();
        }

        // 2. Fetch secondary data (EmpDetails for DOB) - NOW FILTERS BY ed.is_active =
        // 1
        List<EmpDetails> allEmpDetails = empDetailsRepository.findByEmployee_idIn(employees);

        Map<Integer, EmpDetails> detailsMap = allEmpDetails.stream()
                .collect(Collectors.toMap(
                        e -> e.getEmployee_id().getEmp_id(), // Key is emp_id
                        e -> e, // Value is the EmpDetails object
                        (existing, replacement) -> existing // <-- ADD THIS MERGE FUNCTION!
                ));

        // 3. Fetch tertiary data (Subjects taught) - NOW FILTERS BY es.is_active = 1
        List<EmpSubject> allEmpSubjects = empSubjectRepository.findByEmp_idInWithSubjectName(employees);
        Map<Integer, List<String>> subjectsMap = allEmpSubjects.stream()
                .collect(Collectors.groupingBy(
                        es -> es.getEmp_id().getEmp_id(),
                        Collectors.mapping(es -> es.getSubject_id().getSubject_name(), Collectors.toList())));

        // 4. Map and Calculate - Filter to only include employees with payroll ID
        return employees.stream()
                .filter(emp -> emp.getPayRollId() != null && !emp.getPayRollId().isEmpty())
                .map(emp -> convertToFullDetailsDTO(
                        emp,
                        detailsMap.get(emp.getEmp_id()),
                        subjectsMap.getOrDefault(emp.getEmp_id(), List.of())))
                .collect(Collectors.toList());
    }

    private EmployeeFullDetailsDTO convertToFullDetailsDTO(
            Employee emp, EmpDetails details, List<String> subjects) {

        // Calculate Age
        Integer age = null;
        if (details != null && details.getDate_of_birth() != null) {
            age = calculateAge(details.getDate_of_birth());
        }

        return new EmployeeFullDetailsDTO(
                emp.getEmp_id(),
                emp.getFirst_name() + " " + emp.getLast_name(),
                emp.getPayRollId(), // Will be null if not set
                emp.getPrimary_mobile_no(),
                emp.getEmail(),
                emp.getDesignation().getDesignation_name(),
                emp.getGender().getGenderName(),
                age,
                subjects);
    }

    private Integer calculateAge(java.sql.Date dob) {
        LocalDate birthDate = dob.toLocalDate();
        LocalDate currentDate = LocalDate.now();
        return Period.between(birthDate, currentDate).getYears();
    }

    public List<GenericDropdownDTO> getActiveDepartments(String categoryName) {
        if (categoryName != null && !categoryName.isEmpty()) {
            return departmentRepository.findActiveDepartmentNamesWithTypesByCategory(categoryName);
        }
        return departmentRepository.findActiveDepartmentNamesWithTypes();
    }
}
