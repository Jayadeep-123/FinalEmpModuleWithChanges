package com.employee.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.employee.entity.EmpRecentSearch;

import jakarta.transaction.Transactional;

@Repository
public interface EmpRecentSearchRepository extends JpaRepository<EmpRecentSearch, Integer> {

        @Query("SELECT e FROM EmpRecentSearch e WHERE e.logInEmployee.emp_id = :logInEmpId AND e.logOut IS NULL")
        List<EmpRecentSearch> findByLogInEmployee_EmpIdAndLogOutIsNull(@Param("logInEmpId") Integer logInEmpId);

        @Query("SELECT e FROM EmpRecentSearch e WHERE e.logInEmployee.emp_id = :logInEmpId AND e.employee.emp_id = :searchEmpId AND e.logOut IS NULL")
        Optional<EmpRecentSearch> findByLogInEmployee_EmpIdAndEmployee_EmpIdAndLogOutIsNull(
                        @Param("logInEmpId") Integer logInEmpId, @Param("searchEmpId") Integer searchEmpId);

        // Find ANY existing record for this pair to reuse it (Across sessions)
        @Query("SELECT e FROM EmpRecentSearch e WHERE e.logInEmployee.emp_id = :logInEmpId AND e.employee.emp_id = :searchEmpId ORDER BY e.logIn DESC LIMIT 1")
        Optional<EmpRecentSearch> findTopByLogInEmployee_EmpIdAndEmployee_EmpIdOrderByLogInDesc(
                        @Param("logInEmpId") Integer logInEmpId, @Param("searchEmpId") Integer searchEmpId);

        @Query("SELECT e FROM EmpRecentSearch e WHERE e.logInEmployee.emp_id = :logInEmpId ORDER BY e.logIn DESC LIMIT 5")
        List<EmpRecentSearch> findByLogInEmployee_EmpIdOrderByLogInDesc(@Param("logInEmpId") Integer logInEmpId);

        @Modifying
        @Transactional
        @Query("UPDATE EmpRecentSearch e SET e.logOut = :logOutTime WHERE e.logInEmployee.emp_id = :logInEmpId AND e.logOut IS NULL")
        int updateLogOutTimeForPendingRecords(@Param("logInEmpId") Integer logInEmpId,
                        @Param("logOutTime") LocalDateTime logOutTime);
}
