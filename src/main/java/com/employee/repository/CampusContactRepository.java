package com.employee.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.employee.dto.CampusContactDTO;
import com.employee.entity.Campus;
import com.employee.entity.CampusContact;

public interface CampusContactRepository extends JpaRepository<CampusContact, Integer> {
	 @Query("SELECT new com.employee.dto.CampusContactDTO(c.empName, c.designation, c.contactNo, c.email) " +
	           "FROM CampusContact c " +
	           "WHERE c.cmpsId.campusId = :campusId AND c.isActive = 1")
	    List<CampusContactDTO> findActiveContactsByCampusId(@Param("campusId") Integer campusId);
	 
	 Optional<CampusContact> findByCmpsIdAndDesignation(Campus cmpsId, String designation);
	 
	 @Query("SELECT c FROM  CampusContact c where c.cmpsId.campusId=:campusid")
		public List<CampusContact> findByCmpsIds(@Param("campusid") int campusid);
}