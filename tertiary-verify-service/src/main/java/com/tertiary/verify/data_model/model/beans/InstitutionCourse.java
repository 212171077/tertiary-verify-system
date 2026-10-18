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
public class InstitutionCourse {
  private Long id;
  private String description;
  private List<DropDown> courseTypeList;
  private List<DropDown> courseLevelList;
  private List<InstitutionAddress> addressList;
}
