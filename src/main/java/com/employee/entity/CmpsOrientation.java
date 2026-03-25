package com.employee.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "sce_cmps_orientation", schema = "sce_course")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CmpsOrientation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cmps_orientation_id", unique = true, nullable = false)
    private Integer cmpsOrientationId;

    @Column(name = "cmps_id")
    private Integer cmpsId;

    @Column(name = "orientation_id")
    private Integer orientationId;

    @Column(name = "acdc_year_id", nullable = false)
    private Integer acdcYearId;

    @Column(name = "orientation_batch_id")
    private Integer orientationBatchId;

    @Column(name = "orientation_fee", nullable = false)
    private Double orientationFee;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Integer isActive = 1;

    @CreationTimestamp
    @Column(name = "created_date", nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @Column(name = "created_by", nullable = false)
    @Builder.Default
    private Integer createdBy = 1;

    @UpdateTimestamp
    @Column(name = "updated_date")
    private LocalDateTime updatedDate;

    @Column(name = "updated_by")
    private Integer updatedBy;
}