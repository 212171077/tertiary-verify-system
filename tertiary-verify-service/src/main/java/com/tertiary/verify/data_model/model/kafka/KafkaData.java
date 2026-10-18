package com.tertiary.verify.data_model.model.kafka;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KafkaData<T> {
  private String traceId;
  private EventType eventType;
  private Date createDate;
  private T data;
}
