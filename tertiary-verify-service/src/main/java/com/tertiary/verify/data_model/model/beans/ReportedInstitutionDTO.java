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
public class ReportedInstitutionDTO {
  private boolean success;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private String institutionName;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private String reportType;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private String reportedDate;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private String refNumber;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private String investigatorFeedback;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private String status;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private String reporterFullName;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private String lastUpdatedDate;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private String errorMessage;
}
