package com.tertiary.verify.service.lookup;

import static com.tertiary.verify.utils.ConstantUtil.UNABLE_TO_PROCESS_REQUEST;

import com.tertiary.verify.data_model.model.beans.MessageResponse;
import com.tertiary.verify.data_model.model.beans.lookup.CourseLevelRequest;
import com.tertiary.verify.data_model.model.entities.lookup.CourseLevel;
import com.tertiary.verify.data_model.model.repositories.lookup.CourseLevelRepository;
import com.tertiary.verify.framework.AbstractService;
import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class CourseLevelService extends AbstractService {
  @Autowired private CourseLevelRepository repository;

  @Autowired private CourseService courseService;

  @CacheEvict(value = "course_levels", allEntries = true)
  public void saveCourseLevel(CourseLevel courseLevel) throws Exception {
    if (courseLevel.getId() == null) {
      checkIfExist(courseLevel.getDescription());
      courseLevel.setDeleted(false);
      courseLevel.setCreateDate(new Date());
    } else {
      courseLevel.setLastUpdateDate(new Date());
    }
    repository.save(courseLevel);
  }

  @CacheEvict(value = "course_levels", allEntries = true)
  public void deleteCourseLevel(CourseLevel courseLevel) throws Exception {
    checkIfInUse(courseLevel);
    courseLevel.setDeleted(true);
    repository.save(courseLevel);
  }

  @Cacheable("course_levels")
  public List<CourseLevel> findAllCourseLevel() {
    return repository.findByDeleted(false);
  }

  @Cacheable("course_levels")
  public CourseLevel findById(Long parseLong) {
    return repository.findByIdAndDeleted(parseLong, false);
  }

  private void checkIfExist(String desc) throws Exception {
    List<CourseLevel> list = findAllCourseLevel();
    if (!list.isEmpty()) {
      CourseLevel existingCourseLevel =
          list.stream()
              .filter(courseLevel -> courseLevel.getDescription().equalsIgnoreCase(desc))
              .findAny()
              .orElse(null);
      if (existingCourseLevel != null) {
        throw new Exception("Course Level already exist");
      }
    }
  }

  public CourseLevel findByDescription(String desc) {
    List<CourseLevel> list = findAllCourseLevel();
    if (!list.isEmpty()) {
      return list.stream()
          .filter(courseLevel -> courseLevel.getDescription().equalsIgnoreCase(desc))
          .findAny()
          .orElse(null);
    } else {
      return null;
    }
  }

  private void checkIfInUse(CourseLevel courseLevel) throws Exception {
    long count = courseService.countByCourseLevel(courseLevel);
    if (count > 0) {
      throw new Exception("This course level cannot be deleted because it's being used");
    }
  }

  public ResponseEntity<? extends Serializable> saveCourseLevel(CourseLevelRequest request) {
    CourseLevel courseLevel = findById(request.getId());
    if (courseLevel == null) {
      courseLevel = new CourseLevel();
    }
    courseLevel.setDescription(request.getDescription());
    try {
      saveCourseLevel(courseLevel);
    } catch (Exception e) {
      return ResponseEntity.badRequest()
          .body(new MessageResponse(false, UNABLE_TO_PROCESS_REQUEST.concat(e.getMessage()), null));
    }
    return ResponseEntity.ok(new MessageResponse(true, "Course Level saved", null));
  }

  public ResponseEntity<? extends Serializable> deleteCourseLevel(Long id) {
    CourseLevel courseLevel = findById(id);
    if (courseLevel == null) {
      return ResponseEntity.badRequest()
          .body(new MessageResponse(false, "Invalid course level ID", null));
    }
    try {
      deleteCourseLevel(courseLevel);
    } catch (Exception e) {
      return ResponseEntity.badRequest()
          .body(new MessageResponse(false, UNABLE_TO_PROCESS_REQUEST.concat(e.getMessage()), null));
    }
    return ResponseEntity.ok(new MessageResponse(true, "Course level deleted", null));
  }

  public ResponseEntity<List<CourseLevel>> findAllCourseLevels() {
    List<CourseLevel> list = findAllCourseLevel();
    if (!list.isEmpty()) {
      list.sort(Comparator.comparing(CourseLevel::getDescription));
    }
    return ResponseEntity.ok(list);
  }
}
