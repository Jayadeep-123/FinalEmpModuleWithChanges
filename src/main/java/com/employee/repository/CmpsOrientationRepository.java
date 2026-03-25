package com.employee.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.employee.dto.CmpsOrientationsDTO;
import com.employee.entity.CmpsOrientation;

public interface CmpsOrientationRepository extends JpaRepository<CmpsOrientation, Integer> {
	
	@Query("SELECT c FROM CmpsOrientation c WHERE c.isActive = :isActive")
    List<CmpsOrientation> findByIsActive(@Param("isActive") Integer isActive);
	
	
	@Query(value = "SELECT co.cmps_orientation_id AS cmpsOrientationId, " +
            "co.orientation_id AS orientationId, " +
            "so.orientation_name AS orientationName " +
            "FROM sce_course.sce_cmps_orientation co " +
            "JOIN sce_course.sce_orientation so ON co.orientation_id = so.orientation_id " +
            "WHERE co.cmps_id = :cmpsId AND co.is_active = 1", 
    nativeQuery = true)
List<CmpsOrientationsDTO> findActiveByCmpsId(@Param("cmpsId") Integer cmpsId);
}
