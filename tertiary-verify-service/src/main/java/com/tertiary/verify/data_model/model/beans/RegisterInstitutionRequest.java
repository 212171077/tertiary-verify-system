package com.tertiary.verify.data_model.model.beans;

import java.util.Date;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterInstitutionRequest {
  private Long id;
  @NotNull private Long creatorUserID;
  @NotNull private String institutionName;
  private String knownAs;
  @NotNull private String contactNumber;
  @NotNull private String alternativeContactNumber;
  @NotNull private String email;
  @NotNull private String accreditationNumber;
  private String image;
  @NotNull private String institutionType;
  private String applyLink;
  private Date accreditationStartDate;
  private Date accreditationEndDate;
}
