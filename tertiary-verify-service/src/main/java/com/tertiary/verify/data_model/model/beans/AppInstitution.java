package com.tertiary.verify.data_model.model.beans;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppInstitution {

  private Long id;
  private String intitutionType;
  private String name;
  private String knownAs;
  private String contactNumber;
  private String alternativeContactNumber;
  private String email;
  private String startDate;
  private String endDate;
  private String province;
  private String addessLine1;
  private String addessLine2;
  private String addessLine3;
  private String addessLine4;
  private String accreditationNumber;
  private String courseDesc;
  private String courseType;
  private String courseLevel;
}
