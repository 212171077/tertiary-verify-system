package com.tertiary.verify.service.lookup;

import com.tertiary.verify.data_model.model.entities.lookup.Course;
import com.tertiary.verify.data_model.model.entities.lookup.Faculty;
import com.tertiary.verify.data_model.model.entities.lookup.FacultyCourse;
import com.tertiary.verify.data_model.model.repositories.lookup.FacultyCourseRepository;
import com.tertiary.verify.framework.AbstractService;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class FacultyCourseService extends AbstractService {
  @Autowired FacultyCourseRepository repository;

  @CacheEvict(value = "faculty_courses", allEntries = true)
  public void saveFacultyCourse(FacultyCourse facultyCourse) {
    if (facultyCourse.getId() == null) {
      facultyCourse.setDeleted(false);
      facultyCourse.setCreateDate(new Date());
    } else {
      facultyCourse.setLastUpdateDate(new Date());
    }
    repository.save(facultyCourse);
  }

  @CacheEvict(value = "faculty_courses", allEntries = true)
  public void deleteFacultyCourse(FacultyCourse facultyCourse) {
    facultyCourse.setDeleted(false);
    repository.save(facultyCourse);
  }

  @Cacheable("faculty_courses")
  public FacultyCourse findById(Long parseLong) {
    return repository.findByIdAndDeleted(parseLong, false);
  }

  @Cacheable("faculty_courses")
  public List<FacultyCourse> findByAll() {
    return repository.findByDeleted(false);
  }

  public List<FacultyCourse> findByFaculty(Faculty faculty) {
    List<FacultyCourse> list = findByAll();
    if (!list.isEmpty()) {
      return list.stream()
          .filter(facultyCourse -> facultyCourse.getFaculty().getId().equals(faculty.getId()))
          .collect(Collectors.toList());
    } else {
      return new ArrayList<>();
    }
  }

  @CacheEvict(value = "faculty_courses", allEntries = true)
  public void deleteAll(ArrayList<FacultyCourse> list) {
    for (FacultyCourse facultyCourse : list) {
      facultyCourse.setDeleted(false);
    }
    repository.saveAll(list);
  }

  public long countByCourse(Course course) {
    return repository.countByCourseAndDeleted(course, false);
  }
}
