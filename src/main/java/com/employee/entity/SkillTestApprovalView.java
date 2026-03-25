package com.employee.entity;

import java.sql.Date;

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
@Table(name = "sce_skill_test_approval", schema = "sce_employee")
public class SkillTestApprovalView {

    @Id
    @Column(name = "temp_employee_id")
    private String tempEmployeeId;

    @Column(name = "employee_name")
    private String employeeName;

    @Column(name = "date_of_birth")
    private Date dateOfBirth;

    @Column(name = "age")
    private Double age;

    @Column(name = "gender_id")
    private Integer genderId;

    @Column(name = "gender_name")
    private String genderName;

    @Column(name = "contact_no")
    private Long contactNo;

    @Column(name = "previous_chaitanya_id")
    private String previousChaitanyaId;

    @Column(name = "email")
    private String email;

    @Column(name = "aadhaar_no")
    private String aadhaarNo;

    @Column(name = "total_experience")
    private Double totalExperience;

    @Column(name = "qualification_id")
    private Integer qualificationId;

    @Column(name = "qualification_name")
    private String qualificationName;

    @Column(name ="highest_qualification")
    private String highestQualification;

    @Column(name = "join_type_id")
    private Integer joinTypeId;

    @Column(name = "join_type")
    private String joinType;

    @Column(name = "emp_level_id")
    private Integer empLevelId;

    @Column(name = "level_name")
    private String levelName;

    @Column(name = "emp_structure_id")
    private Integer empStructureId;

    @Column(name = "structure_name")
    private String structureName;

    @Column(name = "emp_grade_id")
    private Integer empGradeId;

    @Column(name = "grade_name")
    private String gradeName;
    
    @Column(name = "subject_name")
    private String subjectName;
    
    @Column (name = "subject_id")
    private Integer subjectId;
    
    
}