package com.employee.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.employee.entity.OrientationGroup;

@Repository
public interface OrientationGroupRepository extends JpaRepository<OrientationGroup, Integer> {

    List<OrientationGroup> findByIsActive(int isActive);
}
