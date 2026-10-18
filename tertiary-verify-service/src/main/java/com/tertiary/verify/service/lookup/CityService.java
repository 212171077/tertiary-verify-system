package com.tertiary.verify.service.lookup;

import static com.tertiary.verify.utils.ConstantUtil.UNABLE_TO_PROCESS_REQUEST;

import com.tertiary.verify.data_model.model.beans.MessageResponse;
import com.tertiary.verify.data_model.model.beans.lookup.CityRequest;
import com.tertiary.verify.data_model.model.entities.lookup.City;
import com.tertiary.verify.data_model.model.entities.lookup.Province;
import com.tertiary.verify.data_model.model.repositories.lookup.CityRepository;
import com.tertiary.verify.data_model.model.repositories.lookup.ProvinceRepository;
import com.tertiary.verify.framework.AbstractService;
import com.tertiary.verify.service.AddressService;
import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import javax.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class CityService extends AbstractService {
  @Autowired private CityRepository repository;

  @Autowired private AddressService addressService;
  @Autowired private ProvinceRepository provinceRepository;

  @CacheEvict(value = "cities", allEntries = true)
  public void saveCity(City city) throws Exception {
    if (city.getId() == null) {
      checkIfExist(city.getDescription());
      city.setDeleted(false);
      city.setCreateDate(new Date());
    } else {
      city.setLastUpdateDate(new Date());
    }
    repository.save(city);
  }

  @CacheEvict(value = "cities", allEntries = true)
  public void deleteCity(City city) throws Exception {
    checkIfInUse(city);
    city.setDeleted(true);
    repository.save(city);
  }

  @Cacheable("cities")
  public List<City> findAllCity() {
    return repository.findByDeleted(false);
  }

  @Cacheable("cities")
  public City findById(Long parseLong) {
    return repository.findByIdAndDeleted(parseLong, false);
  }

  @Cacheable("cities")
  public List<City> findByProvince(Province province) {
    return repository.findByProvinceAndDeleted(province, false);
  }

  @Cacheable("cities")
  public List<City> findByProvinceId(Long provinceId) {
    return repository.findByProvinceIdAndDeleted(provinceId, false);
  }

  private void checkIfExist(String desc) throws Exception {
    List<City> allCities = findAllCity();
    if (!allCities.isEmpty()) {
      City existingCity =
          allCities.stream()
              .filter(city -> city.getDescription().equalsIgnoreCase(desc))
              .findAny()
              .orElse(null);
      if (existingCity != null) {
        throw new Exception("City already exist");
      }
    }
  }

  private void checkIfInUse(City city) throws Exception {
    long count = addressService.countByCity(city);
    if (count > 0) {
      throw new Exception("This city cannot be deleted because it's being used");
    }
  }

  public City findByName(String name) throws Exception {
    List<City> list = findAllCity();
    City city = null;
    if (!list.isEmpty()) {
      city =
          list.stream()
              .filter(p -> p.getDescription().equalsIgnoreCase(name))
              .findAny()
              .orElse(null);
      if (city == null) {
        throw new Exception("City not  found [" + name + "]");
      }
    }
    return city;
  }

  public long countByProvince(Province province) {
    return repository.countByProvinceAndDeleted(province, false);
  }

  public ResponseEntity<? extends Serializable> saveCity(CityRequest request) {
    City city = findById(request.getId());
    if (city == null) {
      city = new City();
    }
    try {
      Province province = provinceRepository.findByIdAndDeleted(request.getProvinceId(), false);
      if (province == null) {
        throw new ValidationException("Invalid province ID");
      }
      city.setProvince(province);
      city.setDescription(request.getDescription());
      saveCity(city);
    } catch (Exception e) {
      return ResponseEntity.badRequest()
          .body(new MessageResponse(false, UNABLE_TO_PROCESS_REQUEST.concat(e.getMessage()), null));
    }
    return ResponseEntity.ok(new MessageResponse(true, "Application configuration saved", null));
  }

  public ResponseEntity<? extends Serializable> deleteCity(Long id) {
    City city = findById(id);
    if (city == null) {
      return ResponseEntity.badRequest().body(new MessageResponse(false, "Invalid city ID", null));
    }
    try {
      deleteCity(city);
    } catch (Exception e) {
      return ResponseEntity.badRequest()
          .body(new MessageResponse(false, UNABLE_TO_PROCESS_REQUEST.concat(e.getMessage()), null));
    }
    return ResponseEntity.ok(new MessageResponse(true, "City deleted", null));
  }

  public ResponseEntity<List<City>> findAllCities() {
    List<City> list = findAllCity();
    if (!list.isEmpty()) {
      list.sort(Comparator.comparing(City::getDescription));
    }
    return ResponseEntity.ok(list);
  }
}
