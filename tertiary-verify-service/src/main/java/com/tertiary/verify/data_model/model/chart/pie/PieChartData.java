package com.tertiary.verify.data_model.model.chart.pie;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PieChartData {
  private List<String> labels;
  private List<Dataset> datasets;
}
