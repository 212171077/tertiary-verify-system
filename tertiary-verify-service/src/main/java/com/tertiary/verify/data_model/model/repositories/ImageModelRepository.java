package com.tertiary.verify.data_model.model.repositories;

import com.tertiary.verify.data_model.model.entities.ImageModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageModelRepository extends JpaRepository<ImageModel, Long> {

  @Query("select c from ImageModel c where c.id = ?1")
  ImageModel getById(Long id);
}
