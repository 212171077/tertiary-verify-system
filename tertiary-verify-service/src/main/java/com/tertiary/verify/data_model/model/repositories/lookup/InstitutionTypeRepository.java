package com.tertiary.verify.data_model.model.repositories.lookup;

import com.tertiary.verify.data_model.model.entities.lookup.InstitutionType;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InstitutionTypeRepository extends JpaRepository<InstitutionType, Integer> {

  InstitutionType findByIdAndDeleted(Long parseLong, boolean deleted);

  List<InstitutionType> findByDescriptionAndDeleted(String desc, boolean deleted);

  List<InstitutionType> findByDeleted(boolean deleted);
}
