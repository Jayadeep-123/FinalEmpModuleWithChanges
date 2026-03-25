package com.employee.repository;
 
import java.util.List;
import java.util.Optional;
 
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
 
import com.employee.entity.Organization;
 

@Repository
public interface OrganizationRepository extends JpaRepository<Organization, Integer> {
    
//    List<Organization> findByIsActive(int isActive);

    // New method: Fetches orgs where isActive matches AND both payroll columns have data
	List<Organization> findByIsActiveAndPayrollCodeIsNotNullAndPayrollMaxNoIsNotNull(int isActive);    // Note: @Lock annotation removed due to database permission issues
    // The pessimistic lock required row-level locking permissions which are not available
    // Warning: This may allow race conditions when incrementing payrollMaxNo concurrently
    // For production, consider: 1) Granting SELECT/UPDATE permissions on sce_campus.sce_organization
    //                          2) Using optimistic locking with @Version column
    //                          3) Using database sequences for payroll number generation
    @Override
    Optional<Organization> findById(Integer orgId);
}