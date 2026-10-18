package com.tertiary.verify.data_model.model.beans;

import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RejectInstitutionRequest {
  @NotNull private Long userId;
  @NotNull private Long institutionId;
  @NotNull private List<Long> rejectReasonIdList;
}
