package com.tertiary.verify.data_model.model.beans;

import java.io.Serializable;
import java.util.*;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDetails implements Serializable {

  @NotBlank private Long id;

  private String name;

  private String surname;

  private String email;

  private List<Role> roles = new ArrayList<>();

  private String rsaId;

  private String dateOfBirth;

  private String cellNumber;

  private Date createDate;

  private Date lastLoginDate;

  private String status;

  private List<DropDown> titles;

  private List<DropDown> genders;

  /** Residential Address */
  private String residentialAddressLine1;

  private String residentialAddressLine2;

  private String residentialAddressLine3;

  private String residentialAddressLine4;

  private List<DropDown> residentialAddressCities;

  private String residentialAddressSuburb;

  private String residentialCode;

  private List<DropDown> residentialAddressProvinces;

  /** Postal Address */
  private String postalAddressLine1;

  private String postalAddressLine2;

  private String postalAddressLine3;

  private String postalAddressLine4;

  private List<DropDown> postalAddressCities;

  private String postalAddressSuburb;

  private String postalAddressCode;

  private List<DropDown> postalAddressProvinces;

  private String imageUrl;

  private List<Menu> menus;
}
