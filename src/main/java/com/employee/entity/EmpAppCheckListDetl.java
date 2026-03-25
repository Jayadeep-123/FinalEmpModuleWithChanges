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
import org.hibernate.annotations.DynamicUpdate;
 
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "sce_emp_app_check_list_detl", schema = "sce_employee")
// @DynamicUpdate  // Only update changed columns, prevents Hibernate from selecting all columns
public class EmpAppCheckListDetl {
   
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "emp_app_check_list_detl_id")
    private Integer empAppCheckListDetlId;
   
    @Column(name = "check_list_detl_name", length = 200)
    private String checkListDetlName; // Optional - nullable
   
    @Column(name = "short_name", length = 20)
    private String shortName; // Optional - nullable
   
    @Column(name = "is_active", nullable = false)
    private Integer isActive = 1; // Default 1
   
    // NOTE: check_list_id column does NOT exist in sce_emp_app_check_list_detl table
    // Checklist IDs are stored in Employee.emp_app_check_list_detl_id as comma-separated string
   
    @Column(name = "check_list_name", length = 128)
    private String checkListName; // Optional - nullable
   
    @Column(name = "created_on", length = 50)
    private String createdOn; // Optional - nullable
   
    // Audit fields - required NOT NULL columns
    @Column(name = "created_by", nullable = false)
    private Integer createdBy = 1; // Default to 1 if not provided
   
    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;
   
    @Column(name = "updated_by")
    private Integer updatedBy;
   
    @Column(name = "updated_date")
    private LocalDateTime updatedDate;
}
