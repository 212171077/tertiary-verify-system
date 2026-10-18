package com.tertiary.verify.data_model.model.beans;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppReportedInstitution {
  private Long id;
  private String firstname;
  private String lastname;
  private String cellNumber;
  private String email;
  private boolean reportType;
  private String institutionName;
  private String knownAs;
  private String addressLineOne;
  private String addressLineTwo;
  private String addressLineThree;
  private String province;
  private String surbub;
  private String code;
  private List<String> courseName;
}
