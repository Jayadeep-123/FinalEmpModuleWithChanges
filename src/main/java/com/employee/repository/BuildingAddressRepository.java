package com.employee.repository;
 
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
 
import com.employee.entity.BuildingAddress;
 
@Repository
public interface BuildingAddressRepository  extends JpaRepository<BuildingAddress, Integer>{
	
	// Recommended: Filter by both building ID and the specific address type string
	@Query("SELECT ba FROM BuildingAddress ba WHERE ba.building.buildingId = :buildingId AND ba.address_type = :type")
    BuildingAddress findByBuildingIdAndAddressType(@Param("buildingId") Integer buildingId, @Param("type") String type);
 
    @Query("SELECT ba FROM BuildingAddress ba WHERE ba.building.buildingId = :buildingId")
    BuildingAddress findAddressByBuildingId(@Param("buildingId") Integer buildingId);
 
//	BuildingAddress findAddressByBuildingId(int buildingId);
 
    // Alternative using Spring Data naming convention:
    // BuildingAddress findByBuilding_BuildingIdAndAddress_type(Integer buildingId, String type)
}
 