package com.tertiary.verify.service.lookup;

import static com.tertiary.verify.utils.ConstantUtil.UNABLE_TO_PROCESS_REQUEST;

import com.tertiary.verify.data_model.model.beans.MessageResponse;
import com.tertiary.verify.data_model.model.beans.lookup.FacultyRequest;
import com.tertiary.verify.data_model.model.entities.lookup.Course;
import com.tertiary.verify.data_model.model.entities.lookup.Faculty;
import com.tertiary.verify.data_model.model.entities.lookup.FacultyCourse;
import com.tertiary.verify.data_model.model.repositories.lookup.FacultyRepository;
import com.tertiary.verify.framework.AbstractService;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class FacultyService extends AbstractService {
  @Autowired FacultyRepository repository;

  @Autowired FacultyCourseService facultyCourseServiceService;

  @Autowired private FacultyCourseService facultyCourseService;

  @CacheEvict(value = "faculties", allEntries = true)
  public void saveFaculty(Faculty faculty) throws Exception {
    checkIfExist(faculty.getDescription());
    if (faculty.getId() == null) {
      faculty.setCreateDate(new Date());
    } else {
      faculty.setLastUpdateDate(new Date());
    }
    repository.save(faculty);
  }

  @CacheEvict(value = "faculties", allEntries = true)
  public void submitFaculty(Faculty faculty, List<Course> courseList) {
    if (faculty.getId() == null) {
      faculty.setCreateDate(new Date());
      repository.save(faculty);
      if (courseList != null && courseList.size() > 0) {
        for (Course course : courseList) {
          FacultyCourse fc = new FacultyCourse();
          fc.setCourse(course);
          fc.setFaculty(faculty);
          facultyCourseServiceService.saveFacultyCourse(fc);
        }
      }
    } else {
      ArrayList<FacultyCourse> currentCouseList =
          (ArrayList<FacultyCourse>) facultyCourseService.findByFaculty(faculty);
      facultyCourseService.deleteAll(currentCouseList);
      repository.save(faculty);
      if (courseList != null && courseList.size() > 0) {
        for (Course course : courseList) {
          FacultyCourse fc = new FacultyCourse();
          fc.setCourse(course);
          fc.setFaculty(faculty);
          facultyCourseServiceService.saveFacultyCourse(fc);
        }
      }
    }
  }

  @CacheEvict(value = "faculties", allEntries = true)
  public void deleteFaculty(Faculty faculty) {
    repository.delete(faculty);
  }

  @CacheEvict(value = "faculties", allEntries = true)
  public void deleteFacultyByID(Integer arg0) {
    repository.deleteById(arg0);
  }

  @Cacheable("faculties")
  public List<Faculty> findAllFaculty() {
    return repository.findByDeleted(false);
  }

  @Cacheable("faculties")
  public Faculty findById(Long parseLong) {
    return repository.findByIdAndDeleted(parseLong, false);
  }

  private void checkIfExist(String desc) throws Exception {
    List<Faculty> list = findAllFaculty();
    if (!list.isEmpty()) {
      Faculty existingFaculty =
          list.stream()
              .filter(faculty -> faculty.getDescription().equalsIgnoreCase(desc))
              .findAny()
              .orElse(null);
      if (existingFaculty != null) {
        throw new Exception("Faculty already exist");
      }
    }
  }

  public ResponseEntity<? extends Serializable> saveFaculty(FacultyRequest request) {
    Faculty faculty = findById(request.getId());
    if (faculty == null) {
      faculty = new Faculty();
    }
    try {
      faculty.setDescription(request.getDescription());
      faculty.setCode(request.getCode());
      saveFaculty(faculty);
    } catch (Exception e) {
      return ResponseEntity.badRequest()
          .body(new MessageResponse(false, UNABLE_TO_PROCESS_REQUEST.concat(e.getMessage()), null));
    }
    return ResponseEntity.ok(new MessageResponse(true, "Faculty saved", null));
  }

  public ResponseEntity<? extends Serializable> deleteFaculty(Long id) {
    Faculty faculty = findById(id);
    if (faculty == null) {
      return ResponseEntity.badRequest()
          .body(new MessageResponse(false, "Invalid faculty ID", null));
    }
    try {
      deleteFaculty(faculty);
    } catch (Exception e) {
      return ResponseEntity.badRequest()
          .body(new MessageResponse(false, UNABLE_TO_PROCESS_REQUEST.concat(e.getMessage()), null));
    }
    return ResponseEntity.ok(new MessageResponse(true, "Faculty deleted", null));
  }

  public ResponseEntity<List<Faculty>> findAllFaculties() {
    List<Faculty> list = findAllFaculty();
    if (!list.isEmpty()) {
      list.sort(Comparator.comparing(Faculty::getDescription));
    }
    return ResponseEntity.ok(list);
  }
}
