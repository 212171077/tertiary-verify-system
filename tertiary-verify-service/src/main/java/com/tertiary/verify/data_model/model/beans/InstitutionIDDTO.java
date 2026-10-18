package com.tertiary.verify.data_model.model.beans;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InstitutionIDDTO {
  private Long id;
  private String errorMessage;
}
