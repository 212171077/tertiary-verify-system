package com.tertiary.verify.service;

import com.tertiary.verify.data_model.model.entities.ReportedInstitutionCourse;
import com.tertiary.verify.data_model.model.entities.ReportedInstitutions;
import com.tertiary.verify.data_model.model.repositories.ReportedInstitutionCourseRepository;
import com.tertiary.verify.framework.AbstractService;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class ReportedInstitutionCourseService extends AbstractService {
  @Autowired ReportedInstitutionCourseRepository repository;

  @CacheEvict(value = "reported_institution_courses", allEntries = true)
  public void saveReportedInstitutionCourse(ReportedInstitutionCourse reportedInstitutionCourse) {
    if (reportedInstitutionCourse.getId() == null) {
      reportedInstitutionCourse.setCreateDate(new Date());
    } else {
      reportedInstitutionCourse.setLastUpdateDate(new Date());
    }
    repository.save(reportedInstitutionCourse);
  }

  @CacheEvict(value = "reported_institution_courses", allEntries = true)
  public void deleteReportedInstitutionCourse(ReportedInstitutionCourse reportedInstitutionCourse) {
    repository.delete(reportedInstitutionCourse);
  }

  @CacheEvict(value = "reported_institution_courses", allEntries = true)
  public void deleteReportedInstitutionCourseByID(Integer arg0) {
    repository.deleteById(arg0);
  }

  @Cacheable("reported_institution_courses")
  public List<ReportedInstitutionCourse> findAllReportedInstitutionCourse() {
    return repository.findAll();
  }

  public Optional<ReportedInstitutionCourse> findReportedInstitutionCourseById(Integer arg0) {
    return repository.findById(arg0);
  }

  public List<ReportedInstitutionCourse> findByReportedInstitution(
      ReportedInstitutions reportedInstitutions) {
    return repository.findByReportedInstitution(reportedInstitutions);
  }
}
