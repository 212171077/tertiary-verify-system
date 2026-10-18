package com.tertiary.verify.data_model.model.beans;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenuItem {
  private String label;
  private String icon;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private String target;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  @JsonProperty("class")
  private String theClass;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private List<String> url;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private List<String> routerLink;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private List<MenuItem> items;
}
