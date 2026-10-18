package com.tertiary.verify.data_model.model.repositories;

import com.tertiary.verify.data_model.model.entities.Address;
import com.tertiary.verify.data_model.model.entities.lookup.City;
import com.tertiary.verify.data_model.model.entities.lookup.Province;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressRepository extends JpaRepository<Address, Integer> {

  String FIND_BY_ID_LIST = "SELECT o FROM Address o WHERE id IN ?1";

  @Query(FIND_BY_ID_LIST)
  List<Address> findByIds(List<Long> idList);

  Address findById(Long parseLong);

  long countByCity(City city);

  long countByProvince(Province province);
}
