package com.tertiary.verify.service;

import com.tertiary.verify.data_model.model.beans.MessageResponse;
import com.tertiary.verify.data_model.model.beans.RejectReasonResponse;
import com.tertiary.verify.data_model.model.entities.Institution;
import com.tertiary.verify.data_model.model.entities.InstitutionRejectReason;
import com.tertiary.verify.data_model.model.entities.lookup.RejectReason;
import com.tertiary.verify.data_model.model.repositories.InstitutionRejectReasonRepository;
import com.tertiary.verify.framework.AbstractService;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class InstitutionRejectReasonService extends AbstractService {
  private static final Logger LOGGER =
      LoggerFactory.getLogger(InstitutionRejectReasonService.class);
  @Autowired InstitutionRejectReasonRepository repository;
  @Autowired private InstitutionService institutionService;

  @CacheEvict(value = "institution_reject_reasons", allEntries = true)
  public void saveInstitutionRejectReason(InstitutionRejectReason institutionRejectReason) {
    if (institutionRejectReason.getId() == null) {
      institutionRejectReason.setCreateDate(new Date());
    }
    repository.save(institutionRejectReason);
  }

  @CacheEvict(value = "institution_reject_reasons", allEntries = true)
  public void deleteInstitutionRejectReason(InstitutionRejectReason institutionRejectReason) {
    repository.delete(institutionRejectReason);
  }

  @CacheEvict(value = "institution_reject_reasons", allEntries = true)
  public void deleteInstitutionRejectReasonByID(Integer arg0) {
    repository.deleteById(arg0);
  }

  @Cacheable("institution_reject_reasons")
  public List<InstitutionRejectReason> findAllInstitutionRejectReason() {
    return repository.findAll();
  }

  public InstitutionRejectReason findById(Long parseLong) {
    return repository.findById(parseLong);
  }

  public List<InstitutionRejectReason> findByInstitution(Institution institution) {
    return repository.findByInstitution(institution);
  }

  public long countByRejectReason(RejectReason rejectReason) {
    return repository.countByRejectReason(rejectReason);
  }

  public ResponseEntity<?> findByInstitutionId(Long id) {
    List<RejectReasonResponse> list = new ArrayList<>();
    Institution institution = institutionService.findInstitutionById(id);

    if (institution == null) {
      LOGGER.error("Invalid institution ID :{} ", id);
      return ResponseEntity.badRequest()
          .body(new MessageResponse(false, "Invalid institution ID", null));
    }

    List<InstitutionRejectReason> rejectReasonList =
        findByInstitution(institutionService.findInstitutionById(id));
    if (rejectReasonList != null && !rejectReasonList.isEmpty()) {
      list =
          rejectReasonList.stream()
              .map(
                  rj ->
                      RejectReasonResponse.builder()
                          .description(rj.getRejectReason().getDescription())
                          .detailInfo(rj.getRejectReason().getDetailInfo())
                          .build())
              .collect(Collectors.toList());
    }
    return ResponseEntity.ok().body(list);
  }
}
