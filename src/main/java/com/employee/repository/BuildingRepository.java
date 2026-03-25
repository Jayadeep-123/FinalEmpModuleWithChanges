//package com.employee.repository;
//
//import java.util.List;
//
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
//import org.springframework.stereotype.Repository;
//
//import com.employee.dto.GenericDropdownDTO;
//import com.employee.entity.Building;
//
//@Repository
//public interface BuildingRepository extends JpaRepository<Building, Integer> {
//	
//	 @Query("SELECT new com.employee.dto.GenericDropdownDTO(b.buildingId, b.buildingName) " +
//	           "FROM Building b " +
//	           "WHERE b.campusId.campusId = :campusId AND b.isActive = 1")
//	    List<GenericDropdownDTO> findBuildingsByCampusId(@Param("campusId") int campusId);
//	 
//	 
//	 @Query("SELECT b  from Building b WHERE b.buildingId=:bid  AND b.campusId.campusId=:campusid")
//	 public Building findbyBuildingIdAndCampusId(@Param("bid") int bid,@Param("campusid")int campusid);
//	 
//	 
//	 @Query("SELECT b FROM Building b WHERE b.campusId.campusId = :campusId")
//	 List<Building> findBuildingsByCampusId(Integer campusId);
//
//}
//


package com.employee.repository;
 
import java.util.List;
 
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
 
import com.employee.dto.GenericDropdownDTO;
import com.employee.entity.Building;
 
@Repository
public interface BuildingRepository extends JpaRepository<Building, Integer> {
 
    @Query("SELECT new com.employee.dto.BuildingDropdownDTO(b.buildingId, b.buildingName, b.isMainBuilding) " +
            "FROM Building b " +
            "WHERE b.campusId.campusId = :campusId AND b.isActive = 1")
    List<com.employee.dto.BuildingDropdownDTO> findBuildingsByCampusId(@Param("campusId") int campusId);
 
    @Query("SELECT b  from Building b WHERE b.buildingId=:bid  AND b.campusId.campusId=:campusid")
    public Building findbyBuildingIdAndCampusId(@Param("bid") int bid, @Param("campusid") int campusid);
 
    @Query("SELECT b FROM Building b WHERE b.campusId.campusId = :campusId")
    List<Building> findBuildingsByCampusId(Integer campusId);
 
    @Query("SELECT b FROM Building b WHERE b.campusId.campusId = :campusId AND b.isMainBuilding = 1 AND b.isActive = 1")
    java.util.Optional<Building> findMainBuildingByCampusId(@Param("campusId") int campusId);
}
 