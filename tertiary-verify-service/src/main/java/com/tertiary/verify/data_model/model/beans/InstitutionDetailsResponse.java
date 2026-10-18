package com.tertiary.verify.data_model.model.beans;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InstitutionDetailsResponse {
  private Long id;
  private String createDate;
  private String lastUpdateDate;
  private Long creatorUserID;
  private Long supervisorID;
  private String institutionName;
  private String knownAs;
  private String contactNumber;
  private String alternativeContactNumber;
  private String email;
  private String accreditationStartDate;
  private String accreditationEndDate;
  private String accreditationNumber;
  private String qrCode;
  private String image;
  private String type;
  private String status;
  private String uuid;
  private String applyLink;
  private boolean prioritize;
}
