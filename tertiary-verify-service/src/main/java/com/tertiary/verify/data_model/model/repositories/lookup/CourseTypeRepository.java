package com.tertiary.verify.data_model.model.repositories.lookup;

import com.tertiary.verify.data_model.model.entities.lookup.CourseType;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseTypeRepository extends JpaRepository<CourseType, Integer> {

  CourseType findByIdAndDeleted(Long parseLong, boolean deleted);

  List<CourseType> findByDescriptionAndDeleted(String desc, boolean deleted);

  List<CourseType> findByDeleted(boolean deleted);
}
