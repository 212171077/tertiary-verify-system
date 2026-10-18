package com.tertiary.verify.data_model.model.beans;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DashboardCardResponse {
  private long reportedInstitutions;
  private long underInvestigationInstitutions;
  private long accreditedCourses;
  private long approvedInstitutions;
}
