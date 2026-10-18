package com.tertiary.verify.data_model.model.beans;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StatisticsReport implements Serializable {

  private static final long serialVersionUID = 1L;
  private Long totalCourse;
  private Long totalInstitution;
  private Long totalInstitutionReported;
  private Long totalInstitutionUndetInvestigation;
  private String errorMessage;

  public StatisticsReport(
      Long totalCourse,
      Long totalInstitution,
      Long totalInstitutionReported,
      Long totalInstitutionUndetInvestigation) {
    super();
    this.totalCourse = totalCourse;
    this.totalInstitution = totalInstitution;
    this.totalInstitutionReported = totalInstitutionReported;
    this.totalInstitutionUndetInvestigation = totalInstitutionUndetInvestigation;
  }
}
