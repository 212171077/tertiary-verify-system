package com.tertiary.verify.data_model.model.beans;

import com.tertiary.verify.data_model.model.entities.enums.ReportedInstitutionsStatus;
import java.io.Serializable;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InvestigationRequest implements Serializable {
  @NotNull private String feedbackDetails;
  @NotNull private Long reportedInstitutionId;
  @NotNull private ReportedInstitutionsStatus status;
}
