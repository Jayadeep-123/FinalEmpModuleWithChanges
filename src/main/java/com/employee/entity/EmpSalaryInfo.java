package com.employee.entity;
 
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
 
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
 
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "sce_emp_sal_info", schema = "sce_employee")
public class EmpSalaryInfo {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "emp_sal_info_id")
	private Integer empSalInfoId;
	
	@ManyToOne
	@JoinColumn(name = "emp_id", nullable = false)
	private Employee empId; // Required NOT NULL - Foreign key to Employee
	
	@Column(name = "payroll_id")
	private String payrollId; // Optional - can be null (will be updated later)
	
	@ManyToOne
	@JoinColumn(name = "emp_payment_type_id", nullable = true)
	private EmpPaymentType empPaymentType; // Optional - can be null
	
	// Database has monthly_take_home (bytea), not monthly_ctc (float8)
	@Column(name = "monthly_take_home", nullable = false)
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	private byte[] monthlyTakeHome; // Required NOT NULL - bytea (binary data)
	
	@Column(name = "ctc_words")
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	private byte[] ctcWords; // Optional - nullable, bytea (binary data)
	
	@Column(name = "yearly_ctc", nullable = false)
	@Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	private byte[] yearlyCtc; // Required NOT NULL - bytea (binary data)
	
	@ManyToOne
	@JoinColumn(name = "emp_structure_id", nullable = false)
	private EmpStructure empStructure; // Required NOT NULL - Foreign key to sce_emp_structure
	
	@ManyToOne
	@JoinColumn(name = "grade_id", nullable = true)
	private EmpGrade grade; // Optional - nullable (FK to sce_emp_grade)
	
	@Column(name = "is_active", nullable = false)
	private Integer isActive = 1; // Default 1
	
	@Column(name = "temp_payroll_id", length = 50)
	private String tempPayrollId; // Optional - nullable
	
	@ManyToOne
	@JoinColumn(name = "cost_center_id", nullable = true)
	private CostCenter costCenter; // Optional - nullable (FK to sce_emp_costcenter)
	
	@Column(name = "is_pf_eligible", nullable = false)
	private Integer isPfEligible; // Required NOT NULL - Default 0 (int2)
	
	@Column(name = "is_esi_eligible", nullable = false)
	private Integer isEsiEligible; // Required NOT NULL - Default 0 (int2)
	
	// Audit fields - required NOT NULL columns
	@Column(name = "created_by", nullable = false)
	private Integer createdBy = 1; // Default to 1 if not provided
	
	@Column(name = "created_date", nullable = false)
	private LocalDateTime createdDate;
	
	@Column(name = "updated_by")
	private Integer updatedBy;
	
	@Column(name = "updated_date")
	private LocalDateTime updatedDate;
	
	// ========== Helper Methods for byte[] to Double/String Conversion ==========
	// These methods allow the service layer to work with Double/String while entity stores byte[]
	
	/**
	 * Convert monthlyTakeHome (byte[]) to Double
	 * Assumes byte[] contains 8 bytes representing a double value
	 */
	public Double getMonthlyTakeHomeAsDouble() {
		if (monthlyTakeHome == null || monthlyTakeHome.length == 0) {
			return null;
		}
		try {
			// Try to parse as string first (if stored as text)
			String str = new String(monthlyTakeHome, StandardCharsets.UTF_8);
			return Double.parseDouble(str);
		} catch (Exception e) {
			// If not a string, try to read as binary double
			if (monthlyTakeHome.length >= 8) {
				return ByteBuffer.wrap(monthlyTakeHome).getDouble();
			}
			return null;
		}
	}
	
	/**
	 * Set monthlyTakeHome from Double value
	 * Stores as UTF-8 string bytes for simplicity
	 */
	public void setMonthlyTakeHomeFromDouble(Double value) {
		if (value == null) {
			this.monthlyTakeHome = null;
		} else {
			this.monthlyTakeHome = String.valueOf(value).getBytes(StandardCharsets.UTF_8);
		}
	}
	
	/**
	 * Convert yearlyCtc (byte[]) to Double
	 */
	public Double getYearlyCtcAsDouble() {
		if (yearlyCtc == null || yearlyCtc.length == 0) {
			return null;
		}
		try {
			String str = new String(yearlyCtc, StandardCharsets.UTF_8);
			return Double.parseDouble(str);
		} catch (Exception e) {
			if (yearlyCtc.length >= 8) {
				return ByteBuffer.wrap(yearlyCtc).getDouble();
			}
			return null;
		}
	}
	
	/**
	 * Set yearlyCtc from Double value
	 */
	public void setYearlyCtcFromDouble(Double value) {
		if (value == null) {
			this.yearlyCtc = null;
		} else {
			this.yearlyCtc = String.valueOf(value).getBytes(StandardCharsets.UTF_8);
		}
	}
	
	/**
	 * Convert ctcWords (byte[]) to String
	 */
	public String getCtcWordsAsString() {
		if (ctcWords == null || ctcWords.length == 0) {
			return null;
		}
		return new String(ctcWords, StandardCharsets.UTF_8);
	}
	
	/**
	 * Set ctcWords from String value
	 */
	public void setCtcWordsFromString(String value) {
		if (value == null) {
			this.ctcWords = null;
		} else {
			this.ctcWords = value.getBytes(StandardCharsets.UTF_8);
		}
	}
	
}
 
 
 