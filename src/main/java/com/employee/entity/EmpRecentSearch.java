package com.employee.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "sce_emp_recent_search", schema = "sce_employee")
public class EmpRecentSearch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "emp_recent_search_id")
    private Integer empRecentSearchId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "log_in_emp_id")
    private Employee logInEmployee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emp_id", nullable = false)
    private Employee employee;

    @Column(name = "emp_name", nullable = false, length = 250)
    private String empName;

    @Column(name = "payroll_id", length = 30)
    private String payrollId;

    @Column(name = "temp_payroll_id", length = 50)
    private String tempPayrollId;

    @Column(name = "department_name", nullable = false, length = 50)
    private String departmentName;

    @Column(name = "join_type", nullable = false, length = 30)
    private String joinType;

    @Column(name = "level_name", length = 30)
    private String levelName;

    @Column(name = "log_in", nullable = false)
    private LocalDateTime logIn;

    @Column(name = "log_out")
    private LocalDateTime logOut;

    @Column(name = "photo_path", length = 250)
    private String photoPath;

    @Column(name = "created_by", nullable = false)
    private Integer createdBy;

    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate = LocalDateTime.now();

    @Column(name = "updated_by")
    private Integer updatedBy;

    @Column(name = "updated_date")
    private LocalDateTime updatedDate;

    @Column(name = "cmps_id")
    private Integer cmpsId;

    @Column(name = "cmps_name", length = 250)
    private String cmpsName;

    @Column(name = "business_name")
    private Integer businessName;
}
