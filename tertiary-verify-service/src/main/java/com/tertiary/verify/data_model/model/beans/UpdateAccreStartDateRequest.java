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
public class UpdateAccreStartDateRequest {
  @NotNull private Long institutionId;
  @NotNull private Date startDate;
}
