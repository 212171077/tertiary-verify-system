package com.tertiary.verify.data_model.model.repositories;

import com.tertiary.verify.data_model.model.entities.DocByte;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocByteRepository extends JpaRepository<DocByte, Integer> {
  DocByte findById(Long pareLong);
}
