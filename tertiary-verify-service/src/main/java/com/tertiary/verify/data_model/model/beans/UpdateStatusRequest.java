package com.tertiary.verify.data_model.model.beans;

import com.tertiary.verify.data_model.model.entities.enums.ApprovalStatusEnum;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateStatusRequest {
  @NotNull private Long institutionId;
  @NotNull private ApprovalStatusEnum status;
}
