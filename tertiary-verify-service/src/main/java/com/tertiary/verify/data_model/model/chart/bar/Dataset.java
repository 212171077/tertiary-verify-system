package com.tertiary.verify.data_model.model.chart.bar;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Dataset {
  private String label;
  private List<Integer> data;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private List<String> borderColor;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private int borderWidth;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private List<Integer> borderDash;

  private boolean fill;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private Integer pointRadius;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private Double tension;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private List<String> backgroundColor;
}
