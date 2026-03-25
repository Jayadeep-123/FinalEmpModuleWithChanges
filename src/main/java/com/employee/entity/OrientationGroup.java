//package com.employee.entity;
//
//import java.time.LocalDateTime;
//
//import jakarta.persistence.Column;
//import jakarta.persistence.Entity;
//import jakarta.persistence.GeneratedValue;
//import jakarta.persistence.GenerationType;
//import jakarta.persistence.Id;
//import jakarta.persistence.Table;
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//@Data
//@AllArgsConstructor
//@NoArgsConstructor
//@Entity
//@Table(name = "sce_orientation_group", schema = "sce_course")
//public class OrientationGroup {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "group_id")
//    private Integer groupId;
//
//    @Column(name = "group_name", nullable = false, length = 50)
//    private String groupName;
//
//    @Column(name = "is_active", nullable = false)
//    private Integer isActive; // Default 1
//
//    @Column(name = "created_date", nullable = false, updatable = false)
//    private LocalDateTime createdDate;
//
//    @Column(name = "created_by", nullable = false)
//    private Integer createdBy;
//
//    @Column(name = "updated_date")
//    private LocalDateTime updatedDate;
//
//    @Column(name = "updated_by")
//    private Integer updatedBy;
//}

package com.employee.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "sce_orientation_group", schema = "sce_course")
public class OrientationGroup {

    @Id
    @Column(name = "group_id")
    private Integer groupId;

    @Column(name = "group_name")
    private String groupName;

    @Column(name = "is_active", nullable = false)
    private Integer isActive = 1;

    @Column(name = "created_by", nullable = false)
    private Integer createdBy;

    @Column(name = "created_date", insertable = false, updatable = false)
    private java.time.LocalDateTime createdDate;

    @Column(name = "updated_by")
    private Integer updatedBy;

    @Column(name = "updated_date")
    private java.time.LocalDateTime updatedDate;
}
