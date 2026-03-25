//package com.employee.entity;
// 
//import java.time.LocalDate;
//import java.time.LocalDateTime;
// 
//import jakarta.persistence.Column;
//import jakarta.persistence.Entity;
//import jakarta.persistence.FetchType;
//import jakarta.persistence.GeneratedValue;
//import jakarta.persistence.GenerationType;
//import jakarta.persistence.Id;
//import jakarta.persistence.JoinColumn;
//import jakarta.persistence.ManyToOne;
//import jakarta.persistence.Table;
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;
// 
//@Data
//@AllArgsConstructor
//@NoArgsConstructor
//@Entity
//@Table(name = "sce_skill_test_detl", schema = "sce_employee")
//public class SkillTestDetails {
//    
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "skill_test_detl_id")
//    private Integer skillTestDetlId;
//    
//    // Foreign key to Employee table
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "emp_id")
//    private Employee empId;
//    
//    @Column(name = "aadhaar_no")
//    private Long aadhaar_no;
//    
//    @Column(name = "pan_number", length = 15)
//    private String panNumber;
//    
//    @Column(name = "ssc_no", length = 20)
//    private String sscNo;
//    
//    @Column(name = "previous_chaitanya_id", length = 50)
//    private String previous_chaitanya_id;
// 
//    @Column(name = "first_name", length = 50)
//    private String firstName;
//    
//    @Column(name = "last_name", length = 50)
//    private String lastName;
// 
//    @Column(name = "dob")
//    private LocalDate dob;
//    
//    @Column(name = "age")
//    private Integer age;
// 
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "gender_id")
//    private Gender gender;
//    
//    @Column(name = "contact_no")
//    private Long contact_number;
//    
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "marital_status_id")
//    private MaritalStatus maritalStatus;
// 
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "qualification_id")
//    private Qualification qualification;
//    
//    @Column(name = "highest_qualification", length = 100)
//    private String highestQualification;
// 
//    @Column(name = "total_experience")
//    private Double totalExperience; // Changed from Long to Double (float8 in DDL)
// 
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "join_type_id")
//    private JoiningAs joiningAs;
// 
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "stream_id")
//    private Stream stream;
// 
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "subject_id")
//    private Subject subject;
//    
//    @Column(name = "sections_to_be_handled", length = 100)
//    private String sectionsToBeHandled;
//    
//    @Column(name = "proposed_ctc_per_month")
//    private Double proposedCtcPerMonth;
// 
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "emp_level_id")
//    private EmployeeLevel employeeLevel;
//    
//    @Column(name = "agreed_periods_per_week")
//    private Integer agreedPeriodsPerWeek;
//    
//    // Foreign key to Employee table (referrer)
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "refered_by_id")
//    private Employee referedBy;
//    
//    @Column(name = "is_active", nullable = false)
//    private Integer isActive ;
//    
//    @Column(name = "created_by", nullable = false)
//    private Integer createdBy;
//    
//    @Column(name = "created_date")
//    private LocalDateTime createdDate;
//    
//    @Column(name = "updated_by")
//    private Integer updatedBy;
//    
//    @Column(name = "updated_date")
//    private LocalDateTime updatedDate;
//    
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "emp_structure_id")
//    private Structure empStructure;
//    
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "emp_grade_id")
//    private Grade empGrade;
//    
//    @Column(name = "email", length = 150)
//    private String email;
//    
//    @Column(name = "temp_payroll_id", length = 50)
//    private String tempPayrollId;
//    
//    @Column(name = "password", length = 150)
//    private String password;
//    
//    // Foreign key to sce_skill_test_approval_status table
////    @ManyToOne(fetch = FetchType.LAZY)
////    @JoinColumn(name = "groupId" , referencedColumnName = "skill_test_approval_status_id")
////    private SkillTestApprovalStatus groupId;
////    
//    
//    
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "group_id") // Must match SQL column name exactly
//    private SkillTestApprovalStatus group;
//    
//    
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "cmps_id")
//    private Campus campusId;
//    
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "city_id")
//    private City cityId;
//    
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "building_id")
//    private Building buildingId;
//    
//    
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "orientation_id")
//    private Orientation orientation;
//    
//    // Foreign key to sce_emp_type table
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "emp_type_id")
//    private EmployeeType employeeType;
//    
//    
//    @Column(name="created_date",insertable = false, updatable = false)
//    private LocalDate joinDate;
//    
// 
//}
// 

package com.employee.entity;

import java.time.LocalDate;
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
@Table(name = "sce_skill_test_detl", schema = "sce_employee")
public class SkillTestDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "skill_test_detl_id")
    private Integer skillTestDetlId;

    // Foreign key to Employee table
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emp_id")
    private Employee empId;

    @Column(name = "aadhaar_no", unique = true)
    private Long aadhaar_no;

    @Column(name = "pan_number", length = 15)
    private String panNumber;

    @Column(name = "ssc_no", length = 20)
    private String sscNo;

    @Column(name = "previous_chaitanya_id", length = 50)
    private String previous_chaitanya_id;

    @Column(name = "first_name", length = 50)
    private String firstName;

    @Column(name = "last_name", length = 50)
    private String lastName;

    @Column(name = "dob")
    private LocalDate dob;

    @Column(name = "age")
    private Integer age;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gender_id")
    private Gender gender;

    @Column(name = "contact_no")
    private Long contact_number;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "marital_status_id")
    private MaritalStatus maritalStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "qualification_id")
    private Qualification qualification;

    @Column(name = "highest_qualification", length = 100)
    private String highestQualification;

    @Column(name = "total_experience")
    private Double totalExperience; // Changed from Long to Double (float8 in DDL)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "join_type_id")
    private JoiningAs joiningAs;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stream_id")
    private Stream stream;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id")
    private Subject subject;

    @Column(name = "sections_to_be_handled", length = 100)
    private String sectionsToBeHandled;

    @Column(name = "proposed_ctc_per_month")
    private Double proposedCtcPerMonth;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emp_level_id")
    private EmployeeLevel employeeLevel;

    @Column(name = "agreed_periods_per_week")
    private Integer agreedPeriodsPerWeek;

    // Foreign key to Employee table (referrer)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "refered_by_id")
    private Employee referedBy;

    @Column(name = "is_active", nullable = false)
    private Integer isActive = 1;

    @Column(name = "created_by", nullable = false)
    private Integer createdBy;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Column(name = "updated_by")
    private Integer updatedBy;

    @Column(name = "updated_date")
    private LocalDateTime updatedDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emp_structure_id")
    private Structure empStructure;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emp_grade_id")
    private Grade empGrade;

    @Column(name = "email", length = 150)
    private String email;

    @Column(name = "temp_payroll_id", length = 50)
    private String tempPayrollId;

    @Column(name = "password", length = 150)
    private String password;

    // Foreign key to sce_emp_type table
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emp_type_id")
    private EmployeeType employeeType;
    @Column(name = "created_date", insertable = false, updatable = false)
    private LocalDate joinDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private OrientationGroup orientationGroup;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orientation_id")
    private Orientation orientation;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cmps_id")
    private Campus campus; // Make sure you have a 'Cmps' entity class

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "city_id")
    private City city; // Make sure you have a 'City' entity class

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "building_id")
    private Building building;

    @Column(name = "campus_category", length = 50)
    private String campusCategory;

}