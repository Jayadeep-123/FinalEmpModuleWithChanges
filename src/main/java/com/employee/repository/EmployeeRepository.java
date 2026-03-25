package com.employee.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.employee.dto.FullBasicInfoDto;
import com.employee.entity.EmpQualification;
import com.employee.entity.Employee;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Integer>, EmployeeRepositoryCustom {

        @Query("SELECT e FROM Employee e WHERE e.emp_id = :id AND e.is_active = :is_active")
        Optional<Employee> findByIdAndIs_active(@Param("id") Integer id, @Param("is_active") int is_active);

        Optional<Employee> findByTempPayrollId(String tempPayrollId);

        /**
         * Finds an employee by the 'payRollId' field.
         * Note: This method name relies on Spring Data naming conventions.
         * If your field is 'payRollId', this works automatically.
         */
        Optional<Employee> findByPayRollId(String payrollId);

        /**
         * Finds all employees by their active status.
         */
        @Query("SELECT e FROM Employee e WHERE e.is_active = :status")
        List<Employee> findByIsActive(@Param("status") int status);

        @Query("SELECT e FROM Employee e "
                        + "LEFT JOIN FETCH e.campus_id c "
                        + "LEFT JOIN FETCH c.city "
                        + "LEFT JOIN FETCH e.employee_manager_id m "
                        + "LEFT JOIN FETCH e.employee_replaceby_id r "
                        + "LEFT JOIN FETCH e.employee_hired h "
                        + "LEFT JOIN FETCH e.modeOfHiring_id moh "
                        + "LEFT JOIN FETCH e.workingMode_id wm "
                        + "LEFT JOIN FETCH e.join_type_id jat "
                        + "WHERE e.tempPayrollId = :tempPayrollId")
        Optional<Employee> findWorkingInfoByTempPayrollId(@Param("tempPayrollId") String tempPayrollId);

        @Query("SELECT e FROM Employee e "
                        + "LEFT JOIN FETCH e.qualification_id q "
                        + "WHERE e.tempPayrollId = :tempPayrollId")
        Optional<Employee> findHighestQualificationDetailsByTempPayrollId(@Param("tempPayrollId") String tempPayrollId);

        @Query("SELECT eq FROM EmpQualification eq "
                        + "JOIN eq.emp_id e "
                        + "LEFT JOIN FETCH eq.qualification_degree_id qd "
                        + "WHERE e.tempPayrollId = :tempPayrollId AND eq.qualification_id.qualification_id = e.qualification_id.qualification_id")
        Optional<EmpQualification> findHighestEmpQualificationRecord(@Param("tempPayrollId") String tempPayrollId);

        @Query("SELECT e FROM Employee e JOIN FETCH e.designation d "
                        + "WHERE e.department.department_id = :departmentId "
                        + "AND e.campus_id.campusId = :campusId "
                        + "AND e.is_active = 1")
        List<Employee> findActiveEmployeesByDepartmentAndCampus(
                        @Param("departmentId") int departmentId,
                        @Param("campusId") int campusId);

        @Query("SELECT e FROM Employee e "
                        + "JOIN FETCH e.designation d "
                        + "JOIN FETCH e.gender g "
                        + "WHERE e.campus_id.campusId = :campusId AND e.is_active = 1")
        List<Employee> findActiveEmployeesByCampusId(@Param("campusId") int campusId);

        @Query("SELECT e FROM Employee e "
                        + "LEFT JOIN FETCH e.designation d "
                        + "LEFT JOIN FETCH e.gender g "
                        + "WHERE e.campus_id.campusId = :campusId")
        List<Employee> findAllEmployeesByCampusId(@Param("campusId") Integer campusId);

        @Query("SELECT e FROM Employee e "
                        + "LEFT JOIN FETCH e.designation d "
                        + "LEFT JOIN FETCH e.gender g "
                        + "WHERE e.campus_id.campusId = :campusId AND e.is_active = 1 AND e.payRollId IS NOT NULL")
        List<Employee> findActiveEmployeesWithPayrollByCampusId(@Param("campusId") Integer campusId);

        @Query("SELECT e FROM Employee e WHERE e.primary_mobile_no = :mobileNo AND e.emp_id != :empId")
        Optional<Employee> findByPrimary_mobile_noExcludingEmpId(@Param("mobileNo") Long mobileNo,
                        @Param("empId") Integer empId);

        @Query(value = "SELECT temp_payroll_id FROM sce_employee.sce_emp " +
                        "WHERE temp_payroll_id LIKE :keyPrefix " +
                        "ORDER BY LENGTH(temp_payroll_id) DESC, temp_payroll_id DESC LIMIT 1", nativeQuery = true)
        String findMaxTempPayrollIdByKey(@Param("keyPrefix") String keyPrefix);

        // Find max permanent payroll ID that matches the prefix pattern
        @Query("SELECT MAX(e.payRollId) FROM Employee e WHERE e.payRollId IS NOT NULL AND e.payRollId LIKE :keyPrefix")
        String findMaxPayrollIdByKey(@Param("keyPrefix") String keyPrefix);

        @Query("SELECT e FROM Employee e WHERE e.primary_mobile_no = :mobileNo")
        Optional<Employee> findByPrimary_mobile_no(@Param("mobileNo") Long mobileNo);

        // --- THE FIX IS HERE ---
        // 1. Changed 'e.payroll_id' (DB Column) to 'e.payRollId' (Java Variable)
        // 2. Ensure this matches your Employee.java field exactly.
        @Query("SELECT e FROM Employee e WHERE e.payRollId = :payrollId")
        Optional<Employee> findByPayrollId(@Param("payrollId") String payrollId);

        @Query("SELECT e FROM Employee e WHERE e.payRollId = :payrollId AND e.is_active = :isActive")
        Optional<Employee> findByPayRollIdAndIs_active(@Param("payrollId") String payrollId,
                        @Param("isActive") int isActive);

        // NOTE: All 31 search query methods have been replaced by dynamic queries in
        // EmployeeRepositoryImpl
        // The dynamic queries handle all filter combinations automatically

        // These methods are still used for other purposes (returning List<Employee>):
        @Query("SELECT e FROM Employee e "
                        + "JOIN FETCH e.department d "
                        + "JOIN FETCH e.campus_id c "
                        + "JOIN FETCH c.city city "
                        + "LEFT JOIN FETCH e.modeOfHiring_id moh "
                        + "WHERE city.cityId = :cityId AND e.is_active = 1")
        List<Employee> findByCityId(@Param("cityId") Integer cityId);

        @Query("SELECT e FROM Employee e "
                        + "JOIN FETCH e.department d "
                        + "JOIN FETCH e.campus_id c "
                        + "JOIN FETCH c.city city "
                        + "JOIN FETCH e.employee_type_id et "
                        + "LEFT JOIN FETCH e.modeOfHiring_id moh "
                        + "WHERE city.cityId = :cityId AND et.emp_type_id = :employeeTypeId AND e.is_active = 1")
        List<Employee> findByCityIdAndEmployeeTypeId(
                        @Param("cityId") Integer cityId,
                        @Param("employeeTypeId") Integer employeeTypeId);

        @Query("SELECT e FROM Employee e "
                        + "JOIN FETCH e.department d "
                        + "JOIN FETCH e.employee_type_id et "
                        + "LEFT JOIN FETCH e.modeOfHiring_id moh "
                        + "WHERE et.emp_type_id = :employeeTypeId AND e.is_active = 1")
        List<Employee> findByEmployeeTypeId(@Param("employeeTypeId") Integer employeeTypeId);

        @Query("""
                        SELECT new com.employee.dto.FullBasicInfoDto(
                            e.emp_id,
                            e.tempPayrollId,
                            e.payRollId,
                            e.first_name,
                            e.last_name,
                            e.date_of_join,
                            e.age,
                            e.primary_mobile_no,
                            e.secondary_mobile_no,
                            e.email,

                            g.gender_id,
                            g.genderName,

                            et.emp_type_id,
                            et.emp_type,

                            d.department_id,
                            d.department_name,

                            des.designation_id,
                            des.designation_name,

                            c.category_id,
                            c.category_name,

                            q.qualification_id,
                            q.qualification_name,

                            wm.emp_work_mode_id,
                            wm.work_mode_type,

                            cm.campusId,
                            cm.campusName,
                            cm.cmps_code,
                            cm.cmps_type,


                            b.buildingId,
                            b.buildingName,

                            m.emp_id,
                            concat(m.first_name, ' ', m.last_name),

                            ed.adhaar_name,
                            ed.adhaar_no,
                            ed.pancard_no,
                            ed.date_of_birth,
                            ed.personal_email,
                            ed.emergency_ph_no,
                            ed.fatherName,
                            ed.uanNo,

                            bg.bloodGroupId,
                            bg.bloodGroupName,

                            cs.caste_id,
                            cs.caste_type,

                            r.religion_id,
                            r.religion_type,

                            ms.marital_status_id,
                            ms.marital_status_type,

                            rel.studentRelationId,
                            rel.studentRelationType,

                            mh.mode_of_hiring_id,
                            mh.mode_of_hiring_name,

                            j.join_type_id,
                            j.join_type,

                            ed.adhaar_enrolment_no,
                            e.ssc_no,

                            ref.emp_id,
                            concat(ref.first_name, ' ', ref.last_name),

                            hired.emp_id,
                            concat(hired.first_name, ' ', hired.last_name),

                            rm.emp_id,
                            concat(rm.first_name, ' ', rm.last_name),

                            rep.emp_id,
                            concat(rep.first_name, ' ', rep.last_name),

                            e.total_experience,
                            epd.pre_esi_no,
                            e.contract_start_date,
                            e.contract_end_date,
                            eth.emp_type_hiring_id,
                            eth.emp_type_hiring_name
                        )
                        FROM Employee e
                        LEFT JOIN EmpDetails ed ON ed.employee_id.emp_id = e.emp_id AND ed.is_active = 1
                        LEFT JOIN EmpPfDetails epd ON epd.employee_id.emp_id = e.emp_id AND epd.is_active = 1
                        LEFT JOIN e.gender g
                        LEFT JOIN e.employee_type_id et
                        LEFT JOIN e.department d
                        LEFT JOIN e.designation des
                        LEFT JOIN e.category c
                        LEFT JOIN e.qualification_id q
                        LEFT JOIN e.workingMode_id wm
                        LEFT JOIN e.campus_id cm
                        LEFT JOIN e.building_id b
                        LEFT JOIN e.modeOfHiring_id mh
                        LEFT JOIN e.join_type_id j
                        LEFT JOIN e.employee_manager_id m

                        LEFT JOIN e.employee_reference ref
                        LEFT JOIN e.employee_hired hired
                        LEFT JOIN e.employee_reporting_id rm
                        LEFT JOIN e.employee_replaceby_id rep
                        LEFT JOIN e.employee_type_hiring_id eth

                        LEFT JOIN ed.bloodGroup_id bg
                        LEFT JOIN ed.caste_id cs
                        LEFT JOIN ed.religion_id r
                        LEFT JOIN ed.marital_status_id ms
                        LEFT JOIN ed.relation_id rel
                        WHERE e.tempPayrollId = :tempPayrollId
                        """)
        Optional<FullBasicInfoDto> findFullEmployeeDetailsByTempPayrollId(
                        @Param("tempPayrollId") String tempPayrollId);

        @Query("SELECT e FROM Employee e " +
                        "LEFT JOIN FETCH e.employee_manager_id " +
                        "LEFT JOIN FETCH e.employee_reporting_id " +
                        "WHERE e.payRollId IN :payrollIds OR e.tempPayrollId IN :payrollIds")
        List<Employee> findAllByPayRollIdInOrTempPayrollIdIn(@Param("payrollIds") List<String> payrollIds);

        @Query("SELECT COUNT(e) > 0 FROM Employee e WHERE e.ssc_no = :sscNo")
        boolean existsBySsc_no(@Param("sscNo") Long sscNo);

        @Query("SELECT COUNT(e) > 0 FROM Employee e WHERE e.primary_mobile_no = :mobileNo")
        boolean existsByPrimary_mobile_no(@Param("mobileNo") Long mobileNo);

        @Query("SELECT COUNT(e) > 0 FROM Employee e WHERE e.secondary_mobile_no = :mobileNo")
        boolean existsBySecondary_mobile_no(@Param("mobileNo") Long mobileNo);

        @Query(value = "SELECT role_id FROM sce_admin.sce_user_admin WHERE emp_id = :empId LIMIT 1", nativeQuery = true)
        List<Integer> findRoleIdByEmpId(@Param("empId") Integer empId);

        @Query(value = "SELECT role_name FROM sce_admin.sce_user_admin WHERE emp_id = :empId LIMIT 1", nativeQuery = true)
        List<String> findRoleNameByEmpId(@Param("empId") Integer empId);

        @Query("SELECT COUNT(e) > 0 FROM Employee e WHERE e.email = :email")
        boolean existsByEmail(@Param("email") String email);

}