package com.tertiary.verify.data_model.model.repositories;

import com.tertiary.verify.data_model.model.entities.Address;
import com.tertiary.verify.data_model.model.entities.InstCourseAddress;
import com.tertiary.verify.data_model.model.entities.InstitutionCourse;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface InstCourseAddressRepository extends JpaRepository<InstCourseAddress, Integer> {
  String FIND_BY_ID_LIST = "SELECT o FROM InstCourseAddress o WHERE id IN ?1";
  String FIND_BY_INST_ID_LIST =
      "SELECT o FROM InstCourseAddress o WHERE o.institutionCourse.institution.id IN ?1";

  InstCourseAddress findById(Long parseLong);

  @Query(FIND_BY_ID_LIST)
  List<InstCourseAddress> findByIds(List<String> idList);

  @Query(FIND_BY_INST_ID_LIST)
  List<InstCourseAddress> findByInstitutionId(Long parseLong);

  List<InstCourseAddress> findByAddress(Address address);

  List<InstCourseAddress> findByInstitutionCourse(InstitutionCourse ic);
}
