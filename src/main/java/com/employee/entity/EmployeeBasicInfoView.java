package com.employee.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity

@Table(name = "sce_emp_basic_info", schema = "sce_employee")
public class EmployeeBasicInfoView {

    @Id
    @Column(name = "employee_id")
    private Integer employeeId;

    @Column(name = "employee_name")
    private String employeeName;

    @Column(name = "adhaar_no")
    private Long adhaarNo;

    @Column(name = "emergency_ph_no")
    private String emergencyPhNo;

    @Column(name = "emergency_cont_name")
    private String emergencyContName;

    @Column(name = "relation_id")
    private Integer relationId;

    @Column(name = "relation_type")
    private String relationType;

    @Column(name = "religion_id")
    private Integer religionId;

    @Column(name = "religion_type")
    private String religionType;

    @Column(name = "payroll_id")
    private String payrollId;

    @Column(name = "adhaar_enrolment_no")
    private String adhaarEnrolmentNo;

    @Column(name = "pancard_no")
    private String pancardNo;

    @Column(name = "category_id")
    private Integer categoryId;

    @Column(name = "category_name")
    private String categoryName;

    @Column(name = "father_name")
    private String fatherName;

    @Column(name = "mother_name")
    private String motherName;

    @Column(name = "pre_esi_no")
    private Long preEsiNo;

    @Column(name = "uan_no")
    private Long uanNo;

    @Column(name = "total_experience")
    private Double totalExperience;

    @Column(name = "highest_qualification_id")
    private Integer highestQualificationId;

    @Column(name = "highest_qualification_name")
    private String highestQualificationName;

    @Column(name = "pre_chaitanya_id")
    private String preChaitanyaId;
}