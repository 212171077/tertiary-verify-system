package com.tertiary.verify.data_model.model.beans;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppCourse {
  private Long id;
  private String courseName;
  private Long institutionId;
  private String courseType;
  private String courseLevel;
}
