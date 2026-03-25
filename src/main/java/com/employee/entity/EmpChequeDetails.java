package com.employee.entity;

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
@Table(name = "sce_emp_cheque_details", schema = "sce_employee")
public class EmpChequeDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "emp_cheque_details_id")
	private Integer empChequeDetailsId;

	@ManyToOne
	@JoinColumn(name = "emp_id", nullable = false)
	private Employee empId; // Required NOT NULL - Foreign key to Employee

	@Column(name = "cheque_no", nullable = false)
	private Long chequeNo; // Required NOT NULL - int8 (bigint)

	@Column(name = "cheque_bank_name", nullable = false, length = 50)
	private String chequeBankName; // Required NOT NULL - varchar(50)

	@Column(name = "cheque_bank_ifsc_code", nullable = false, length = 20)
	private String chequeBankIfscCode; // Required NOT NULL - varchar(20)

	@Column(name = "is_active", nullable = false)
	private Integer isActive; // Default 1

	// Audit fields - required NOT NULL columns
	@Column(name = "created_by", nullable = false)
	private Integer createdBy = 1; // Default to 1 if not provided

	@Column(name = "created_date", nullable = false)
	private LocalDateTime createdDate;

	@Column(name = "updated_by")
	private Integer updatedBy; // Optional - nullable

	@Column(name = "updated_date")
	private LocalDateTime updatedDate; // Optional - nullable

	@ManyToOne
	@JoinColumn(name = "emp_doc_id", nullable = true)
	private EmpDocuments empDocId; // Optional - Foreign key to EmpDocuments
}
