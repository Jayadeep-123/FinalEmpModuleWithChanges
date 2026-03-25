package com.employee.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.employee.dto.GenericDropdownDTO;
import com.employee.entity.Department;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Integer> {

    // 1. Fetch ALL active departments
    List<Department> findByIsActive(Integer isActive);

    // 2. Fetch active department by ID
    @Query("SELECT d FROM Department d WHERE d.department_id = :id AND d.isActive = :isActive")
    Optional<Department> findByIdAndIsActive(@Param("id") Integer id, @Param("isActive") Integer isActive);

    @Query("SELECT d FROM Department d " +
            "WHERE d.empTypeId.emp_type_id = :empTypeId " +
            "AND d.departmentCategory.businessTypeId = :deptCategoryId " +
            "AND d.isActive = :isActive")
    List<Department> findByEmpTypeId_EmpTypeIdAndDepartmentCategory_BusinessTypeIdAndIsActive(
            @Param("empTypeId") int empTypeId,
            @Param("deptCategoryId") int deptCategoryId,
            @Param("isActive") Integer isActive);

    // --- NEWLY ADDED METHODS ---

    // 4. Find department by Name (Useful for duplicate checks during save)
    @Query("SELECT d FROM Department d WHERE d.department_name = :deptName AND d.isActive = :isActive")
    Optional<Department> findByDepartmentNameAndIsActive(
            @Param("deptName") String deptName,
            @Param("isActive") int isActive);

    // 5. Check if department exists by Name (Boolean return)
    @Query("SELECT CASE WHEN COUNT(d) > 0 THEN true ELSE false END FROM Department d WHERE d.department_name = :deptName AND d.isActive = 1")
    boolean existsByDepartmentName(@Param("deptName") String deptName);

    @Query("""
            SELECT new com.employee.dto.GenericDropdownDTO(
                d.department_id,
                CONCAT(d.department_name, ' - ', replace(e.emp_type,'_',' '))
            )
            FROM Department d
            JOIN d.empTypeId e
            WHERE d.isActive = 1
            """)
    List<GenericDropdownDTO> findActiveDepartmentNamesWithTypes();

    @Query("""
            SELECT new com.employee.dto.GenericDropdownDTO(
                d.department_id,
                CONCAT(d.department_name, ' - ', replace(e.emp_type,'_',' '))
            )
            FROM Department d
            JOIN d.empTypeId e
            WHERE UPPER(d.departmentCategory.businessTypeName) = UPPER(:categoryName)
            AND d.isActive = 1
            """)
    List<GenericDropdownDTO> findActiveDepartmentNamesWithTypesByCategory(@Param("categoryName") String categoryName);

}