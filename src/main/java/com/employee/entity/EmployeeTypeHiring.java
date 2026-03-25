package com.employee.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "sce_emp_type_hiring", schema = "sce_employee")
public class EmployeeTypeHiring {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int emp_type_hiring_id;

    private String emp_type_hiring_name;

    @Column(name = "is_active", nullable = false)
    private int is_active = 1;

    @Column(name = "created_by", nullable = false)
    private int created_by;

    @Column(name = "created_date", nullable = false, updatable = false)
    private LocalDateTime created_date;

    @Column(name = "updated_by")
    private Integer updated_by;

    @Column(name = "updated_date")
    private LocalDateTime updated_date;
}
