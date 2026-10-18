package com.tertiary.verify.service.lookup;

import static com.tertiary.verify.utils.ConstantUtil.UNABLE_TO_PROCESS_REQUEST;

import com.tertiary.verify.data_model.model.beans.MessageResponse;
import com.tertiary.verify.data_model.model.beans.lookup.InstitutionTypeRequest;
import com.tertiary.verify.data_model.model.entities.lookup.InstitutionType;
import com.tertiary.verify.data_model.model.repositories.lookup.InstitutionTypeRepository;
import com.tertiary.verify.framework.AbstractService;
import com.tertiary.verify.service.InstitutionService;
import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class InstitutionTypeService extends AbstractService {
  @Autowired private InstitutionTypeRepository repository;

  @Autowired private InstitutionService institutionService;

  @CacheEvict(value = "institution_type", allEntries = true)
  public void saveInstitutionType(InstitutionType institutionType) throws Exception {
    if (institutionType.getId() == null) {
      checkIfExist(institutionType.getDescription());
      institutionType.setDeleted(false);
      institutionType.setCreateDate(new Date());
    } else {
      institutionType.setLastUpdateDate(new Date());
    }
    repository.save(institutionType);
  }

  @CacheEvict(value = "institution_type", allEntries = true)
  public void deleteInstitutionType(InstitutionType institutionType) throws Exception {
    checkIfInUse(institutionType);
    institutionType.setDeleted(true);
    repository.save(institutionType);
  }

  @Cacheable("institution_type")
  public List<InstitutionType> findAllInstitutionType() {
    return repository.findByDeleted(false);
  }

  @Cacheable("institution_type")
  public InstitutionType findById(Long parseLong) {
    return repository.findByIdAndDeleted(parseLong, false);
  }

  private void checkIfExist(String desc) throws Exception {
    List<InstitutionType> list = findAllInstitutionType();
    if (!list.isEmpty()) {
      InstitutionType existingInstType =
          list.stream()
              .filter(institutionType -> institutionType.getDescription().equalsIgnoreCase(desc))
              .findAny()
              .orElse(null);
      if (existingInstType != null) {
        throw new Exception("Institution type already exist");
      }
    }
  }

  private void checkIfInUse(InstitutionType institutionType) throws Exception {
    long count = institutionService.countByInstitutionType(institutionType);
    if (count > 0) {
      throw new Exception("This institution type cannot be deleted because it's being used");
    }
  }

  public ResponseEntity<? extends Serializable> saveInstitutionType(
      InstitutionTypeRequest request) {
    InstitutionType institutionType = findById(request.getId());
    if (institutionType == null) {
      institutionType = new InstitutionType();
    }
    try {
      institutionType.setDescription(request.getDescription());
      saveInstitutionType(institutionType);
    } catch (Exception e) {
      return ResponseEntity.badRequest()
          .body(new MessageResponse(false, UNABLE_TO_PROCESS_REQUEST.concat(e.getMessage()), null));
    }
    return ResponseEntity.ok(new MessageResponse(true, "Institution type saved", null));
  }

  public ResponseEntity<? extends Serializable> deleteInstitutionType(Long id) {
    InstitutionType institutionType = findById(id);
    if (institutionType == null) {
      return ResponseEntity.badRequest()
          .body(new MessageResponse(false, "Invalid institution type ID", null));
    }
    try {
      deleteInstitutionType(institutionType);
    } catch (Exception e) {
      return ResponseEntity.badRequest()
          .body(new MessageResponse(false, UNABLE_TO_PROCESS_REQUEST.concat(e.getMessage()), null));
    }
    return ResponseEntity.ok(new MessageResponse(true, "Institution type deleted", null));
  }

  public ResponseEntity<List<InstitutionType>> findAllInstitutionTypes() {
    List<InstitutionType> list = findAllInstitutionType();
    if (!list.isEmpty()) {
      list.sort(Comparator.comparing(InstitutionType::getDescription));
    }
    return ResponseEntity.ok(list);
  }

  public InstitutionType findByDescription(String desc) {
    List<InstitutionType> list = findAllInstitutionType();
    if (!list.isEmpty()) {
      InstitutionType existingInstType =
          list.stream()
              .filter(institutionType -> institutionType.getDescription().equalsIgnoreCase(desc))
              .findAny()
              .orElse(null);
      return existingInstType;
    }
    return null;
  }
}
