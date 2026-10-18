package com.tertiary.verify.data_model.model.beans;

import java.util.Date;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportedInstitutionDetails {
  private Long id;
  private Date createDate;
  private Date lastUpdateDate;
  private String reportType;
  private String institutionName;
  private String knownAs;
  private String additionalInfo;
  private String refNumber;
  private List<InvestigationResponse> investigationList;
  private String status;
  private String investigatorName;
  private String investigatorSurname;
  private String investigatorEmail;
  private String investigatorCell;
  private String reporterName;
  private String reporterSurname;
  private String reporterEmail;
  private String reporterCell;
  private String addressLine1;
  private String addressLine2;
  private String addressLine3;
  private String addressLine4;
  private String code;
  private String province;
  private String city;
  private List<CourseBean> reportedCourseList;
}
