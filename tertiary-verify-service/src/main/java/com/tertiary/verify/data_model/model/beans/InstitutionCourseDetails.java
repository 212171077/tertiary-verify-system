package com.tertiary.verify.data_model.model.beans;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InstitutionCourseDetails {
  private Long id;
  private String description;
  private String courseType;
  private String courseLevel;
  private List<InstitutionCourseAddress> addressList;
}
