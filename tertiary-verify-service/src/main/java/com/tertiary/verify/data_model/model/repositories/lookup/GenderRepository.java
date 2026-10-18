package com.tertiary.verify.data_model.model.repositories.lookup;

import com.tertiary.verify.data_model.model.entities.lookup.Gender;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GenderRepository extends JpaRepository<Gender, Integer> {

  Gender findByIdAndDeleted(Long parseLong, boolean deleted);

  Gender findByGenderNameAndDeleted(String name, boolean deleted);

  List<Gender> findByDeleted(boolean deleted);
}
