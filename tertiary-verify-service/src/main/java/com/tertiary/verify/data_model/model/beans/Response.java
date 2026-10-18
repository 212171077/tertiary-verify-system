package com.tertiary.verify.data_model.model.beans;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Response implements Serializable {
  private static final long serialVersionUID = 1L;
  private Long id;
  private String message;
  private Boolean success;
}
