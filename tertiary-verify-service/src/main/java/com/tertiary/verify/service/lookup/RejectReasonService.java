package com.tertiary.verify.service.lookup;

import static com.tertiary.verify.utils.ConstantUtil.UNABLE_TO_PROCESS_REQUEST;

import com.tertiary.verify.data_model.model.beans.MessageResponse;
import com.tertiary.verify.data_model.model.beans.lookup.RejectReasonRequest;
import com.tertiary.verify.data_model.model.entities.lookup.RejectReason;
import com.tertiary.verify.data_model.model.repositories.lookup.RejectReasonRepository;
import com.tertiary.verify.framework.AbstractService;
import com.tertiary.verify.service.InstitutionRejectReasonService;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class RejectReasonService extends AbstractService {
  @Autowired private RejectReasonRepository repository;

  @Autowired private InstitutionRejectReasonService institutionRejectReasonService;

  @CacheEvict(value = "reject_reason", allEntries = true)
  public void saveRejectReason(RejectReason rejectReason) throws Exception {
    if (rejectReason.getId() == null) {
      checkIfExist(rejectReason.getDescription());
      rejectReason.setDeleted(false);
      rejectReason.setCreateDate(new Date());
    } else {
      rejectReason.setLastUpdateDate(new Date());
    }
    repository.save(rejectReason);
  }

  @CacheEvict(value = "reject_reason", allEntries = true)
  public void deleteRejectReason(RejectReason rejectReason) throws Exception {
    checkIfInUse(rejectReason);
    rejectReason.setDeleted(true);
    repository.save(rejectReason);
  }

  @Cacheable("reject_reason")
  public List<RejectReason> findAllRejectReason() {
    return repository.findByDeleted(false);
  }

  @Cacheable("reject_reason")
  public RejectReason findById(Long parseLong) {
    return repository.findByIdAndDeleted(parseLong, false);
  }

  public List<RejectReason> findByDescriptionStartingWith(String description) {
    List<RejectReason> list = findAllRejectReason();
    if (!list.isEmpty()) {
      return list.stream()
          .filter(rejectReason -> rejectReason.getDescription().startsWith(description))
          .collect(Collectors.toList());
    } else {
      return new ArrayList<>();
    }
  }

  private void checkIfExist(String desc) throws Exception {
    List<RejectReason> list = findAllRejectReason();
    if (!list.isEmpty()) {
      RejectReason existingRejectReason =
          list.stream()
              .filter(rejectReason -> rejectReason.getDescription().equalsIgnoreCase(desc))
              .findAny()
              .orElse(null);
      if (existingRejectReason != null) {
        throw new Exception("Reject reason already exist");
      }
    }
  }

  private void checkIfInUse(RejectReason rejectReason) throws Exception {
    long count = institutionRejectReasonService.countByRejectReason(rejectReason);
    if (count > 0) {
      throw new Exception("This reject reason cannot be deleted because it's being used");
    }
  }

  public ResponseEntity<? extends Serializable> saveRejectReason(RejectReasonRequest request) {
    RejectReason rejectReason = findById(request.getId());
    if (rejectReason == null) {
      rejectReason = new RejectReason();
    }
    try {
      rejectReason.setDescription(request.getDescription());
      rejectReason.setCode(request.getCode());
      rejectReason.setDetailInfo(request.getDetailInfo());
      saveRejectReason(rejectReason);
    } catch (Exception e) {
      return ResponseEntity.badRequest()
          .body(new MessageResponse(false, UNABLE_TO_PROCESS_REQUEST.concat(e.getMessage()), null));
    }
    return ResponseEntity.ok(new MessageResponse(true, "Reject reason saved", null));
  }

  public ResponseEntity<? extends Serializable> deleteRejectReason(Long id) {
    RejectReason rejectReason = findById(id);
    if (rejectReason == null) {
      return ResponseEntity.badRequest()
          .body(new MessageResponse(false, "Invalid reject reason ID", null));
    }
    try {
      deleteRejectReason(rejectReason);
    } catch (Exception e) {
      return ResponseEntity.badRequest()
          .body(new MessageResponse(false, UNABLE_TO_PROCESS_REQUEST.concat(e.getMessage()), null));
    }
    return ResponseEntity.ok(new MessageResponse(true, "Reject reason deleted", null));
  }

  public ResponseEntity<List<RejectReason>> findAllRejectReasons() {
    List<RejectReason> list = findAllRejectReason();
    if (!list.isEmpty()) {
      list.sort(Comparator.comparing(RejectReason::getDescription));
    }
    return ResponseEntity.ok(list);
  }
}
