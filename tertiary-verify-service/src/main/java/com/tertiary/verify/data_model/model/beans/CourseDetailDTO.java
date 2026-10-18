package com.tertiary.verify.data_model.model.beans;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseDetailDTO {

  private String description;
  private String courseType;
  private String courseLevel;
  private String errorMessage;

  public CourseDetailDTO(String description, String courseType, String courseLevel) {
    super();
    this.description = description;
    this.courseType = courseType;
    this.courseLevel = courseLevel;
  }
}
