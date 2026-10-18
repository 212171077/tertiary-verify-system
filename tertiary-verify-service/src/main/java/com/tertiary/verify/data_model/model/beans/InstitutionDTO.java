package com.tertiary.verify.data_model.model.beans;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InstitutionDTO {

  private Long id;
  private String institutionName;
  private String contactNumber;
  private String accreditationNumber;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private String province;

  private String image;
  private String applyLink;
  private boolean prioritize;
  private String status;
  private String statusDescription;
}
