package com.employee.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.employee.entity.Designation;

@Repository
public interface DesignationRepository extends JpaRepository<Designation, Integer> {

    // Find by ID and is_active = 1
    @Query("SELECT d FROM Designation d WHERE d.designation_id = :id AND d.isActive = :isActive")
    Optional<Designation> findByIdAndIsActive(@Param("id") Integer id, @Param("isActive") Integer isActive);

    /**
     * Finds active designations for a specific department.
     * CORRECTED: Added @Query to manually define the query because
     * Spring was incorrectly looking for 'department.id' instead of
     * 'department.department_id'.
     */
    @Query("SELECT d FROM Designation d " +
            "WHERE d.department.department_id = :departmentId " +
            "AND d.isActive = :isActiveStatus")
    List<Designation> findByDepartmentId_DepartmentIdAndIsActive(
            @Param("departmentId") int departmentId,
            @Param("isActiveStatus") int isActiveStatus);

    // Find all active designations
    List<Designation> findByIsActive(int isActive);
    
    @Query("""
 	       SELECT d FROM Designation d
 	       WHERE (:empTypeId IS NULL
 	              OR d.department.empTypeId.emp_type_id = :empTypeId)
 	       """)
 	List<Designation> findDesignationsByEmpType(@Param("empTypeId") Integer empTypeId);
}