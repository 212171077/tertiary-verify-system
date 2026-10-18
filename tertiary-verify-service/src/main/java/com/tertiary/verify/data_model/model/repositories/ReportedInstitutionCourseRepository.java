package com.tertiary.verify.data_model.model.repositories;

import com.tertiary.verify.data_model.model.entities.ReportedInstitutionCourse;
import com.tertiary.verify.data_model.model.entities.ReportedInstitutions;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportedInstitutionCourseRepository
    extends JpaRepository<ReportedInstitutionCourse, Integer> {

  List<ReportedInstitutionCourse> findByReportedInstitution(
      ReportedInstitutions reportedInstitutions);
}
