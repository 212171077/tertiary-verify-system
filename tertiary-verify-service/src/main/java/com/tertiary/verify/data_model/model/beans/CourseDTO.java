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
public class CourseDTO {
  private Long id;
  private String description;
  private String courseType;
  private String courseLevel;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private String institution;
}
