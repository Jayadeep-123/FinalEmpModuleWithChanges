package com.employee.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

// Make sure this is the correct path to your new DTO
import com.employee.dto.CategoryInfoDTO1;
import com.employee.entity.EmpSubject;
import com.employee.entity.Employee;

@Repository
public interface EmpSubjectRepository extends JpaRepository<EmpSubject, Integer> {

	/**
	 * This query is now updated to use your new 'CategoryInfoDTO1'.
	 * The class name is changed, and the fields in the 'NEW'
	 * clause are re-ordered to match the DTO's constructor.
	 */
	@Query("SELECT NEW com.employee.dto.CategoryInfoDTO1(" +
			"et.emp_type, et.emp_type_id, " +
			"d.department_name, d.department_id, " +
			"des.designation_name, des.designation_id, " +
			"COALESCE(s.subject_name, null), COALESCE(s.subject_id, null), " +
			"COALESCE(o.orientationId, null), COALESCE(o.orientationName, null), " +
			"COALESCE(sub.agree_no_period, null)" +
			") " +
			"FROM Employee e " +
			"JOIN e.employee_type_id et " +
			"JOIN e.department d " +
			"JOIN e.designation des " +
			"LEFT JOIN EmpSubject sub ON sub.emp_id = e " +
			"LEFT JOIN sub.subject_id s " +
			"LEFT JOIN sub.orientation_id o " +
			"WHERE e.tempPayrollId = :payrollId " +
			"AND e.is_active = 1 " +
			"AND et.isActive = 1")
	// Note the change in the return type to use the new DTO
	List<CategoryInfoDTO1> findCategoryInfoByPayrollId(@Param("payrollId") String payrollId);

	/**
	 * This query was already correct.
	 */
	@Query("SELECT es FROM EmpSubject es WHERE es.emp_id.emp_id = :empId AND es.is_active = 1")
	List<EmpSubject> findActiveSubjectsByEmpId(@Param("empId") Integer empId);

	@Query("SELECT es FROM EmpSubject es JOIN FETCH es.subject_id s "
			+ "WHERE es.emp_id IN :employees AND es.is_active = 1") // <--- ADDED isActive = 1 CHECK
	List<EmpSubject> findByEmp_idInWithSubjectName(List<Employee> employees);
}