package com.employee.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.employee.entity.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {

    // Find all active roles
    List<Role> findByIsActive(Integer isActive);

    // Find role by name
    Role findByRoleName(String roleName);
}
