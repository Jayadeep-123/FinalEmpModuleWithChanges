package com.employee.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.employee.entity.CampusProfileView;

@Repository
public interface CampusProfileViewRepository extends JpaRepository<CampusProfileView, Integer>{
	
	@Query("SELECT c FROM CampusProfileView c WHERE c.cmpsId = :cmpsId")
    List<CampusProfileView> findCampusDetailsByCmpsId(@Param("cmpsId") int cmpsId);
}
