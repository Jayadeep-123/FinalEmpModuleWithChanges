package com.employee.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import com.employee.entity.SkillTestDetails;
import com.employee.dto.SkillTestDashboardDto;

@Repository
public interface SkillTestDetailsRepository extends JpaRepository<SkillTestDetails, Integer> {

        @Query("SELECT new com.employee.dto.SkillTestDashboardDto(" +
                        "COALESCE(d.firstName, '') || ' ' || COALESCE(d.lastName, ''), " +
                        "e.payRollId, " +
                        "d.tempPayrollId, " +
                        "d.previous_chaitanya_id, " +
                        "d.joinDate, " +
                        "c.cityName, " +
                        "ca.campusName, " +
                        "g.genderName, " +
                        "s.groupName) " +
                        "FROM SkillTestDetails d " +
                        "LEFT JOIN d.empId e " +
                        "LEFT JOIN d.city c " +
                        "LEFT JOIN d.campus ca " +
                        "LEFT JOIN d.gender g " +
                        "LEFT JOIN d.orientationGroup s")
        List<SkillTestDashboardDto> getSkillTestDashboardDetails();

        @Query(value = "SELECT temp_payroll_id FROM sce_employee.sce_skill_test_detl " +
                        "WHERE temp_payroll_id LIKE :keyPrefix " +
                        "ORDER BY LENGTH(temp_payroll_id) DESC, temp_payroll_id DESC LIMIT 1", nativeQuery = true)
        String findMaxTempPayrollIdByKey(@Param("keyPrefix") String keyPrefix);

        @Query("SELECT std FROM SkillTestDetails std WHERE std.tempPayrollId = :tempPayrollId")
        Optional<SkillTestDetails> findByTempPayrollId(@Param("tempPayrollId") String tempPayrollId);

        // FIX: Changed aadhaarNo from String to Long
        @Query("SELECT std FROM SkillTestDetails std WHERE std.aadhaar_no = :aadhaarNo AND std.contact_number = :contactNumber")
        Optional<SkillTestDetails> findByAadhaarNoAndContactNumber(
                        @Param("aadhaarNo") Long aadhaarNo,
                        @Param("contactNumber") Long contactNumber);

        // FIX: Changed aadhaarNo from String to Long
        @Query("SELECT std FROM SkillTestDetails std WHERE std.aadhaar_no = :aadhaarNo")
        Optional<SkillTestDetails> findByAadhaarNo(@Param("aadhaarNo") Long aadhaarNo);

        @Query("SELECT COUNT(std) > 0 FROM SkillTestDetails std WHERE std.aadhaar_no = :aadhaarNo")
        boolean existsByAadhaar_no(@Param("aadhaarNo") Long aadhaarNo);

        @Query("SELECT std FROM SkillTestDetails std WHERE std.contact_number = :contactNumber")
        Optional<SkillTestDetails> findByContactNumber(@Param("contactNumber") Long contactNumber);

        // FIX: Changed aadhaarNo from String to Long
        @Query("SELECT std FROM SkillTestDetails std WHERE std.aadhaar_no = :aadhaarNo AND std.isActive = 1")
        Optional<SkillTestDetails> findActiveByAadhaarNo(@Param("aadhaarNo") Long aadhaarNo);

        @Query("SELECT std FROM SkillTestDetails std WHERE std.contact_number = :contactNumber AND std.isActive = 1")
        Optional<SkillTestDetails> findActiveByContactNumber(@Param("contactNumber") Long contactNumber);

        @Query("SELECT std FROM SkillTestDetails std WHERE std.tempPayrollId = :tempPayrollId AND std.isActive = 1")
        Optional<SkillTestDetails> findActiveByTempPayrollId(@Param("tempPayrollId") String tempPayrollId);

        @Query("SELECT std FROM SkillTestDetails std WHERE std.isActive = 1 AND NOT EXISTS " +
                        "(SELECT str FROM SkillTestResult str WHERE str.skillTestDetlId = std AND str.isActive = 1)")
        List<SkillTestDetails> findActiveWithoutResults();

        List<SkillTestDetails> findByIsActive(Integer isActive);

        List<SkillTestDetails> findByIsActiveAndCampusCategory(Integer isActive, String campusCategory);
}