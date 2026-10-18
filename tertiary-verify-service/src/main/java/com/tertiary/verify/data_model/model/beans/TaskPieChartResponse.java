package com.tertiary.verify.data_model.model.beans;

import com.tertiary.verify.data_model.model.chart.pie.ChartOptions;
import com.tertiary.verify.data_model.model.chart.pie.PieChartData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskPieChartResponse {
  private String displayMessage;
  private PieChartData pieChartData;
  private ChartOptions chartOptions;
}
