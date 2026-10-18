package com.tertiary.verify.data_model.model.repositories.lookup;

import com.tertiary.verify.data_model.model.entities.lookup.City;
import com.tertiary.verify.data_model.model.entities.lookup.Province;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CityRepository extends JpaRepository<City, Integer> {

  City findByIdAndDeleted(Long parseLong, boolean deleted);

  List<City> findByProvinceAndDeleted(Province province, boolean deleted);

  List<City> findByProvinceIdAndDeleted(Long provinceId, boolean deleted);

  List<City> findByDescriptionAndDeleted(String desc, boolean deleted);

  long countByProvinceAndDeleted(Province province, boolean deleted);

  List<City> findByDeleted(boolean deleted);
}
