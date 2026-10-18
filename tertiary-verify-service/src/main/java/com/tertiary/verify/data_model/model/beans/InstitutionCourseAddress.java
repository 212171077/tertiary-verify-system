package com.tertiary.verify.data_model.model.beans;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InstitutionCourseAddress {
  private Long id;
  private String addressLine1;
  private String addressLine2;
  private String addressLine3;
  private String addressLine4;
  private String code;
  private String examinationNumber;
  private String province;
  private String city;
}
