package com.employee.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.employee.entity.CmpsOrg;
import com.employee.dto.GenericDropdownDTO;

public interface CmpsOrgRepository extends JpaRepository<CmpsOrg, Integer> {

    @Query("SELECT new com.employee.dto.GenericDropdownDTO(o.organizationId, o.organizationName) " +
           "FROM CmpsOrg co " +
           "JOIN co.orgId o " +
           "JOIN co.cmpsId c " +
           "WHERE c.campusId = :campusId AND co.isActive = 1 AND o.isActive = 1")
    List<GenericDropdownDTO> findOrganizationsByCampusId(@Param("campusId") int campusId);
}
