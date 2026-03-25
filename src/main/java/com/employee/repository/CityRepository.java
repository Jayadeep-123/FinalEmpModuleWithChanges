package com.employee.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.employee.entity.City;



@Repository
public interface CityRepository extends JpaRepository<City, Integer>{
	  // Add this line
	// In your CityRepository interface
//	List<City> findByDistrictStateStateIdAndStatus(int stateId, int status);
//    List<City> findByDistrictDistrictIdAndStatus(int districtId,int status);
//    List<City> findByStatus(int status);
	
	// Find all cities by district ID (no is_active filter - column doesn't exist in table)
	@Query("SELECT c FROM City c WHERE c.district.districtId = :districtId")
	List<City> findByDistrictId(@Param("districtId") Integer districtId);
}