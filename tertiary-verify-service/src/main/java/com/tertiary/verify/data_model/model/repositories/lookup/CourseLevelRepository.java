package com.tertiary.verify.data_model.model.repositories.lookup;

import com.tertiary.verify.data_model.model.entities.lookup.CourseLevel;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseLevelRepository extends JpaRepository<CourseLevel, Integer> {

  CourseLevel findByIdAndDeleted(Long parseLong, boolean deleted);

  List<CourseLevel> findByDescriptionAndDeleted(String desc, boolean deleted);

  List<CourseLevel> findByDeleted(boolean deleted);
}
