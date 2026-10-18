package com.tertiary.verify.service;

import com.tertiary.verify.data_model.model.entities.EmailContent;
import com.tertiary.verify.data_model.model.repositories.EmailContentRepository;
import com.tertiary.verify.framework.AbstractService;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class EmailContentService extends AbstractService {
  @Autowired EmailContentRepository repository;

  @CacheEvict(value = "emails_content", allEntries = true)
  public void saveEmailContent(EmailContent emailContent) {
    if (emailContent.getId() == null) {
      emailContent.setCreateDate(new Date());
      emailContent.setDeleted(false);
    } else {
      emailContent.setLastUpdateDate(new Date());
    }
    repository.save(emailContent);
  }

  @CacheEvict(value = "emails_content", allEntries = true)
  public void deleteEmailContent(EmailContent emailContent) {
    repository.delete(emailContent);
  }

  @CacheEvict(value = "emails_content", allEntries = true)
  public void deleteEmailContentByID(Integer arg0) {
    repository.deleteById(arg0);
  }

  public List<EmailContent> findAllEmailContent() {
    return repository.findAll();
  }

  public Page<EmailContent> findTop100ByOrderByCreateDateDesc() {
    PageRequest pageable = PageRequest.of(0, 100);
    return repository.findTop100ByOrderByCreateDateDesc(pageable);
  }

  public EmailContent findById(Long parseLong) {
    return repository.findById(parseLong);
  }

  @CacheEvict(value = "emails_content", allEntries = true)
  public void deleteAllEmailContent(List<EmailContent> emailContents) {
    repository.deleteAll(emailContents);
  }

  public List<EmailContent> findTop100OrderByIdDesc() {
    return repository.findTop100ByOrderByIdDesc();
  }
}
