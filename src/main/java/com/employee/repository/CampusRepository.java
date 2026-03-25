package com.employee.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.employee.dto.CampusDto;
import com.employee.dto.GenericDropdownDTO;
import com.employee.entity.Campus;
import com.employee.entity.City;

@Repository
public interface CampusRepository extends JpaRepository<Campus, Integer> {

        /**
         * 1. REQUIRED FOR SKILL TEST SERVICE
         * Used to initialize ID counters on startup.
         * Note: If your Campus entity field is named 'cmps_code', change 'c.code' to
         * 'c.cmps_code' below.
         */
        @Query("SELECT c FROM Campus c WHERE c.code IS NOT NULL")
        List<Campus> findAllWithCodeNotNull();

        /**
         * 2. Find Campus by ID and Active Status
         */
        Optional<Campus> findByCampusIdAndIsActive(Integer campusId, Integer isActive);

        /**
         * 3. DTO Projection: Active Campus Details
         */
        @Query("SELECT new com.employee.dto.CampusDto(" +
                        "c.campusId, c.campusName, c.cmps_type, c.cmps_code, " +
                        "c.city.cityId, c.city.cityName) " +
                        "FROM Campus c " +
                        "WHERE c.campusId = :campusId AND c.isActive = 1")
        Optional<CampusDto> findActiveCampusById(@Param("campusId") int campusId);

        /**
         * 4. DTO Projection: Dropdown List by City and Business Type
         */
        @Query("SELECT new com.employee.dto.GenericDropdownDTO(c.campusId, c.campusName) "
                        + "FROM Campus c WHERE c.city.cityId = :cityId "
                        + "AND c.businessType.businessTypeId = :businessId "
                        + "AND c.isActive = 1")
        List<GenericDropdownDTO> findCampusDropdownByCityAndBusinessId(
                        @Param("cityId") int cityId,
                        @Param("businessId") int businessId);

        /**
         * 5. Find all active campuses (is_active = 1)
         */
        @Query("SELECT c FROM Campus c WHERE c.isActive = :isActive")
        List<Campus> findByIsActive(@Param("isActive") Integer isActive);

        @Query("SELECT c FROM Campus c WHERE c.city_id = :cityId AND c.isActive = 1")
        List<Campus> findCampusesByCityId(@Param("cityId") int cityId);

        @Query("SELECT ca.city FROM Campus ca WHERE ca.campusId = :campusid")
        City findByCampusId(@Param("campusid") int campusid);

        @Query("SELECT new com.employee.dto.GenericDropdownDTO(c.campusId, c.campusName) " +
                        "FROM Campus c WHERE c.isActive = :isActive AND c.businessType.businessTypeId = :businessId")
        List<GenericDropdownDTO> findByIsActiveAndBusinessId(@Param("isActive") Integer isActive,
                        @Param("businessId") Integer businessId);

        @Query("SELECT new com.employee.dto.GenericDropdownDTO(c.campusId, c.campusName) " +
                        "FROM Campus c WHERE c.isActive = :isActive AND LOWER(c.businessType.businessTypeName) = LOWER(:businessName)")
        List<GenericDropdownDTO> findByIsActiveAndBusinessName(@Param("isActive") Integer isActive,
                        @Param("businessName") String businessName);
}