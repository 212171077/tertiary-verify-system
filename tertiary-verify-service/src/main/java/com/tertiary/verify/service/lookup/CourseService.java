package com.tertiary.verify.service.lookup;

import static com.tertiary.verify.utils.ConstantUtil.UNABLE_TO_PROCESS_REQUEST;

import com.tertiary.verify.data_model.model.beans.AllCourseDTO;
import com.tertiary.verify.data_model.model.beans.MessageResponse;
import com.tertiary.verify.data_model.model.beans.lookup.CourseRequest;
import com.tertiary.verify.data_model.model.entities.lookup.Course;
import com.tertiary.verify.data_model.model.entities.lookup.CourseLevel;
import com.tertiary.verify.data_model.model.entities.lookup.CourseType;
import com.tertiary.verify.data_model.model.repositories.lookup.CourseRepository;
import com.tertiary.verify.framework.AbstractService;
import com.tertiary.verify.service.InstitutionCourseService;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class CourseService extends AbstractService {
  @Autowired private CourseRepository repository;

  @Autowired private CourseLevelService courseLevelService;

  @Autowired private CourseTypeService courseTypeService;

  @Autowired private InstitutionCourseService institutionCourseService;

  @Autowired private FacultyCourseService facultyCourseService;

  @CacheEvict(value = "courses", allEntries = true)
  public void saveCourse(Course course) throws Exception {
    if (course.getId() == null) {
      checkIfExist(course);
      course.setDeleted(false);
      course.setCreateDate(new Date());
    } else {
      course.setLastUpdateDate(new Date());
    }
    repository.save(course);
  }

  @CacheEvict(value = "courses", allEntries = true)
  public void deleteCourse(Course course) throws Exception {
    checkIfInUse(course);
    course.setDeleted(true);
    repository.save(course);
  }

  @Cacheable("courses")
  public List<Course> findAllCourse() {
    long startTime = System.currentTimeMillis();
    try {
      return repository.findByDeleted(false);
    } finally {
      long endTime = System.currentTimeMillis();
      long duration = endTime - startTime;
      logger.info("findAllCourse took " + duration + " milliseconds to execute.");
    }
  }

  public List<AllCourseDTO> findAllCourseDTO() {
    return repository.findAllCourseDTO();
  }

  @Cacheable("courses")
  public Course findById(Long parseLong) {
    return repository.findByIdAndDeleted(parseLong, false);
  }

  public List<Course> findByDescriptionStartingWith(String description) {
    List<Course> list = findAllCourse();
    if (list != null) {
      return list.stream()
          .filter(course -> course.getDescription().startsWith(description))
          .collect(Collectors.toList());
    } else {
      return new ArrayList<>();
    }
  }

  public List<Course> searchByNameTypeAndLevel(
      String description, CourseType courseType, CourseLevel courseLevel) {
    List<Course> list = findAllCourse();
    if (list != null) {
      return list.stream()
          .filter(
              course ->
                  course.getDescription().contains(description)
                      && course.getCourseType().getId().equals(courseType.getId())
                      && course.getCourseLevel().getId().equals(courseLevel.getId()))
          .collect(Collectors.toList());
    } else {
      return new ArrayList<>();
    }
  }

  public List<Course> findByDescriptionAndCourseTypeAndCourseLevel(
      String description, String courseTypeDesc, String courseLevelDesc) {
    List<Course> list = findAllCourse();
    if (!list.isEmpty()) {
      return list.stream()
          .filter(
              course ->
                  course.getDescription().equalsIgnoreCase(description)
                      && course.getCourseType().getDescription().equalsIgnoreCase(courseTypeDesc)
                      && course.getCourseLevel().getDescription().equalsIgnoreCase(courseLevelDesc))
          .collect(Collectors.toList());
    } else {
      return new ArrayList<>();
    }
  }

  public long countByCourseLevel(CourseLevel courseLevel) {
    return repository.countByCourseLevelAndDeleted(courseLevel, false);
  }

  public long countByCourseType(CourseType courseType) {
    return repository.countByCourseTypeAndDeleted(courseType, false);
  }

  private void checkIfExist(Course course) throws Exception {
    List<Course> list =
        findByDescriptionAndCourseTypeAndCourseLevel(
            course.getDescription(),
            course.getCourseType().getDescription(),
            course.getCourseLevel().getDescription());
    if (list != null && list.size() > 0) {
      throw new Exception("Course already exist");
    }
  }

  private void checkIfInUse(Course course) throws Exception {
    long count = institutionCourseService.countByCourse(course);
    if (count > 0) {
      throw new Exception("This course cannot be deleted because it's being used");
    } else {
      count = facultyCourseService.countByCourse(course);
      if (count > 0) {
        throw new Exception("This course cannot be deleted because it's being used");
      }
    }
  }

  public ResponseEntity<? extends Serializable> saveCourse(CourseRequest request) {
    Course course = findById(request.getId());
    if (course == null) {
      course = new Course();
    }
    try {
      CourseType courseType = courseTypeService.findById(request.getCourseTypeId());
      if (courseType == null) {
        throw new ValidationException("Invalid course type ID");
      }
      CourseLevel courseLevel = courseLevelService.findById(request.getCourseLevelId());
      if (courseLevel == null) {
        throw new ValidationException("Invalid course level ID");
      }
      course.setDescription(request.getDescription());
      course.setCourseLevel(courseLevel);
      course.setCourseType(courseType);
      saveCourse(course);
    } catch (Exception e) {
      return ResponseEntity.badRequest()
          .body(new MessageResponse(false, UNABLE_TO_PROCESS_REQUEST.concat(e.getMessage()), null));
    }
    return ResponseEntity.ok(new MessageResponse(true, "Course saved", null));
  }

  public ResponseEntity<? extends Serializable> deleteCourse(Long id) {
    Course course = findById(id);
    if (course == null) {
      return ResponseEntity.badRequest()
          .body(new MessageResponse(false, "Invalid course ID", null));
    }
    try {
      deleteCourse(course);
    } catch (Exception e) {
      return ResponseEntity.badRequest()
          .body(new MessageResponse(false, UNABLE_TO_PROCESS_REQUEST.concat(e.getMessage()), null));
    }
    return ResponseEntity.ok(new MessageResponse(true, "Course deleted", null));
  }

  public ResponseEntity<List<Course>> findAllCourses() {
    List<Course> list = findAllCourse();
    if (!list.isEmpty()) {
      list.sort(Comparator.comparing(Course::getDescription));
    }
    return ResponseEntity.ok(list);
  }
}
