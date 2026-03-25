package com.employee.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.employee.entity.BusinessType;

@Repository
public interface BusinessTypeRepository extends JpaRepository<BusinessType, Integer> {

    java.util.Optional<BusinessType> findByBusinessTypeNameIgnoreCase(String businessTypeName);

}
