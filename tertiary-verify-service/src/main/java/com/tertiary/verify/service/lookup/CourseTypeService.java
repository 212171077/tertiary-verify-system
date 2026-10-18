package com.tertiary.verify.service.lookup;

import static com.tertiary.verify.utils.ConstantUtil.UNABLE_TO_PROCESS_REQUEST;

import com.tertiary.verify.data_model.model.beans.MessageResponse;
import com.tertiary.verify.data_model.model.beans.lookup.CourseTypeRequest;
import com.tertiary.verify.data_model.model.entities.lookup.CourseType;
import com.tertiary.verify.data_model.model.repositories.lookup.CourseTypeRepository;
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
public class CourseTypeService extends AbstractService {
  @Autowired private CourseTypeRepository repository;

  @Autowired private CourseService courseService;

  @CacheEvict(value = "course_types", allEntries = true)
  public void saveCourseType(CourseType courseType) throws Exception {
    if (courseType.getId() == null) {
      checkIfExist(courseType.getDescription());
      courseType.setDeleted(false);
      courseType.setCreateDate(new Date());
    } else {
      courseType.setLastUpdateDate(new Date());
    }
    repository.save(courseType);
  }

  @CacheEvict(value = "course_types", allEntries = true)
  public void deleteCourseType(CourseType courseType) throws Exception {
    checkIfInUse(courseType);
    courseType.setDeleted(true);
    repository.save(courseType);
  }

  @Cacheable("course_types")
  public List<CourseType> findAllCourseType() {
    return repository.findByDeleted(false);
  }

  @Cacheable("course_types")
  public CourseType findById(Long parseLong) {
    return repository.findByIdAndDeleted(parseLong, false);
  }

  private void checkIfExist(String desc) throws Exception {
    List<CourseType> list = findAllCourseType();
    if (!list.isEmpty()) {
      CourseType existingCourseType =
          list.stream()
              .filter(courseType -> courseType.getDescription().equalsIgnoreCase(desc))
              .findAny()
              .orElse(null);
      if (existingCourseType != null) {
        throw new Exception("Course Type already exist");
      }
    }
  }

  private void checkIfInUse(CourseType courseType) throws Exception {
    long count = courseService.countByCourseType(courseType);
    if (count > 0) {
      throw new Exception("This course type cannot be deleted because it's being used");
    }
  }

  public CourseType findByDescription(String desc) {
    List<CourseType> list = findAllCourseType();
    if (!list.isEmpty()) {
      return list.stream()
          .filter(courseType -> courseType.getDescription().equalsIgnoreCase(desc))
          .findAny()
          .orElse(null);
    } else {
      return null;
    }
  }

  public ResponseEntity<? extends Serializable> saveCourseType(CourseTypeRequest request) {
    CourseType courseType = findById(request.getId());
    if (courseType == null) {
      courseType = new CourseType();
    }
    try {
      courseType.setDescription(request.getDescription());
      saveCourseType(courseType);
    } catch (Exception e) {
      return ResponseEntity.badRequest()
          .body(new MessageResponse(false, UNABLE_TO_PROCESS_REQUEST.concat(e.getMessage()), null));
    }
    return ResponseEntity.ok(new MessageResponse(true, "Course type saved", null));
  }

  public ResponseEntity<? extends Serializable> deleteCourseType(Long id) {
    CourseType courseType = findById(id);
    if (courseType == null) {
      return ResponseEntity.badRequest()
          .body(new MessageResponse(false, "Invalid course type ID", null));
    }
    try {
      deleteCourseType(courseType);
    } catch (Exception e) {
      return ResponseEntity.badRequest()
          .body(new MessageResponse(false, UNABLE_TO_PROCESS_REQUEST.concat(e.getMessage()), null));
    }
    return ResponseEntity.ok(new MessageResponse(true, "Course type deleted", null));
  }

  public ResponseEntity<List<CourseType>> findAllCourseTypes() {
    List<CourseType> list = findAllCourseType();
    if (!list.isEmpty()) {
      list.sort(Comparator.comparing(CourseType::getDescription));
    }
    return ResponseEntity.ok(list);
  }
}
