////package com.employee.repository;
////
////import java.util.List;
////import org.springframework.data.jpa.repository.JpaRepository;
////import org.springframework.data.jpa.repository.Query;
////import org.springframework.data.repository.query.Param;
////import org.springframework.stereotype.Repository;
////
////import com.employee.dto.EmpFamilyDetailsDTO;
////import com.employee.dto.FamilyDetailsDTO;
////import com.employee.entity.EmpFamilyDetails; // Use your entity class
////
////@Repository
////public interface EmpFamilyDetailsRepository extends JpaRepository<EmpFamilyDetails, Integer> { 
////
////	/**
////	 * This query is now 100% correct and matches all your entity files.
////	 * The startup error will be fixed.
////	 */
////	@Query("SELECT NEW com.employee.dto.FamilyDetailsDTO(" +
////	           
////	           // --- 1. THIS LINE IS NOW FIXED ---
////	           "rel.studentRelationType, " +  // Was 'rel.relationName'
////	           
////	           "CONCAT(fd.first_name, ' ', fd.last_name), " + 
////	           "bg.bloodGroupName, " + // This one was correct
////	           "g.genderName, " +      // This one was correct
////	           "fd.nationality, " +    
////	           "fd.occupation, " +     
////	           "fd.date_of_birth" +      
////	           ") " +
////	           "FROM EmpFamilyDetails fd " + // Use your entity class
////	           
////	           // Join using your exact Java field names (e.g., relation_id)
////	           "LEFT JOIN fd.relation_id rel " +
////	           "LEFT JOIN fd.blood_group_id bg " +
////	           "LEFT JOIN fd.gender_id g " +
////	           
////	           // This links Family Details -> Employee -> tempPayrollId
////	           "WHERE fd.emp_id.tempPayrollId = :payrollId AND fd.is_active = 1")
////	
////	List<FamilyDetailsDTO> findFamilyDetailsByPayrollId(@Param("payrollId") String payrollId);
////
////}
//
//package com.employee.repository;
//
//import java.util.List;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
//import org.springframework.stereotype.Repository;
//
//import com.employee.dto.EmpFamilyDetailsDTO;
//import com.employee.dto.FamilyDetailsDTO;
//import com.employee.entity.EmpFamilyDetails; // Use your entity class
//
//@Repository
//public interface EmpFamilyDetailsRepository extends JpaRepository<EmpFamilyDetails, Integer> { 
//
//	/**
//	 * This is your existing query. It remains unchanged.
//	 */
//	@Query("SELECT NEW com.employee.dto.FamilyDetailsDTO(" +
//	           "rel.studentRelationType, " +
//	           "CONCAT(fd.first_name, ' ', fd.last_name), " + 
//	           "bg.bloodGroupName, " +
//	           "g.genderName, " +
//	           "fd.nationality, " +
//	           "fd.occupation, " +
//	           "fd.date_of_birth" +
//	           ") " +
//	           "FROM EmpFamilyDetails fd " +
//	           "LEFT JOIN fd.relation_id rel " +
//	           "LEFT JOIN fd.blood_group_id bg " +
//	           "LEFT JOIN fd.gender_id g " +
//	           "WHERE fd.emp_id.tempPayrollId = :payrollId AND fd.is_active = 1")
//	List<FamilyDetailsDTO> findFamilyDetailsByPayrollId(@Param("payrollId") String payrollId);
//
//	/**
//	 * --- THIS IS THE NEWLY ADDED METHOD ---
//	 * This method was required by your service class.
//	 * The SELECT NEW... fields now match your EmpFamilyDetailsDTO constructor.
//	 */
//	@Query("SELECT NEW com.employee.dto.EmpFamilyDetailsDTO(" +
//	           "fd.emp_family_detl_id, " +
//	           "fd.first_name, " +
//	           "fd.last_name, " +
//	           "fd.occupation, " +
//	           "g.genderName, " +         // Assumes Gender has genderName
//	           "bg.bloodGroupName, " +   // Assumes BloodGroup has bloodGroupName
//	           "fd.nationality, " +
//	           "rel.studentRelationType, " + // Assumes Relation has studentRelationType
//	           "fd.is_dependent, " +
//	           "fd.is_late, " +
//	           "fd.email, " +
//	           // Use COALESCE to prevent null error when mapping to a primitive long
//	           "COALESCE(fd.contact_no, 0L)" +
//	       ") " +
//	       "FROM EmpFamilyDetails fd " +
//	       // Joins to get the names from related tables
//	       "LEFT JOIN fd.gender_id g " +
//	       "LEFT JOIN fd.blood_group_id bg " +
//	       "LEFT JOIN fd.relation_id rel " +
//	       // Filter by the Employee's primary key (emp_id)
//	       "WHERE fd.emp_id.emp_id = :empId AND fd.is_active = 1")
//	List<EmpFamilyDetailsDTO> findFamilyDetailsByEmpId(@Param("empId") int empId);
//	List<EmpFamilyDetails> findByEmpId_PayRollIdAndIsActive(String payRollId, int isActive);
//	
//	List<EmpFamilyDetails> findByEmp_id_EmpId(int empId);
//
//
//}

package com.employee.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.employee.dto.EmpFamilyDetailsDTO;
import com.employee.dto.FamilyDetailsDTO;
import com.employee.entity.EmpFamilyDetails;
import com.employee.entity.Employee;

@Repository
public interface EmpFamilyDetailsRepository extends JpaRepository<EmpFamilyDetails, Integer> {

        /**
         * --- FIXED QUERY ---
         * 1. Removed CONCAT(first_name, last_name) because those fields don't exist
         * anymore.
         * 2. Used 'fd.fullName' instead.
         */
        @Query("SELECT NEW com.employee.dto.FamilyDetailsDTO(" +
                        "rel.studentRelationType, " +
                        "fd.fullName, " + // <--- FIXED: Use direct fullName field
                        "bg.bloodGroupName, " +
                        "g.genderName, " +
                        "fd.nationality, " +
                        "fd.occupation, " +
                        "fd.date_of_birth" +
                        ") " +
                        "FROM EmpFamilyDetails fd " +
                        "LEFT JOIN fd.relation_id rel " +
                        "LEFT JOIN fd.blood_group_id bg " +
                        "LEFT JOIN fd.gender_id g " +
                        "WHERE fd.emp_id.tempPayrollId = :payrollId AND fd.is_active = :isActive")
        List<FamilyDetailsDTO> findFamilyDetailsByPayrollId(
                        @Param("payrollId") String payrollId,
                        @Param("isActive") int isActive);

        /**
         * This query is correct.
         */
        @Query("SELECT e FROM EmpFamilyDetails e WHERE e.emp_id.emp_id = :emp_id AND e.is_active = 1")
        List<EmpFamilyDetails> findFamilyDetailsByEmpId(@Param("emp_id") int empId);

        @Query("SELECT fd FROM EmpFamilyDetails fd WHERE fd.emp_id.payRollId = :payrollId AND fd.is_active = :isActive")
        List<EmpFamilyDetails> findByEmp_id_PayrollIdAndIsActive(
                        @Param("payrollId") String payrollId,
                        @Param("isActive") int isActive);

        @Query("SELECT fd FROM EmpFamilyDetails fd WHERE fd.emp_id.emp_id = :empId")
        List<EmpFamilyDetails> findByEmp_id_EmpId(@Param("empId") int empId);

        @Query("SELECT f FROM EmpFamilyDetails f WHERE f.emp_id = :employee")
        List<EmpFamilyDetails> findByEmployeeEntity(@Param("employee") Employee employee);
}