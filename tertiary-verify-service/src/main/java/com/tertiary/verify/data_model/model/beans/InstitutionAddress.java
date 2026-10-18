package com.tertiary.verify.data_model.model.beans;

import java.util.Date;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InstitutionAddress {
  private Long id;
  private String addressLine1;
  private String addressLine2;
  private String addressLine3;
  private String addressLine4;
  private String suburb;
  private String code;
  private Double latitude;
  private Double longitude;
  private Date createDate;
  private Date lastUpdateDate;
  private String examinationNumber;
  private List<DropDown> provinceList;
  private List<DropDown> cityList;
}
