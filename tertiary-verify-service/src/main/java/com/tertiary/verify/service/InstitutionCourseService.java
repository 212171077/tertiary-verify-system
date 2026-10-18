package com.tertiary.verify.service;

import com.tertiary.verify.data_model.model.beans.*;
import com.tertiary.verify.data_model.model.entities.InstitutionCourse;
import com.tertiary.verify.data_model.model.entities.lookup.Course;
import com.tertiary.verify.data_model.model.repositories.InstitutionCourseRepository;
import com.tertiary.verify.framework.AbstractService;
import java.util.*;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class InstitutionCourseService extends AbstractService {
  private static final Logger LOGGER = LoggerFactory.getLogger(InstitutionCourseService.class);
  @Autowired InstitutionCourseRepository repository;

  @CacheEvict(value = "institution_course", allEntries = true)
  public void saveInstitutionCourse(InstitutionCourse institutionCourse) {
    if (institutionCourse.getId() == null) {
      institutionCourse.setCreateDate(new Date());
    } else {
      institutionCourse.setLastUpdateDate(new Date());
    }
    repository.save(institutionCourse);
  }

  @CacheEvict(value = "institution_course", allEntries = true)
  public void deleteInstitutionCourse(InstitutionCourse institutionCourse) {
    repository.delete(institutionCourse);
  }

  @CacheEvict(value = "institution_course", allEntries = true)
  public ResponseEntity<?> deleteInstitutionCourse(Long id) {
    InstitutionCourse institutionCourse = repository.findById(id);

    if (institutionCourse == null) {
      LOGGER.error("Invalid institution course ID :{} ", id);
      return ResponseEntity.badRequest()
          .body(new MessageResponse(false, "Invalid institution course ID", null));
    }

    repository.delete(institutionCourse);

    return ResponseEntity.ok()
        .body(new MessageResponse(true, "Institution course successfully deleted", null));
  }

  @CacheEvict(value = "institution_course", allEntries = true)
  public void deleteInstitutionCourseByID(Integer arg0) {
    repository.deleteById(arg0);
  }

  @Cacheable("institution_course")
  public List<InstitutionCourse> findAllInstitutionCourse() {
    long startTime = System.currentTimeMillis();
    try {
      List<InstitutionCourse> repositoryAll = repository.findAll();
      if (!repositoryAll.isEmpty()) {
        return repositoryAll.stream()
            .filter(institutionCourse -> !institutionCourse.getInstitution().getDeleted())
            .collect(Collectors.toList());
      } else {
        return new ArrayList<>();
      }
    } catch (Exception e) {
      LOGGER.error("Error in findAllInstitutionCourse: ", e);
      return new ArrayList<>();
    } finally {
      long endTime = System.currentTimeMillis();
      long duration = endTime - startTime;
      LOGGER.info("findAllInstitutionCourse took " + duration + " milliseconds to execute.");
    }
  }

  @CacheEvict(value = "institution_course", allEntries = true)
  public void saveAll(ArrayList<InstitutionCourse> courseList) {
    repository.saveAll(courseList);
  }

  @CacheEvict(value = "institution_course", allEntries = true)
  public void deleteAll(List<InstitutionCourse> institutionCourseList) {
    repository.deleteAll(institutionCourseList);
  }

  public long countAll() {
    return repository.count();
  }

  public long countByCourse(Course course) {
    return repository.countByCourse(course);
  }
}
