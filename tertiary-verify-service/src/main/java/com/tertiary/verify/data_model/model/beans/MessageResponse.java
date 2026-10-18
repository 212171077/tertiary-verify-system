package com.tertiary.verify.data_model.model.beans;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageResponse implements Serializable {
  private boolean success;
  private String message;
  private Object data;
}
