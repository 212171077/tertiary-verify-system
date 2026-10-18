package com.tertiary.verify.data_model.model.beans;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class StatisticCountResponse {
  private String title;
  private long count;

  public StatisticCountResponse(String title, long count) {
    this.title = title;
    this.count = count;
  }
}
