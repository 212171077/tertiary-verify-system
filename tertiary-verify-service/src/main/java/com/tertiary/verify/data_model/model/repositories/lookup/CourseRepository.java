package com.tertiary.verify.data_model.model.repositories.lookup;

import com.tertiary.verify.data_model.model.beans.AllCourseDTO;
import com.tertiary.verify.data_model.model.entities.lookup.Course;
import com.tertiary.verify.data_model.model.entities.lookup.CourseLevel;
import com.tertiary.verify.data_model.model.entities.lookup.CourseType;
import java.util.List;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseRepository extends JpaRepository<Course, Integer> {

  Course findByIdAndDeleted(Long parseLong, boolean deleted);

  @Cacheable("courses")
  @Query(
      "SELECT new com.tertiary.verify.data_model.model.beans.AllCourseDTO(o.id,o.description) FROM Course o WHERE o.deleted = false GROUP BY o.description")
  List<AllCourseDTO> findAllCourseDTO();

  long countByCourseLevelAndDeleted(CourseLevel courseLevel, boolean deleted);

  long countByCourseTypeAndDeleted(CourseType courseType, boolean deleted);

  List<Course> findByDeleted(boolean deleted);
}
