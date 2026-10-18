package com.tertiary.verify.data_model.model.beans;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MonthlyStatsResponse {
  private int month;
  private String title;
  private long count;

  public MonthlyStatsResponse(int month, String title, long count) {
    this.month = month;
    this.title = title;
    this.count = count;
  }
}
