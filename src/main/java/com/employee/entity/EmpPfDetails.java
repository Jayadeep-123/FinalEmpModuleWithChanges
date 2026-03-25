
package com.employee.entity;

import java.sql.Date;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name="sce_emp_pf_esi_uan_info",schema="sce_employee")
public class EmpPfDetails {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private int  emp_pf_esi_uan_info_id;
	private String pf_no;
	
	private Date pf_join_date;
	
	
	@Column(name = "uan_no")
	private Long uan_no; // int8 in database - bigint
	
	@Column(name = "pre_esi_no")
	private Long pre_esi_no; // int8 in database - bigint
	
	@Column(name = "esi_no")
	private Long esi_no; // int8 in database - bigint
	
	private int is_active;
	
	@ManyToOne
    @JoinColumn(name = "emp_id")
    private Employee employee_id;
	
	// Audit fields - required NOT NULL columns
	@Column(name = "created_by", nullable = false)
	private Integer created_by = 1; // Default to 1 if not provided
	
	@Column(name = "created_date", nullable = false)
	private LocalDateTime created_date;
	
	@Column(name = "updated_by")
	private Integer updated_by;
	
	@Column(name = "updated_date")
	private LocalDateTime updated_date;

}
