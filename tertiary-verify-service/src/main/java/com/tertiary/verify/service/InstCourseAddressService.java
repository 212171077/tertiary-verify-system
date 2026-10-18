package com.tertiary.verify.service;

import com.tertiary.verify.data_model.model.entities.Address;
import com.tertiary.verify.data_model.model.entities.InstCourseAddress;
import com.tertiary.verify.data_model.model.entities.InstitutionCourse;
import com.tertiary.verify.data_model.model.repositories.InstCourseAddressRepository;
import com.tertiary.verify.framework.AbstractService;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class InstCourseAddressService extends AbstractService {
  @Autowired InstCourseAddressRepository repository;

  @CacheEvict(value = "inst_course_addresses", allEntries = true)
  public void saveInstCourseAddress(InstCourseAddress instCourseAddress) {
    if (instCourseAddress.getId() == null) {
      instCourseAddress.setCreateDate(new Date());
    } else {
      instCourseAddress.setLastUpdateDate(new Date());
    }
    repository.save(instCourseAddress);
  }

  @CacheEvict(value = "inst_course_addresses", allEntries = true)
  public void deleteInstCourseAddress(InstCourseAddress instCourseAddress) {
    repository.delete(instCourseAddress);
  }

  @CacheEvict(value = "inst_course_addresses", allEntries = true)
  public void deleteInstCourseAddressByID(Integer arg0) {
    repository.deleteById(arg0);
  }

  @Cacheable("inst_course_addresses")
  public List<InstCourseAddress> findAllInstCourseAddress() {
    return repository.findAll();
  }

  public InstCourseAddress findById(Long parseLong) {
    return repository.findById(parseLong);
  }

  public List<InstCourseAddress> findByInstitutionId(Long parseLong) {
    return repository.findByInstitutionId(parseLong);
  }

  @CacheEvict(value = "inst_course_addresses", allEntries = true)
  public void saveAll(ArrayList<InstCourseAddress> instCourseAddressList) {
    repository.saveAll(instCourseAddressList);
  }

  public List<InstCourseAddress> findByIds(List<String> idList) {
    return repository.findByIds(idList);
  }

  public List<InstCourseAddress> findByAddress(Address address) {
    return repository.findByAddress(address);
  }

  public List<InstCourseAddress> findByInstitutionCourse(InstitutionCourse instCourse) {
    return repository.findByInstitutionCourse(instCourse);
  }
}
