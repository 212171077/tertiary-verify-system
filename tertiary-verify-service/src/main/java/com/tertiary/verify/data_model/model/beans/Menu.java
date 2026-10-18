package com.tertiary.verify.data_model.model.beans;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Menu {
  private String label;
  private String icon;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private List<String> routerLink;

  private List<MenuItem> items;
}
