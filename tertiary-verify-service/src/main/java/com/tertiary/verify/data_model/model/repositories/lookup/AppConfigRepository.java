package com.tertiary.verify.data_model.model.repositories.lookup;

import com.tertiary.verify.data_model.model.entities.lookup.AppConfig;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppConfigRepository extends JpaRepository<AppConfig, Integer> {

  AppConfig findByIdAndDeleted(Long parseLong, boolean deleted);

  List<AppConfig> findByCodeAndDeleted(String code, boolean deleted);

  List<AppConfig> findByDeleted(boolean deleted);
}
