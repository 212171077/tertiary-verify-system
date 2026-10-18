package com.tertiary.verify.data_model.model.beans;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InstitituionDetailsDTO {
  private String name;
  private String knownAs;
  private String contactNumber;
  private String alternativeContactNumber;
  private String email;
  private String accreditationNumber;
  private String addressLine1;
  private String addressLine2;
  private String province;
  private String errorMessage;
  private String startDate;
  private String endDate;
}
