package com.tertiary.verify.data_model.model.beans;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccreditedInstitution {
  private Long id;
  private String name;
  private String knownAs;
  private String image;
  private String email;
  private List<String> addressList;
  private String cellNumber;
  private String accreditedNumber;
  private String accreditedStartDate;
  private String accreditedEndDate;
  private String applyLink;
  private String status;
  private String statusDescription;
  private boolean prioritize;
}
