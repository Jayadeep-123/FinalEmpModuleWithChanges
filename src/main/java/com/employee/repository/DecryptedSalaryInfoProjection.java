package com.employee.repository;

import java.time.LocalDateTime;

/**
 * Projection interface for capturing decrypted salary info from native query.
 */
public interface DecryptedSalaryInfoProjection {
    Integer getEmp_sal_info_id();

    Integer getEmp_id();

    String getPayroll_id();

    Integer getEmp_payment_type_id();

    // Decrypted fields
    String getMonthly_take_home();

    String getCtc_words();

    String getYearly_ctc();

    Integer getEmp_structure_id();

    Integer getGrade_id();

    String getTemp_payroll_id();

    Integer getCost_center_id();

    Integer getIs_pf_eligible();

    Integer getIs_esi_eligible();

    Integer getIs_active();

    Integer getCreated_by();

    LocalDateTime getCreated_date();

    Integer getUpdated_by();

    LocalDateTime getUpdated_date();

    // Name fields
    String getEmp_structure_name();

    String getGrade_name();

    String getCost_center_name();
}
