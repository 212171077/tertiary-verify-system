package com.tertiary.verify.data_model.model.chart.pie;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Dataset {
  private List<Integer> data;
  private List<String> backgroundColor;
  private List<String> hoverBackgroundColor;
}
