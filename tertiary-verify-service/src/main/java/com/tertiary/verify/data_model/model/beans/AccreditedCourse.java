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
public class AccreditedCourse {
  private Long id;
  private String name;
  private String type;
  private String level;
  private List<AccreditedInstitution> institutionList;
}
