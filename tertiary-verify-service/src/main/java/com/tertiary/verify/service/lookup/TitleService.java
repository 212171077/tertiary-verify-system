package com.tertiary.verify.service.lookup;

import static com.tertiary.verify.utils.ConstantUtil.UNABLE_TO_PROCESS_REQUEST;

import com.tertiary.verify.data_model.model.beans.MessageResponse;
import com.tertiary.verify.data_model.model.beans.lookup.TitleRequest;
import com.tertiary.verify.data_model.model.entities.lookup.Title;
import com.tertiary.verify.data_model.model.repositories.lookup.TitleRepository;
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
public class TitleService extends AbstractService {
  @Autowired private TitleRepository repository;
  @Autowired private UserService userService;

  @CacheEvict(value = "titles", allEntries = true)
  public void saveTitle(Title title) throws Exception {
    if (title.getId() == null) {
      checkIfExist(title.getDescription());
      title.setDeleted(false);
      title.setCreateDate(new Date());
    } else {
      title.setLastUpdateDate(new Date());
    }
    repository.save(title);
  }

  @CacheEvict(value = "titles", allEntries = true)
  public void deleteTitle(Title title) throws Exception {
    checkIfInUse(title);
    title.setDeleted(true);
    repository.save(title);
  }

  @Cacheable("titles")
  public List<Title> findAllTitle() {
    return repository.findByDeleted(false);
  }

  @Cacheable("titles")
  public Title findById(Long parseLong) {
    return repository.findByIdAndDeleted(parseLong, false);
  }

  private void checkIfExist(String desc) throws Exception {
    List<Title> list = findAllTitle();
    if (!list.isEmpty()) {
      Title existingTitle =
          list.stream()
              .filter(title -> title.getDescription().equalsIgnoreCase(desc))
              .findAny()
              .orElse(null);
      if (existingTitle != null) {
        throw new Exception("Title already exist");
      }
    }
  }

  public Title findByDesc(String desc) {
    List<Title> list = findAllTitle();
    if (!list.isEmpty()) {
      Title title =
          list.stream()
              .filter(t -> t.getDescription().equalsIgnoreCase(desc))
              .findAny()
              .orElse(null);
      return title;
    } else {
      return null;
    }
  }

  private void checkIfInUse(Title title) throws Exception {
    long count = userService.countByTitle(title);
    if (count > 0) {
      throw new Exception("This title cannot be deleted because it's being used");
    }
  }

  public ResponseEntity<? extends Serializable> saveTitle(TitleRequest request) {
    Title title = findById(request.getId());
    if (title == null) {
      title = new Title();
    }
    try {
      title.setDescription(request.getDescription());
      title.setCode(request.getCode());
      saveTitle(title);
    } catch (Exception e) {
      return ResponseEntity.badRequest()
          .body(new MessageResponse(false, UNABLE_TO_PROCESS_REQUEST.concat(e.getMessage()), null));
    }
    return ResponseEntity.ok(new MessageResponse(true, "Title saved", null));
  }

  public ResponseEntity<? extends Serializable> deleteTitle(Long id) {
    Title title = findById(id);
    if (title == null) {
      return ResponseEntity.badRequest().body(new MessageResponse(false, "Invalid title ID", null));
    }
    try {
      deleteTitle(title);
    } catch (Exception e) {
      return ResponseEntity.badRequest()
          .body(new MessageResponse(false, UNABLE_TO_PROCESS_REQUEST.concat(e.getMessage()), null));
    }
    return ResponseEntity.ok(new MessageResponse(true, "Title deleted", null));
  }

  public ResponseEntity<List<Title>> findAllTitles() {
    List<Title> list = findAllTitle();
    if (!list.isEmpty()) {
      list.sort(Comparator.comparing(Title::getDescription));
    }
    return ResponseEntity.ok(list);
  }
}
