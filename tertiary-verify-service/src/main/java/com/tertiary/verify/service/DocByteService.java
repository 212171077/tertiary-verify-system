package com.tertiary.verify.service;

import com.tertiary.verify.data_model.model.entities.DocByte;
import com.tertiary.verify.data_model.model.repositories.DocByteRepository;
import com.tertiary.verify.framework.AbstractService;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class DocByteService extends AbstractService {
  @Autowired DocByteRepository repository;

  @CacheEvict(value = "doc_bytes", allEntries = true)
  public void saveDocByte(DocByte docByte) {
    if (docByte.getId() == null) {
      docByte.setCreateDate(new Date());
    } else {
      docByte.setLastUpdateDate(new Date());
    }
    repository.save(docByte);
  }

  @CacheEvict(value = "doc_bytes", allEntries = true)
  public void deleteDocByte(DocByte docByte) {
    repository.delete(docByte);
  }

  @CacheEvict(value = "doc_bytes", allEntries = true)
  public void deleteDocByteByID(Integer arg0) {
    repository.deleteById(arg0);
  }

  @Cacheable("doc_bytes")
  public List<DocByte> findAllDocByte() {
    return repository.findAll();
  }

  public DocByte findById(Long parseLong) {
    return repository.findById(parseLong);
  }
}
