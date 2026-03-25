package com.employee.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.employee.entity.Subject;

public interface SubjectRepository extends JpaRepository<Subject, Integer> {

	List<Subject> findByIsActive(Integer isActive);

	List<Subject> findBySubjectCategory_BusinessTypeIdAndEmpSubjectAndIsActive(Integer subjectCategoryId,
			Integer empSubject, Integer isActive);

	List<Subject> findBySubjectCategory_BusinessTypeIdAndIsActive(Integer subjectCategoryId, Integer isActive);
}
