package com.tertiary.verify.service;

import com.tertiary.verify.data_model.model.beans.InstitutionAddressRequest;
import com.tertiary.verify.data_model.model.beans.MessageResponse;
import com.tertiary.verify.data_model.model.entities.Address;
import com.tertiary.verify.data_model.model.entities.InstCourseAddress;
import com.tertiary.verify.data_model.model.entities.Institution;
import com.tertiary.verify.data_model.model.entities.enums.ApprovalStatusEnum;
import com.tertiary.verify.data_model.model.entities.lookup.City;
import com.tertiary.verify.data_model.model.entities.lookup.Province;
import com.tertiary.verify.data_model.model.repositories.AddressRepository;
import com.tertiary.verify.framework.AbstractService;
import com.tertiary.verify.service.lookup.CityService;
import com.tertiary.verify.service.lookup.ProvinceService;
import java.util.*;
import javax.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class AddressService extends AbstractService {
  @Autowired AddressRepository repository;
  @Autowired InstitutionService institutionService;
  @Autowired CityService cityService;

  @Autowired ProvinceService provinceService;

  @Autowired InstCourseAddressService instCourseAddressService;

  @CacheEvict(value = "addresses", allEntries = true)
  public void saveAddress(Address address) {
    if (address.getId() == null) {
      address.setCreateDate(new Date());
    } else {
      address.setLastUpdateDate(new Date());
    }
    repository.save(address);
  }

  @CacheEvict(value = "addresses", allEntries = true)
  public ResponseEntity<MessageResponse> saveAddress(InstitutionAddressRequest request) {
    try {
      Optional<Institution> institution =
          Optional.ofNullable(institutionService.findById(request.getInstitutionId()));
      if (institution.isPresent()) {
        Address address = Optional.ofNullable(findById(request.getId())).orElse(new Address());
        address.setAddressLine1(request.getAddressLine1());
        address.setAddressLine2(request.getAddressLine2());
        address.setAddressLine3(request.getAddressLine3());
        address.setAddressLine4(request.getAddressLine4());
        address.setCity(cityService.findByName(request.getCity()));
        address.setProvince(provinceService.findByName(request.getProvince()));
        address.setCode(request.getCode());
        Institution theInstitution = institution.get();
        address.setInstitution(theInstitution);
        address.setExaminationNumber(request.getExaminationNumber());
        saveAddress(address);
        Map<String, Long> idaData = new HashMap<>();
        idaData.put("addressId", address.getId());
        if (theInstitution.getStatus() == null
            || theInstitution.getStatus() != ApprovalStatusEnum.RegistrationInProgress) {
          theInstitution.setStatus(ApprovalStatusEnum.UpdateInProgress);
        }
        institutionService.saveInstitution(theInstitution);
        return ResponseEntity.ok()
            .body(new MessageResponse(true, "Institution address updated", idaData));
      } else {
        return ResponseEntity.badRequest()
            .body(new MessageResponse(false, "Invalid institution ID", null));
      }
    } catch (Exception e) {
      return ResponseEntity.badRequest().body(new MessageResponse(false, e.getMessage(), null));
    }
  }

  @CacheEvict(value = "addresses", allEntries = true)
  public void deleteAddress(Address address) {
    repository.delete(address);
  }

  @CacheEvict(value = "addresses", allEntries = true)
  public ResponseEntity<?> deleteAddressByID(Long id) {
    Address address = repository.findById(id);

    if (address != null) {
      List<InstCourseAddress> courseAddressList = instCourseAddressService.findByAddress(address);
      if (courseAddressList != null && !courseAddressList.isEmpty()) {
        return ResponseEntity.unprocessableEntity()
            .body(new MessageResponse(false, "Address not found", null));
      } else {
        deleteAddress(address);
        return ResponseEntity.ok().body(new MessageResponse(true, "Address deleted", null));
      }
    } else {
      return ResponseEntity.badRequest()
          .body(new MessageResponse(false, "Address not found", null));
    }
  }

  public List<Address> findByIds(@NotNull List<Long> idList) {
    return repository.findByIds(idList);
  }

  public Address findById(Long parseLong) {
    if (parseLong != null) {
      return repository.findById(parseLong);
    } else {
      return null;
    }
  }

  public long countByCity(City city) {
    return repository.countByCity(city);
  }

  public long countByProvince(Province province) {
    return repository.countByProvince(province);
  }
}
