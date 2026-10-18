package com.tertiary.verify.service.lookup;

import static com.tertiary.verify.utils.ConstantUtil.UNABLE_TO_PROCESS_REQUEST;

import com.tertiary.verify.data_model.model.beans.MessageResponse;
import com.tertiary.verify.data_model.model.beans.lookup.ProvinceRequest;
import com.tertiary.verify.data_model.model.entities.lookup.Province;
import com.tertiary.verify.data_model.model.repositories.lookup.ProvinceRepository;
import com.tertiary.verify.framework.AbstractService;
import com.tertiary.verify.service.AddressService;
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
public class ProvinceService extends AbstractService {
  @Autowired private ProvinceRepository repository;

  @Autowired private CityService cityService;

  @Autowired private AddressService addressService;

  @CacheEvict(value = "provinces", allEntries = true)
  public void saveProvince(Province province) throws Exception {
    if (province.getId() == null) {
      checkIfExist(province.getDescription());
      province.setDeleted(false);
      province.setCreateDate(new Date());
    } else {
      province.setLastUpdateDate(new Date());
    }
    repository.save(province);
  }

  @CacheEvict(value = "provinces", allEntries = true)
  public void deleteProvince(Province province) throws Exception {
    checkIfInUse(province);
    province.setDeleted(true);
    repository.save(province);
  }

  @Cacheable("provinces")
  public List<Province> findAllProvince() {
    return repository.findByDeleted(false);
  }

  @Cacheable("provinces")
  public Province findById(Long id) {
    return repository.findByIdAndDeleted(id, false);
  }

  @Cacheable("provinces")
  public Province findByCode(String code) {
    return repository.findByCodeAndDeleted(code, false);
  }

  private void checkIfExist(String desc) throws Exception {
    List<Province> list = findAllProvince();
    if (!list.isEmpty()) {
      Province existingProvince =
          list.stream()
              .filter(province -> province.getDescription().equalsIgnoreCase(desc))
              .findAny()
              .orElse(null);
      if (existingProvince != null) {
        throw new Exception("Province already exist");
      }
    }
  }

  private void checkIfInUse(Province province) throws Exception {
    long count = cityService.countByProvince(province);
    if (count > 0) {
      throw new Exception("This province cannot be deleted because it's being used");
    } else {
      count = addressService.countByProvince(province);
      if (count > 0) {
        throw new Exception("This province cannot be deleted because it's being used");
      }
    }
  }

  public Province findByName(String provinceName) throws Exception {
    List<Province> list = findAllProvince();
    Province province = null;
    if (!list.isEmpty()) {
      province =
          list.stream()
              .filter(p -> p.getDescription().equalsIgnoreCase(provinceName))
              .findAny()
              .orElse(null);
      if (province == null) {
        throw new Exception("Province not  found [" + provinceName + "]");
      }
    }
    return province;
  }

  public ResponseEntity<? extends Serializable> saveProvince(ProvinceRequest request) {
    Province province = findById(request.getId());
    if (province == null) {
      province = new Province();
    }
    try {
      province.setDescription(request.getDescription());
      province.setCode(request.getCode());
      saveProvince(province);
    } catch (Exception e) {
      return ResponseEntity.badRequest()
          .body(new MessageResponse(false, UNABLE_TO_PROCESS_REQUEST.concat(e.getMessage()), null));
    }
    return ResponseEntity.ok(new MessageResponse(true, "Province saved", null));
  }

  public ResponseEntity<? extends Serializable> deleteProvince(Long id) {
    Province province = findById(id);
    if (province == null) {
      return ResponseEntity.badRequest()
          .body(new MessageResponse(false, "Invalid province ID", null));
    }
    try {
      deleteProvince(province);
    } catch (Exception e) {
      return ResponseEntity.badRequest()
          .body(new MessageResponse(false, UNABLE_TO_PROCESS_REQUEST.concat(e.getMessage()), null));
    }
    return ResponseEntity.ok(new MessageResponse(true, "Province deleted", null));
  }

  public ResponseEntity<List<Province>> findAllProvinces() {
    List<Province> list = findAllProvince();
    if (!list.isEmpty()) {
      list.sort(Comparator.comparing(Province::getDescription));
    }
    return ResponseEntity.ok(list);
  }
}
