package com.employee.repository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.employee.dto.AdvancedEmployeeSearchRequestDTO;
import com.employee.dto.EmployeeSearchRequestDTO;
import com.employee.dto.EmployeeSearchResponseDTO;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Repository
public class EmployeeRepositoryImpl implements EmployeeRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Page<EmployeeSearchResponseDTO> searchEmployeesDynamic(EmployeeSearchRequestDTO searchRequest,
            Pageable pageable) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        // Create criteria query for DTO projection
        CriteriaQuery<EmployeeSearchResponseDTO> query = cb.createQuery(EmployeeSearchResponseDTO.class);
        Root<com.employee.entity.Employee> e = query.from(com.employee.entity.Employee.class);

        // Joins
        Join<?, ?> d = e.join("department", JoinType.LEFT);
        Join<?, ?> c = e.join("campus_id", JoinType.LEFT);
        Join<?, ?> city = c.join("city", JoinType.LEFT);
        Join<?, ?> s = c.join("state", JoinType.LEFT);
        Join<?, ?> moh = e.join("modeOfHiring_id", JoinType.LEFT);
        Join<?, ?> et_join = e.join("employee_type_id", JoinType.LEFT);
        Join<?, ?> bt_proj = c.join("businessType", JoinType.LEFT);

        // Build SELECT clause (DTO projection - 14 fields)
        query.select(cb.construct(
                EmployeeSearchResponseDTO.class,
                e.get("emp_id"),
                cb.concat(
                        cb.concat(
                                cb.coalesce(e.get("first_name"), cb.literal("")),
                                cb.literal(" ")),
                        cb.coalesce(e.get("last_name"), cb.literal(""))),
                e.get("payRollId"),
                cb.coalesce(d.get("department_name"), cb.literal("N/A")),
                cb.coalesce(moh.get("mode_of_hiring_name"), cb.literal("N/A")),
                e.get("tempPayrollId"),
                s.get("stateId"),
                cb.coalesce(s.get("stateName"), cb.literal("N/A")),
                city.get("cityId"),
                cb.coalesce(city.get("cityName"), cb.literal("N/A")),
                c.get("campusId"),
                cb.coalesce(c.get("campusName"), cb.literal("N/A")),
                cb.coalesce(bt_proj.get("businessTypeName"), cb.literal("N/A")),
                et_join.get("emp_type_id"),
                cb.coalesce(et_join.get("emp_type"), cb.literal("N/A"))));

        // Build WHERE clause dynamically
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(e.get("is_active"), 1));
        predicates.add(cb.isNotNull(e.get("payRollId"))); // Only show employees with generated payroll ID

        // Combined Search: payrollId OR First Name OR Last Name
        if (searchRequest.getPayrollId() != null && !searchRequest.getPayrollId().trim().isEmpty()) {
            String searchValue = searchRequest.getPayrollId().trim();
            String namePattern = "%" + searchValue.toLowerCase() + "%";

            Predicate payrollPredicate = cb.equal(e.get("payRollId"), searchValue);
            Predicate firstNamePredicate = cb.like(cb.lower(e.get("first_name")), namePattern);
            Predicate lastNamePredicate = cb.like(cb.lower(e.get("last_name")), namePattern);

            // Full Name match: check if pattern matches concatenated first + last name
            Predicate fullNamePredicate = cb.like(
                    cb.lower(cb.concat(
                            cb.concat(cb.coalesce(e.get("first_name"), cb.literal("")), cb.literal(" ")),
                            cb.coalesce(e.get("last_name"), cb.literal("")))),
                    namePattern);

            predicates.add(cb.or(payrollPredicate, firstNamePredicate, lastNamePredicate, fullNamePredicate));
        }

        // Optional: cityId
        if (searchRequest.getCityId() != null) {
            Predicate cityMatch = cb.equal(city.get("cityId"), searchRequest.getCityId());
            if (searchRequest.getPayrollId() != null && !searchRequest.getPayrollId().trim().isEmpty()) {
                predicates.add(cb.or(cityMatch, cb.isNull(e.get("campus_id"))));
            } else {
                predicates.add(cityMatch);
            }
        }

        // Optional: employeeTypeId
        if (searchRequest.getEmployeeTypeId() != null) {
            predicates.add(cb.equal(et_join.get("emp_type_id"), searchRequest.getEmployeeTypeId()));
        }

        // Optional: campusId
        if (searchRequest.getCampusId() != null) {
            Predicate campusMatch = cb.equal(c.get("campusId"), searchRequest.getCampusId());
            if (searchRequest.getPayrollId() != null && !searchRequest.getPayrollId().trim().isEmpty()) {
                predicates.add(cb.or(campusMatch, cb.isNull(e.get("campus_id"))));
            } else {
                predicates.add(campusMatch);
            }
        }

        if (searchRequest.getCmpsCategory() != null && !searchRequest.getCmpsCategory().trim().isEmpty()) {
            Predicate categoryMatch = cb.equal(cb.lower(bt_proj.get("businessTypeName")),
                    searchRequest.getCmpsCategory().trim().toLowerCase());
            if (searchRequest.getPayrollId() != null && !searchRequest.getPayrollId().trim().isEmpty()) {
                predicates.add(cb.or(categoryMatch, cb.isNull(e.get("campus_id"))));
            } else {
                predicates.add(categoryMatch);
            }
        }

        query.where(predicates.toArray(new Predicate[0]));

        // Execute query with pagination
        TypedQuery<EmployeeSearchResponseDTO> typedQuery = entityManager.createQuery(query);
        typedQuery.setFirstResult((int) pageable.getOffset());
        typedQuery.setMaxResults(pageable.getPageSize());

        List<EmployeeSearchResponseDTO> results = typedQuery.getResultList();

        // Get total count for pagination
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<com.employee.entity.Employee> countRoot = countQuery.from(com.employee.entity.Employee.class);
        Join<?, ?> countC = countRoot.join("campus_id", JoinType.LEFT);
        Join<?, ?> countCity = countC.join("city", JoinType.LEFT);
        Join<?, ?> countEt = countRoot.join("employee_type_id", JoinType.LEFT);

        List<Predicate> countPredicates = new ArrayList<>();
        countPredicates.add(cb.equal(countRoot.get("is_active"), 1));
        countPredicates.add(cb.isNotNull(countRoot.get("payRollId"))); // Only show employees with generated payroll ID

        // Combined Search: payrollId OR First Name OR Last Name
        if (searchRequest.getPayrollId() != null && !searchRequest.getPayrollId().trim().isEmpty()) {
            String searchValue = searchRequest.getPayrollId().trim();
            String namePattern = "%" + searchValue.toLowerCase() + "%";

            Predicate payrollPredicate = cb.equal(countRoot.get("payRollId"), searchValue);
            Predicate firstNamePredicate = cb.like(cb.lower(countRoot.get("first_name")), namePattern);
            Predicate lastNamePredicate = cb.like(cb.lower(countRoot.get("last_name")), namePattern);

            // Full Name match: check if pattern matches concatenated first + last name
            Predicate fullNamePredicate = cb.like(
                    cb.lower(cb.concat(
                            cb.concat(cb.coalesce(countRoot.get("first_name"), cb.literal("")), cb.literal(" ")),
                            cb.coalesce(countRoot.get("last_name"), cb.literal("")))),
                    namePattern);

            countPredicates.add(cb.or(payrollPredicate, firstNamePredicate, lastNamePredicate, fullNamePredicate));
        }

        if (searchRequest.getCityId() != null) {
            Predicate cityMatch = cb.equal(countCity.get("cityId"), searchRequest.getCityId());
            if (searchRequest.getPayrollId() != null && !searchRequest.getPayrollId().trim().isEmpty()) {
                countPredicates.add(cb.or(cityMatch, cb.isNull(countRoot.get("campus_id"))));
            } else {
                countPredicates.add(cityMatch);
            }
        }

        if (searchRequest.getEmployeeTypeId() != null) {
            countPredicates.add(cb.equal(countEt.get("emp_type_id"), searchRequest.getEmployeeTypeId()));
        }

        if (searchRequest.getCampusId() != null) {
            Predicate campusMatch = cb.equal(countC.get("campusId"), searchRequest.getCampusId());
            if (searchRequest.getPayrollId() != null && !searchRequest.getPayrollId().trim().isEmpty()) {
                countPredicates.add(cb.or(campusMatch, cb.isNull(countRoot.get("campus_id"))));
            } else {
                countPredicates.add(campusMatch);
            }
        }

        if (searchRequest.getCmpsCategory() != null && !searchRequest.getCmpsCategory().trim().isEmpty()) {
            Join<?, ?> countBt = countC.join("businessType", JoinType.LEFT);
            countPredicates.add(
                    cb.equal(cb.lower(countBt.get("businessTypeName")),
                            searchRequest.getCmpsCategory().trim().toLowerCase()));
        }

        countQuery.select(cb.count(countRoot));
        countQuery.where(countPredicates.toArray(new Predicate[0]));
        Long total = entityManager.createQuery(countQuery).getSingleResult();

        return new org.springframework.data.domain.PageImpl<>(results, pageable, total);
    }

    @Override
    public Page<EmployeeSearchResponseDTO> searchEmployeesAdvancedListDynamic(
            com.employee.dto.AdvancedEmployeeListSearchRequestDTO searchRequest, Pageable pageable) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        CriteriaQuery<EmployeeSearchResponseDTO> query = cb.createQuery(EmployeeSearchResponseDTO.class);
        Root<com.employee.entity.Employee> e = query.from(com.employee.entity.Employee.class);

        // Necessary joins for filtering and projection
        Join<?, ?> d = e.join("department", JoinType.LEFT);
        Join<?, ?> c = e.join("campus_id", JoinType.LEFT);
        Join<?, ?> city = c.join("city", JoinType.LEFT);
        Join<?, ?> state = c.join("state", JoinType.LEFT);
        Join<?, ?> et = e.join("employee_type_id", JoinType.LEFT);
        Join<?, ?> moh = e.join("modeOfHiring_id", JoinType.LEFT);
        Join<?, ?> bt_proj = c.join("businessType", JoinType.LEFT);

        // Build SELECT clause (Projection - 14 fields)
        query.select(cb.construct(
                EmployeeSearchResponseDTO.class,
                e.get("emp_id"),
                cb.concat(
                        cb.concat(
                                cb.coalesce(e.get("first_name"), cb.literal("")),
                                cb.literal(" ")),
                        cb.coalesce(e.get("last_name"), cb.literal(""))),
                e.get("payRollId"),
                cb.coalesce(d.get("department_name"), cb.literal("N/A")),
                cb.coalesce(moh.get("mode_of_hiring_name"), cb.literal("N/A")),
                e.get("tempPayrollId"),
                state.get("stateId"),
                cb.coalesce(state.get("stateName"), cb.literal("N/A")),
                city.get("cityId"),
                cb.coalesce(city.get("cityName"), cb.literal("N/A")),
                c.get("campusId"),
                cb.coalesce(c.get("campusName"), cb.literal("N/A")),
                cb.coalesce(bt_proj.get("businessTypeName"), cb.literal("N/A")),
                et.get("emp_type_id"),
                cb.coalesce(et.get("emp_type"), cb.literal("N/A"))));

        // Build WHERE clause
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(e.get("is_active"), 1)); // Only active employees
        predicates.add(cb.isNotNull(e.get("payRollId"))); // Only show employees with generated payroll ID
        if (searchRequest.getPayrollId() != null && !searchRequest.getPayrollId().trim().isEmpty()) {
            String searchValue = searchRequest.getPayrollId().trim();
            String[] ids = searchValue.split(",");
            if (ids.length > 1) {
                predicates.add(e.get("payRollId").in((Object[]) ids));
            } else {
                String singleValue = ids[0].trim();
                String namePattern = "%" + singleValue.toLowerCase() + "%";

                Predicate payrollPredicate = cb.equal(e.get("payRollId"), singleValue);
                Predicate firstNamePredicate = cb.like(cb.lower(e.get("first_name")), namePattern);
                Predicate lastNamePredicate = cb.like(cb.lower(e.get("last_name")), namePattern);

                // Full Name match: check if pattern matches concatenated first + last name
                Predicate fullNamePredicate = cb.like(
                        cb.lower(cb.concat(
                                cb.concat(cb.coalesce(e.get("first_name"), cb.literal("")), cb.literal(" ")),
                                cb.coalesce(e.get("last_name"), cb.literal("")))),
                        namePattern);

                predicates.add(cb.or(payrollPredicate, firstNamePredicate, lastNamePredicate, fullNamePredicate));
            }
        }

        if (searchRequest.getStateId() != null) {
            predicates.add(cb.equal(state.get("stateId"), searchRequest.getStateId()));
        }

        if (searchRequest.getCityId() != null) {
            predicates.add(cb.equal(city.get("cityId"), searchRequest.getCityId()));
        }

        // Campus Search - Modified to handle Multiple Campus IDs (comma-separated)
        if (searchRequest.getCampusId() != null && !searchRequest.getCampusId().trim().isEmpty()) {
            String searchValue = searchRequest.getCampusId().trim();
            if (searchValue.contains(",")) {
                // Multiple Value search (IDs or Names)
                String[] splitValues = searchValue.split(",");
                List<Integer> campusIds = new ArrayList<>();
                List<String> campusNames = new ArrayList<>();

                for (String val : splitValues) {
                    val = val.trim();
                    if (val.isEmpty())
                        continue;
                    try {
                        campusIds.add(Integer.parseInt(val));
                    } catch (NumberFormatException nfe2) {
                        campusNames.add(val.toLowerCase());
                    }
                }

                List<Predicate> campusPredicates = new ArrayList<>();
                if (!campusIds.isEmpty()) {
                    campusPredicates.add(c.get("campusId").in(campusIds));
                }
                for (String name : campusNames) {
                    campusPredicates.add(cb.like(cb.lower(c.get("campusName")), "%" + name + "%"));
                }

                if (!campusPredicates.isEmpty()) {
                    predicates.add(cb.or(campusPredicates.toArray(new Predicate[0])));
                }
            } else {
                // Single Value logic (ID or Name)
                try {
                    int idVal = Integer.parseInt(searchValue);
                    Predicate idPredicate = cb.equal(c.get("campusId"), idVal);
                    Predicate namePredicate = cb.like(cb.lower(c.get("campusName")),
                            "%" + searchValue.toLowerCase() + "%");
                    predicates.add(cb.or(idPredicate, namePredicate));
                } catch (NumberFormatException nfe) {
                    // Not a number, perform name search only
                    predicates.add(cb.like(cb.lower(c.get("campusName")), "%" + searchValue.toLowerCase() + "%"));
                }
            }
        }

        if (searchRequest.getDepartmentId() != null) {
            predicates.add(cb.equal(d.get("department_id"), searchRequest.getDepartmentId()));
        }

        if (searchRequest.getEmployeeTypeId() != null) {
            predicates.add(cb.equal(et.get("emp_type_id"), searchRequest.getEmployeeTypeId()));
        }

        if (searchRequest.getCmpsCategory() != null && !searchRequest.getCmpsCategory().trim().isEmpty()) {
            predicates.add(cb.equal(cb.lower(bt_proj.get("businessTypeName")),
                    searchRequest.getCmpsCategory().trim().toLowerCase()));
        }

        query.where(predicates.toArray(new Predicate[0]));

        // Pagination logic
        TypedQuery<EmployeeSearchResponseDTO> typedQuery = entityManager.createQuery(query);
        typedQuery.setFirstResult((int) pageable.getOffset());
        typedQuery.setMaxResults(pageable.getPageSize());

        List<EmployeeSearchResponseDTO> results = typedQuery.getResultList();

        // Count Query
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<com.employee.entity.Employee> countRoot = countQuery.from(com.employee.entity.Employee.class);
        Join<?, ?> cc = countRoot.join("campus_id", JoinType.LEFT);
        Join<?, ?> cct = cc.join("city", JoinType.LEFT);
        Join<?, ?> cst = cc.join("state", JoinType.LEFT);
        Join<?, ?> cet = countRoot.join("employee_type_id", JoinType.LEFT);
        Join<?, ?> cd = countRoot.join("department", JoinType.LEFT);

        List<Predicate> countPredicates = new ArrayList<>();
        countPredicates.add(cb.equal(countRoot.get("is_active"), 1));
        countPredicates.add(cb.isNotNull(countRoot.get("payRollId"))); // Only show employees with generated payroll ID

        // Combined Search: Multiple Payroll IDs OR (Single ID OR Name)
        if (searchRequest.getPayrollId() != null && !searchRequest.getPayrollId().trim().isEmpty()) {
            String searchValue = searchRequest.getPayrollId().trim();
            String[] ids = searchValue.split(",");
            if (ids.length > 1) {
                countPredicates.add(countRoot.get("payRollId").in((Object[]) ids));
            } else {
                String singleValue = ids[0].trim();
                String namePattern = "%" + singleValue.toLowerCase() + "%";
                Predicate payrollPredicate = cb.equal(countRoot.get("payRollId"), singleValue);
                Predicate firstNamePredicate = cb.like(cb.lower(countRoot.get("first_name")), namePattern);
                Predicate lastNamePredicate = cb.like(cb.lower(countRoot.get("last_name")), namePattern);
                Predicate fullNamePredicate = cb.like(
                        cb.lower(cb.concat(
                                cb.concat(cb.coalesce(countRoot.get("first_name"), cb.literal("")), cb.literal(" ")),
                                cb.coalesce(countRoot.get("last_name"), cb.literal("")))),
                        namePattern);
                countPredicates.add(cb.or(payrollPredicate, firstNamePredicate, lastNamePredicate, fullNamePredicate));
            }
        }
        if (searchRequest.getStateId() != null)
            countPredicates.add(cb.equal(cst.get("stateId"), searchRequest.getStateId()));
        if (searchRequest.getCityId() != null)
            countPredicates.add(cb.equal(cct.get("cityId"), searchRequest.getCityId()));

        // Count Query Campus Logic
        if (searchRequest.getCampusId() != null && !searchRequest.getCampusId().trim().isEmpty()) {
            String searchValue = searchRequest.getCampusId().trim();
            if (searchValue.contains(",")) {
                // Multiple Value search (IDs or Names)
                String[] splitValues = searchValue.split(",");
                List<Integer> campusIds = new ArrayList<>();
                List<String> campusNames = new ArrayList<>();

                for (String val : splitValues) {
                    val = val.trim();
                    if (val.isEmpty())
                        continue;
                    try {
                        campusIds.add(Integer.parseInt(val));
                    } catch (NumberFormatException nfe3) {
                        campusNames.add(val.toLowerCase());
                    }
                }

                List<Predicate> campusPredicates = new ArrayList<>();
                if (!campusIds.isEmpty()) {
                    campusPredicates.add(cc.get("campusId").in(campusIds));
                }
                for (String name : campusNames) {
                    campusPredicates.add(cb.like(cb.lower(cc.get("campusName")), "%" + name + "%"));
                }

                if (!campusPredicates.isEmpty()) {
                    countPredicates.add(cb.or(campusPredicates.toArray(new Predicate[0])));
                }
            } else {
                try {
                    int idVal = Integer.parseInt(searchValue);
                    Predicate idPredicate = cb.equal(cc.get("campusId"), idVal);
                    Predicate namePredicate = cb.like(cb.lower(cc.get("campusName")),
                            "%" + searchValue.toLowerCase() + "%");
                    countPredicates.add(cb.or(idPredicate, namePredicate));
                } catch (NumberFormatException nfe) {
                    countPredicates.add(cb.like(cb.lower(cc.get("campusName")), "%" + searchValue.toLowerCase() + "%"));
                }
            }
        }

        if (searchRequest.getEmployeeTypeId() != null)
            countPredicates.add(cb.equal(cet.get("emp_type_id"), searchRequest.getEmployeeTypeId()));
        if (searchRequest.getDepartmentId() != null)
            countPredicates.add(cb.equal(cd.get("department_id"), searchRequest.getDepartmentId()));

        if (searchRequest.getCmpsCategory() != null && !searchRequest.getCmpsCategory().trim().isEmpty()) {
            Join<?, ?> countBt = cc.join("businessType", JoinType.LEFT);
            Predicate categoryMatch = cb.equal(cb.lower(countBt.get("businessTypeName")),
                    searchRequest.getCmpsCategory().trim().toLowerCase());
            if (searchRequest.getPayrollId() != null && !searchRequest.getPayrollId().trim().isEmpty()) {
                countPredicates.add(cb.or(categoryMatch, cb.isNull(countRoot.get("campus_id"))));
            } else {
                countPredicates.add(categoryMatch);
            }
        }

        countQuery.select(cb.count(countRoot));
        countQuery.where(countPredicates.toArray(new Predicate[0]));
        Long total = entityManager.createQuery(countQuery).getSingleResult();

        return new org.springframework.data.domain.PageImpl<>(results, pageable, total);
    }

    @Override
    public Page<EmployeeSearchResponseDTO> searchEmployeesAdvancedDynamic(
            AdvancedEmployeeSearchRequestDTO searchRequest, Pageable pageable) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        // Create criteria query for DTO projection
        CriteriaQuery<EmployeeSearchResponseDTO> query = cb.createQuery(EmployeeSearchResponseDTO.class);
        Root<com.employee.entity.Employee> e = query.from(com.employee.entity.Employee.class);

        // Required joins
        Join<?, ?> d = e.join("department", JoinType.LEFT);
        Join<?, ?> c = e.join("campus_id", JoinType.LEFT);
        Join<?, ?> moh = e.join("modeOfHiring_id", JoinType.LEFT);

        // Joins needed for projection
        Join<?, ?> city_proj = c.join("city", JoinType.LEFT);
        Join<?, ?> s_proj = c.join("state", JoinType.LEFT);
        Join<?, ?> et_proj = e.join("employee_type_id", JoinType.LEFT);
        Join<?, ?> bt_proj = c.join("businessType", JoinType.LEFT);

        // Build SELECT clause (DTO projection - 14 fields)
        query.select(cb.construct(
                EmployeeSearchResponseDTO.class,
                e.get("emp_id"),
                cb.concat(
                        cb.concat(
                                cb.coalesce(e.get("first_name"), cb.literal("")),
                                cb.literal(" ")),
                        cb.coalesce(e.get("last_name"), cb.literal(""))),
                e.get("payRollId"),
                cb.coalesce(d.get("department_name"), cb.literal("N/A")),
                cb.coalesce(moh.get("mode_of_hiring_name"), cb.literal("N/A")),
                e.get("tempPayrollId"),
                s_proj.get("stateId"),
                cb.coalesce(s_proj.get("stateName"), cb.literal("N/A")),
                city_proj.get("cityId"),
                cb.coalesce(city_proj.get("cityName"), cb.literal("N/A")),
                c.get("campusId"),
                cb.coalesce(c.get("campusName"), cb.literal("N/A")),
                cb.coalesce(bt_proj.get("businessTypeName"), cb.literal("N/A")),
                et_proj.get("emp_type_id"),
                cb.coalesce(et_proj.get("emp_type"), cb.literal("N/A"))));

        // Build WHERE clause dynamically
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(e.get("is_active"), 1)); // Always required: is_active = 1
        predicates.add(cb.isNotNull(e.get("payRollId"))); // Only show employees with generated payroll ID

        // Optional: payrollId (can be comma-separated)
        if (searchRequest.getPayrollId() != null && !searchRequest.getPayrollId().trim().isEmpty()) {
            String searchValue = searchRequest.getPayrollId().trim();
            String[] ids = searchValue.split(",");
            if (ids.length > 1) {
                predicates.add(e.get("payRollId").in((Object[]) ids));
            } else {
                String singleValue = ids[0].trim();
                String namePattern = "%" + singleValue.toLowerCase() + "%";

                Predicate payrollPredicate = cb.equal(e.get("payRollId"), singleValue);
                Predicate firstNamePredicate = cb.like(cb.lower(e.get("first_name")), namePattern);
                Predicate lastNamePredicate = cb.like(cb.lower(e.get("last_name")), namePattern);

                // Full Name match: check if pattern matches concatenated first + last name
                Predicate fullNamePredicate = cb.like(
                        cb.lower(cb.concat(
                                cb.concat(cb.coalesce(e.get("first_name"), cb.literal("")), cb.literal(" ")),
                                cb.coalesce(e.get("last_name"), cb.literal("")))),
                        namePattern);

                predicates.add(cb.or(payrollPredicate, firstNamePredicate, lastNamePredicate, fullNamePredicate));
            }
        }

        // Optional: stateId
        if (searchRequest.getStateId() != null) {
            predicates.add(cb.equal(s_proj.get("stateId"), searchRequest.getStateId()));
        }

        // Optional: cityId
        if (searchRequest.getCityId() != null) {
            predicates.add(cb.equal(city_proj.get("cityId"), searchRequest.getCityId()));
        }

        // Optional: campusId
        if (searchRequest.getCampusId() != null) {
            predicates.add(cb.equal(c.get("campusId"), searchRequest.getCampusId()));
        }

        // Optional: departmentId
        if (searchRequest.getDepartmentId() != null) {
            predicates.add(cb.equal(d.get("department_id"), searchRequest.getDepartmentId()));
        }

        // Optional: employeeTypeId
        if (searchRequest.getEmployeeTypeId() != null) {
            predicates.add(cb.equal(et_proj.get("emp_type_id"), searchRequest.getEmployeeTypeId()));
        }

        if (searchRequest.getCmpsCategory() != null && !searchRequest.getCmpsCategory().trim().isEmpty()) {
            predicates.add(cb.equal(cb.lower(bt_proj.get("businessTypeName")),
                    searchRequest.getCmpsCategory().trim().toLowerCase()));
        }

        query.where(predicates.toArray(new Predicate[0]));

        // Execute query with pagination
        TypedQuery<EmployeeSearchResponseDTO> typedQuery = entityManager.createQuery(query);
        typedQuery.setFirstResult((int) pageable.getOffset());
        typedQuery.setMaxResults(pageable.getPageSize());

        List<EmployeeSearchResponseDTO> results = typedQuery.getResultList();

        // Get total count for pagination
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<com.employee.entity.Employee> countRoot = countQuery.from(com.employee.entity.Employee.class);
        Join<?, ?> countD = countRoot.join("department", JoinType.LEFT);
        Join<?, ?> countC = countRoot.join("campus_id", JoinType.LEFT);

        List<Predicate> countPredicates = new ArrayList<>();
        countPredicates.add(cb.equal(countRoot.get("is_active"), 1));
        countPredicates.add(cb.isNotNull(countRoot.get("payRollId"))); // Only show employees with generated payroll ID

        if (searchRequest.getPayrollId() != null && !searchRequest.getPayrollId().trim().isEmpty()) {
            String searchValue = searchRequest.getPayrollId().trim();
            String[] ids = searchValue.split(",");
            if (ids.length > 1) {
                countPredicates.add(countRoot.get("payRollId").in((Object[]) ids));
            } else {
                String singleValue = ids[0].trim();
                String namePattern = "%" + singleValue.toLowerCase() + "%";

                Predicate payrollPredicate = cb.equal(countRoot.get("payRollId"), singleValue);
                Predicate firstNamePredicate = cb.like(cb.lower(countRoot.get("first_name")), namePattern);
                Predicate lastNamePredicate = cb.like(cb.lower(countRoot.get("last_name")), namePattern);

                // Full Name match: check if pattern matches concatenated first + last name
                Predicate fullNamePredicate = cb.like(
                        cb.lower(cb.concat(
                                cb.concat(cb.coalesce(countRoot.get("first_name"), cb.literal("")), cb.literal(" ")),
                                cb.coalesce(countRoot.get("last_name"), cb.literal("")))),
                        namePattern);

                countPredicates.add(cb.or(payrollPredicate, firstNamePredicate, lastNamePredicate, fullNamePredicate));
            }
        }

        if (searchRequest.getStateId() != null) {
            Join<?, ?> countS = countC.join("state", JoinType.LEFT);
            countPredicates.add(cb.equal(countS.get("stateId"), searchRequest.getStateId()));
        }

        if (searchRequest.getCityId() != null) {
            Join<?, ?> countCity = countC.join("city", JoinType.LEFT);
            countPredicates.add(cb.equal(countCity.get("cityId"), searchRequest.getCityId()));
        }

        if (searchRequest.getCampusId() != null) {
            countPredicates.add(cb.equal(countC.get("campusId"), searchRequest.getCampusId()));
        }

        if (searchRequest.getDepartmentId() != null) {
            countPredicates.add(cb.equal(countD.get("department_id"), searchRequest.getDepartmentId()));
        }

        if (searchRequest.getEmployeeTypeId() != null) {
            Join<?, ?> countEt = countRoot.join("employee_type_id", JoinType.LEFT);
            countPredicates.add(cb.equal(countEt.get("emp_type_id"), searchRequest.getEmployeeTypeId()));
        }

        if (searchRequest.getCmpsCategory() != null && !searchRequest.getCmpsCategory().trim().isEmpty()) {
            Join<?, ?> countBt = countC.join("businessType", JoinType.LEFT);
            countPredicates.add(
                    cb.equal(cb.lower(countBt.get("businessTypeName")),
                            searchRequest.getCmpsCategory().trim().toLowerCase()));
        }

        countQuery.select(cb.count(countRoot));
        countQuery.where(countPredicates.toArray(new Predicate[0]));
        Long total = entityManager.createQuery(countQuery).getSingleResult();

        return new org.springframework.data.domain.PageImpl<>(results, pageable, total);
    }

    @Override
    public Page<EmployeeSearchResponseDTO> searchEmployeesListDynamic(EmployeeSearchRequestDTO searchRequest,
            Pageable pageable) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        CriteriaQuery<EmployeeSearchResponseDTO> query = cb.createQuery(EmployeeSearchResponseDTO.class);
        Root<com.employee.entity.Employee> e = query.from(com.employee.entity.Employee.class);

        // Joins
        Join<?, ?> d = e.join("department", JoinType.LEFT);
        Join<?, ?> c = e.join("campus_id", JoinType.LEFT);
        Join<?, ?> city = c.join("city", JoinType.LEFT);
        Join<?, ?> s = c.join("state", JoinType.LEFT);
        Join<?, ?> moh = e.join("modeOfHiring_id", JoinType.LEFT);
        Join<?, ?> et_join = e.join("employee_type_id", JoinType.LEFT);
        Join<?, ?> bt_proj = c.join("businessType", JoinType.LEFT);

        // SELECT clause
        query.select(cb.construct(
                EmployeeSearchResponseDTO.class,
                e.get("emp_id"),
                cb.concat(
                        cb.concat(
                                cb.coalesce(e.get("first_name"), cb.literal("")),
                                cb.literal(" ")),
                        cb.coalesce(e.get("last_name"), cb.literal(""))),
                e.get("payRollId"),
                cb.coalesce(d.get("department_name"), cb.literal("N/A")),
                cb.coalesce(moh.get("mode_of_hiring_name"), cb.literal("N/A")),
                e.get("tempPayrollId"),
                s.get("stateId"),
                cb.coalesce(s.get("stateName"), cb.literal("N/A")),
                city.get("cityId"),
                cb.coalesce(city.get("cityName"), cb.literal("N/A")),
                c.get("campusId"),
                cb.coalesce(c.get("campusName"), cb.literal("N/A")),
                cb.coalesce(bt_proj.get("businessTypeName"), cb.literal("N/A")),
                et_join.get("emp_type_id"),
                cb.coalesce(et_join.get("emp_type"), cb.literal("N/A"))));

        // WHERE clause
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(e.get("is_active"), 1));
        predicates.add(cb.isNotNull(e.get("payRollId"))); // Only show employees with generated payroll ID

        // Combined Search: Multiple Payroll IDs OR (Single ID OR Name)
        if (searchRequest.getPayrollId() != null && !searchRequest.getPayrollId().trim().isEmpty()) {
            String searchValue = searchRequest.getPayrollId().trim();
            String[] ids = searchValue.split(",");

            if (ids.length > 1) {
                // Multiple IDs provided - search only by IDs
                predicates.add(e.get("payRollId").in((Object[]) ids));
            } else {
                // Single value provided - search by ID OR Name
                String singleValue = ids[0].trim();
                String namePattern = "%" + singleValue.toLowerCase() + "%";

                Predicate payrollPredicate = cb.equal(e.get("payRollId"), singleValue);
                Predicate firstNamePredicate = cb.like(cb.lower(e.get("first_name")), namePattern);
                Predicate lastNamePredicate = cb.like(cb.lower(e.get("last_name")), namePattern);

                // Full Name match: check if pattern matches concatenated first + last name
                Predicate fullNamePredicate = cb.like(
                        cb.lower(cb.concat(
                                cb.concat(cb.coalesce(e.get("first_name"), cb.literal("")), cb.literal(" ")),
                                cb.coalesce(e.get("last_name"), cb.literal("")))),
                        namePattern);

                predicates.add(cb.or(payrollPredicate, firstNamePredicate, lastNamePredicate, fullNamePredicate));
            }
        }

        if (searchRequest.getCityId() != null) {
            predicates.add(cb.equal(city.get("cityId"), searchRequest.getCityId()));
        }

        if (searchRequest.getEmployeeTypeId() != null) {
            predicates.add(cb.equal(et_join.get("emp_type_id"), searchRequest.getEmployeeTypeId()));
        }

        // Optional: campusId
        if (searchRequest.getCampusId() != null) {
            predicates.add(cb.equal(c.get("campusId"), searchRequest.getCampusId()));
        }

        if (searchRequest.getCmpsCategory() != null && !searchRequest.getCmpsCategory().trim().isEmpty()) {
            predicates.add(cb.equal(cb.lower(bt_proj.get("businessTypeName")),
                    searchRequest.getCmpsCategory().trim().toLowerCase()));
        }

        query.where(predicates.toArray(new Predicate[0]));

        // Execute query
        TypedQuery<EmployeeSearchResponseDTO> typedQuery = entityManager.createQuery(query);
        typedQuery.setFirstResult((int) pageable.getOffset());
        typedQuery.setMaxResults(pageable.getPageSize());

        List<EmployeeSearchResponseDTO> results = typedQuery.getResultList();

        // Total count
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<com.employee.entity.Employee> countRoot = countQuery.from(com.employee.entity.Employee.class);
        Join<?, ?> countC = countRoot.join("campus_id", JoinType.LEFT);
        Join<?, ?> countCity = countC.join("city", JoinType.LEFT);
        Join<?, ?> countEt = countRoot.join("employee_type_id", JoinType.LEFT);

        List<Predicate> countPredicates = new ArrayList<>();
        countPredicates.add(cb.equal(countRoot.get("is_active"), 1));
        countPredicates.add(cb.isNotNull(countRoot.get("payRollId"))); // Only show employees with generated payroll ID

        // Combined Search: Multiple Payroll IDs OR (Single ID OR Name)
        if (searchRequest.getPayrollId() != null && !searchRequest.getPayrollId().trim().isEmpty()) {
            String searchValue = searchRequest.getPayrollId().trim();
            String[] ids = searchValue.split(",");

            if (ids.length > 1) {
                // Multiple IDs - count matching records
                countPredicates.add(countRoot.get("payRollId").in((Object[]) ids));
            } else {
                // Single value - ID OR Name
                String singleValue = ids[0].trim();
                String namePattern = "%" + singleValue.toLowerCase() + "%";

                Predicate payrollPredicate = cb.equal(countRoot.get("payRollId"), singleValue);
                Predicate firstNamePredicate = cb.like(cb.lower(countRoot.get("first_name")), namePattern);
                Predicate lastNamePredicate = cb.like(cb.lower(countRoot.get("last_name")), namePattern);

                // Full Name match: check if pattern matches concatenated first + last name
                Predicate fullNamePredicate = cb.like(
                        cb.lower(cb.concat(
                                cb.concat(cb.coalesce(countRoot.get("first_name"), cb.literal("")), cb.literal(" ")),
                                cb.coalesce(countRoot.get("last_name"), cb.literal("")))),
                        namePattern);

                countPredicates.add(cb.or(payrollPredicate, firstNamePredicate, lastNamePredicate, fullNamePredicate));
            }
        }

        if (searchRequest.getCityId() != null) {
            countPredicates.add(cb.equal(countCity.get("cityId"), searchRequest.getCityId()));
        }

        if (searchRequest.getEmployeeTypeId() != null) {
            countPredicates.add(cb.equal(countEt.get("emp_type_id"), searchRequest.getEmployeeTypeId()));
        }

        if (searchRequest.getCampusId() != null) {
            countPredicates.add(cb.equal(countC.get("campusId"), searchRequest.getCampusId()));
        }

        if (searchRequest.getCmpsCategory() != null && !searchRequest.getCmpsCategory().trim().isEmpty()) {
            Join<?, ?> countBt = countC.join("businessType", JoinType.LEFT);
            countPredicates.add(
                    cb.equal(cb.lower(countBt.get("businessTypeName")),
                            searchRequest.getCmpsCategory().trim().toLowerCase()));
        }

        countQuery.select(cb.count(countRoot));
        countQuery.where(countPredicates.toArray(new Predicate[0]));
        Long total = entityManager.createQuery(countQuery).getSingleResult();

        return new org.springframework.data.domain.PageImpl<>(results, pageable, total);
    }
}
