package com.tertiary.verify.data_model.model.beans;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AllInstitutionDTO {
  private Long id;
  private String institutionName;
  private List<String> province;

  public AllInstitutionDTO(Long id, String institutionName) {
    this.id = id;
    this.institutionName = institutionName;
  }
}
