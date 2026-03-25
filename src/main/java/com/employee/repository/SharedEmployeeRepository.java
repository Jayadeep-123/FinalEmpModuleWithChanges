package com.employee.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.employee.entity.SharedEmployee;

@Repository
public interface SharedEmployeeRepository extends JpaRepository<SharedEmployee, Integer> {

	/**
	 * Find all active shared employee records for a given employee
	 */
	@Query("SELECT se FROM SharedEmployee se WHERE se.empId.emp_id = :empId AND se.isActive = 1")
	List<SharedEmployee> findActiveByEmpId(@Param("empId") Integer empId);

	/**
	 * Find shared employee record by employee ID and campus ID
	 */
	@Query("SELECT se FROM SharedEmployee se WHERE se.empId.emp_id = :empId AND se.cmpsId.campusId = :campusId AND se.isActive = 1")
	Optional<SharedEmployee> findByEmpIdAndCampusId(@Param("empId") Integer empId, @Param("campusId") Integer campusId);

	/**
	 * Find all shared employee records (active and inactive) for a given employee and campus
	 */
	@Query("SELECT se FROM SharedEmployee se WHERE se.empId.emp_id = :empId AND se.cmpsId.campusId = :campusId")
	List<SharedEmployee> findAllByEmpIdAndCampusId(@Param("empId") Integer empId, @Param("campusId") Integer campusId);

	/**
	 * Deactivate all shared employee records for a given employee and campus
	 */
	@Query("UPDATE SharedEmployee se SET se.isActive = 0 WHERE se.empId.emp_id = :empId AND se.cmpsId.campusId = :campusId")
	void deactivateByEmpIdAndCampusId(@Param("empId") Integer empId, @Param("campusId") Integer campusId);
}

