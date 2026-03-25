package com.employee.repository;

import com.employee.entity.Structure;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface StructureRepository extends JpaRepository<Structure, Integer> {
    // Spring Data auto-generates the query:
    List<Structure> findByIsActive(int isActive);
  
}