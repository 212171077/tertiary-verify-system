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
public class InstitutionDetails {
  private Long id;
  private String uuid;
  private String createDate;
  private String lastUpdateDate;
  private Long creatorUserID;
  private Long supervisorID;
  private List<DropDown> institutionTypeList;
  private String institutionName;
  private String knownAs;
  private String contactNumber;
  private String alternativeContactNumber;
  private String email;
  private Date accreditationStartDate;
  private Date accreditationEndDate;
  private String accreditationNumber;
  private String status;
  private String qrCode;
  private String image;
  private List<InstitutionAddress> addressList;
  private List<InstitutionCourse> courseList;
  private String applyLink;
  private boolean prioritize;
}
