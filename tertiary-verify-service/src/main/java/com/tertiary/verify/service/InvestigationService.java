package com.tertiary.verify.service;

import com.tertiary.verify.data_model.model.beans.InvestigationRequest;
import com.tertiary.verify.data_model.model.beans.MessageResponse;
import com.tertiary.verify.data_model.model.entities.Investigation;
import com.tertiary.verify.data_model.model.entities.ReportedInstitutions;
import com.tertiary.verify.data_model.model.repositories.InvestigationRepository;
import com.tertiary.verify.framework.AbstractService;
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
public class InvestigationService extends AbstractService {

  private static final Logger LOGGER = LoggerFactory.getLogger(InvestigationService.class);
  @Autowired public ReportedInstitutionsService reportedInstitutionsService;
  @Autowired InvestigationRepository repository;

  @CacheEvict(value = "investigations", allEntries = true)
  public void saveInvestigation(Investigation investigation) {
    if (investigation.getId() == null) {
      investigation.setCreateDate(new Date());
    } else {
      investigation.setLastUpdateDate(new Date());
    }
    repository.save(investigation);
  }

  @CacheEvict(value = "investigations", allEntries = true)
  public void deleteInvestigation(Investigation investigation) {
    investigation.setDeleted(true);
    repository.save(investigation);
  }

  @Cacheable("investigations")
  public List<Investigation> findAllInvestigations() {
    return repository.findAll();
  }

  public Investigation findById(Long parseLong) {
    return repository.findByIdAndDeleted(parseLong, false);
  }

  public ResponseEntity<?> saveInvestigation(InvestigationRequest request) {
    ReportedInstitutions reportedInstitutions =
        reportedInstitutionsService.findById(request.getReportedInstitutionId());
    if (reportedInstitutions == null) {
      LOGGER.error("Invalid Reported Institution ID :{} ", request.getReportedInstitutionId());
      return ResponseEntity.badRequest()
          .body(new MessageResponse(false, "Invalid Reported Institution ID", null));
    }
    reportedInstitutions.setStatus(request.getStatus());
    reportedInstitutionsService.saveReportedInstitutions(reportedInstitutions);

    Investigation investigation = new Investigation();
    investigation.setReportedInstitution(reportedInstitutions);
    investigation.setDescription(request.getFeedbackDetails());
    investigation.setDeleted(false);
    saveInvestigation(investigation);

    return ResponseEntity.ok()
        .body(new MessageResponse(true, "Investigation feedback saved", null));
  }
}
