package com.tertiary.verify.data_model.model.repositories.lookup;

import com.tertiary.verify.data_model.model.entities.lookup.Course;
import com.tertiary.verify.data_model.model.entities.lookup.Faculty;
import com.tertiary.verify.data_model.model.entities.lookup.FacultyCourse;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FacultyCourseRepository extends JpaRepository<FacultyCourse, Integer> {

  FacultyCourse findByIdAndDeleted(Long parseLong, boolean deleted);

  List<FacultyCourse> findByFacultyAndDeleted(Faculty faculty, boolean deleted);

  long countByCourseAndDeleted(Course course, boolean deleted);

  List<FacultyCourse> findByDeleted(boolean deleted);
}
