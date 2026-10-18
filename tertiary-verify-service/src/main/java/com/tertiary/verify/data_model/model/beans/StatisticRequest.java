package com.tertiary.verify.data_model.model.beans;

import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatisticRequest {
  @NotNull private String echoRef;
  @NotNull private String title;
  @NotNull private String description;
  @NotNull private String channel;
}
