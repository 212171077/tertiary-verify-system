package com.tertiary.verify.data_model.model.beans;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BogusCourseDetails {
  private BogusCourses bogusCourses;
  private List<CourseDTO> courseList;
}
