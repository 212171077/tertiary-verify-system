package com.tertiary.verify.service;

import com.tertiary.verify.data_model.model.entities.MailLog;
import com.tertiary.verify.data_model.model.repositories.MailLogRepository;
import com.tertiary.verify.framework.AbstractService;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class MailLogService extends AbstractService {
  @Autowired MailLogRepository repository;

  @CacheEvict(value = "mail_logs", allEntries = true)
  public void saveMailLog(MailLog mailLog) {
    if (mailLog.getId() == null) {
      mailLog.setCreateDate(new Date());
      mailLog.setDeleted(false);
    } else {
      mailLog.setLastUpdateDate(new Date());
    }

    repository.save(mailLog);
  }

  @CacheEvict(value = "mail_logs", allEntries = true)
  public void deleteMailLog(MailLog mailLog) {
    repository.delete(mailLog);
  }

  @CacheEvict(value = "mail_logs", allEntries = true)
  public void deleteMailLogByID(Integer arg0) {
    repository.deleteById(arg0);
  }

  public List<MailLog> findAllMailLog() {
    return repository.findAll();
  }

  @Cacheable("mail_logs")
  public Page<MailLog> findTop100ByOrderByCreateDateDesc() {
    PageRequest pageable = PageRequest.of(0, 100);
    return repository.findTop100ByOrderByCreateDateDesc(pageable);
  }

  public MailLog findById(Long parseLong) {
    return repository.findById(parseLong);
  }
}
