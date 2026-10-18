package com.tertiary.verify.data_model.model.beans.lookup;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventRequest {

  private Long id;
  @NotNull private String code;
  @NotNull private String title;
  @NotNull private String subTitle;
  @NotNull private String imageUrl;
  @NotNull private String description;
  @NotNull private String dateAndTime;
  @NotNull private Boolean active;
  @NotNull private String location;
}
