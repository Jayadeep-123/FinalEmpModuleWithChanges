package com.employee.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.employee.entity.Relation;

public interface RelationRepository extends JpaRepository<Relation, Integer>{

}
