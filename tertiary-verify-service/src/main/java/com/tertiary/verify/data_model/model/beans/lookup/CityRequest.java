package com.tertiary.verify.data_model.model.beans.lookup;

import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CityRequest {
  private Long id;
  @NotNull private Long provinceId;
  @NotNull private String description;
}
