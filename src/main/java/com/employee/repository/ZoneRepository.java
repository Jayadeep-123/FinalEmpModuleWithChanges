package com.employee.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.employee.entity.Zone;

public interface ZoneRepository extends JpaRepository<Zone, Integer> {

}
