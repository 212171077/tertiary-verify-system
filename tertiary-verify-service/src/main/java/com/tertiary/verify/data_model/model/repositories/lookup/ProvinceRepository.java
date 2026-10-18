package com.tertiary.verify.data_model.model.repositories.lookup;

import com.tertiary.verify.data_model.model.entities.lookup.Province;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProvinceRepository extends JpaRepository<Province, Integer> {

  Province findByIdAndDeleted(Long id, boolean deleted);

  Province findByCodeAndDeleted(String code, boolean deleted);

  List<Province> findByDescriptionAndDeleted(String desc, boolean deleted);

  List<Province> findByDeleted(boolean deleted);
}
