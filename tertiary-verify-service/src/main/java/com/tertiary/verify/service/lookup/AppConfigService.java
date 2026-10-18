package com.tertiary.verify.service.lookup;

import com.tertiary.verify.data_model.model.beans.MessageResponse;
import com.tertiary.verify.data_model.model.beans.lookup.AppConfigRequest;
import com.tertiary.verify.data_model.model.entities.lookup.AppConfig;
import com.tertiary.verify.data_model.model.repositories.lookup.AppConfigRepository;
import com.tertiary.verify.framework.AbstractService;
import com.tertiary.verify.service.TertiaryVerifyService;
import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class AppConfigService extends AbstractService {
  private static final Logger LOGGER = LoggerFactory.getLogger(TertiaryVerifyService.class);

  @Autowired private AppConfigRepository repository;

  @CacheEvict(value = "app_configs", allEntries = true)
  public void saveAppConfig(AppConfig appConfig) {
    if (appConfig.getId() == null) {
      appConfig.setDeleted(false);
      appConfig.setCreateDate(new Date());
    } else {
      appConfig.setLastUpdateDate(new Date());
    }
    repository.save(appConfig);
  }

  @CacheEvict(value = "app_configs", allEntries = true)
  public void deleteAppConfig(AppConfig appConfig) {
    LOGGER.info("Delete App Config");
    appConfig.setDeleted(true);
    repository.save(appConfig);
  }

  @Cacheable("app_configs")
  public List<AppConfig> findAllAppConfig() {
    LOGGER.info("Find All App Config");
    return repository.findByDeleted(false);
  }

  public AppConfig findById(Long parseLong) {
    LOGGER.info("Find By ID");
    return repository.findByIdAndDeleted(parseLong, false);
  }

  public ResponseEntity<? extends Serializable> saveAppConfig(AppConfigRequest request) {
    AppConfig appConfig = findById(request.getId());
    if (appConfig == null) {
      appConfig = new AppConfig();
    }
    appConfig.setCode(request.getCode());
    appConfig.setValue(request.getValue());
    saveAppConfig(appConfig);
    return ResponseEntity.ok(new MessageResponse(true, "Application configuration saved", null));
  }

  public ResponseEntity<? extends Serializable> deleteAppConfig(Long id) {
    AppConfig appConfig = findById(id);
    if (appConfig == null) {
      return ResponseEntity.badRequest()
          .body(new MessageResponse(false, "Invalid Application configuration ID", null));
    }
    deleteAppConfig(appConfig);
    return ResponseEntity.ok(new MessageResponse(true, "Application configuration deleted", null));
  }

  public ResponseEntity<List<AppConfig>> findAllAppConfigs() {
    List<AppConfig> list = findAllAppConfig();
    if (!list.isEmpty()) {
      list.sort(Comparator.comparing(AppConfig::getValue));
    }
    return ResponseEntity.ok(list);
  }
}
