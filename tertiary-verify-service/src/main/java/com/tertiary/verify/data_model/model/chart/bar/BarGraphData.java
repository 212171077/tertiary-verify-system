package com.tertiary.verify.data_model.model.chart.bar;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BarGraphData {
  private List<String> labels;
  private List<Dataset> datasets;
}
