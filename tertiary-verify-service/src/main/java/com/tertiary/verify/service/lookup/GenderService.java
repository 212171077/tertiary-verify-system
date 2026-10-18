package com.tertiary.verify.service.lookup;

import static com.tertiary.verify.utils.ConstantUtil.UNABLE_TO_PROCESS_REQUEST;

import com.tertiary.verify.data_model.model.beans.MessageResponse;
import com.tertiary.verify.data_model.model.beans.lookup.GenderRequest;
import com.tertiary.verify.data_model.model.entities.lookup.Gender;
import com.tertiary.verify.data_model.model.repositories.lookup.GenderRepository;
import com.tertiary.verify.framework.AbstractService;
import com.tertiary.verify.service.UserService;
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
public class GenderService extends AbstractService {
  @Autowired private GenderRepository repository;

  @Autowired private UserService userService;

  @CacheEvict(value = "genders", allEntries = true)
  public void saveGender(Gender gender) throws Exception {

    if (gender.getId() == null) {
      checkIfExist(gender.getGenderName());
      gender.setDeleted(false);
      gender.setCreateDate(new Date());
    } else {
      gender.setLastUpdateDate(new Date());
    }

    repository.saveAndFlush(gender);
  }

  @Cacheable("genders")
  public Gender findByGenderName(String name) {
    return repository.findByGenderNameAndDeleted(name, false);
  }

  @CacheEvict(value = "genders", allEntries = true)
  public void deleteGender(Gender gender) throws Exception {
    checkIfInUse(gender);
    gender.setDeleted(true);
    repository.save(gender);
  }

  @CacheEvict(value = "genders", allEntries = true)
  public List<Gender> findAllGender() {
    return repository.findByDeleted(false);
  }

  @Cacheable("genders")
  public Gender findById(Long parseLong) {
    return repository.findByIdAndDeleted(parseLong, false);
  }

  private void checkIfExist(String desc) throws Exception {
    List<Gender> list = findAllGender();
    if (!list.isEmpty()) {
      Gender existingGender =
          list.stream()
              .filter(gender -> gender.getGenderName().equalsIgnoreCase(desc))
              .findAny()
              .orElse(null);
      if (existingGender != null) {
        throw new Exception("Gender already exist");
      }
    }
  }

  private void checkIfInUse(Gender gender) throws Exception {
    long count = userService.countByGender(gender);
    if (count > 0) {
      throw new Exception("This gender cannot be deleted because it's being used");
    }
  }

  public ResponseEntity<? extends Serializable> saveGender(GenderRequest request) {
    Gender gender = findById(request.getId());
    if (gender == null) {
      gender = new Gender();
    }
    try {
      gender.setGenderName(request.getGenderName());
      gender.setCode(request.getCode());
      saveGender(gender);
    } catch (Exception e) {
      return ResponseEntity.badRequest()
          .body(new MessageResponse(false, UNABLE_TO_PROCESS_REQUEST.concat(e.getMessage()), null));
    }
    return ResponseEntity.ok(new MessageResponse(true, "Gender saved", null));
  }

  public ResponseEntity<? extends Serializable> deleteGender(Long id) {
    Gender gender = findById(id);
    if (gender == null) {
      return ResponseEntity.badRequest()
          .body(new MessageResponse(false, "Invalid gender ID", null));
    }
    try {
      deleteGender(gender);
    } catch (Exception e) {
      return ResponseEntity.badRequest()
          .body(new MessageResponse(false, UNABLE_TO_PROCESS_REQUEST.concat(e.getMessage()), null));
    }
    return ResponseEntity.ok(new MessageResponse(true, "Gender deleted", null));
  }

  public ResponseEntity<List<Gender>> findAllGenders() {
    List<Gender> list = findAllGender();
    if (!list.isEmpty()) {
      list.sort(Comparator.comparing(Gender::getGenderName));
    }
    return ResponseEntity.ok(list);
  }
}
