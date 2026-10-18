package com.tertiary.verify.data_model.model.repositories;

import com.tertiary.verify.data_model.model.entities.Investigation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvestigationRepository extends JpaRepository<Investigation, Long> {
  Investigation findByIdAndDeleted(Long parseLong, boolean deleted);
}
