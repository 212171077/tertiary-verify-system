package com.tertiary.verify.data_model.model.beans;

import java.io.Serializable;
import lombok.*;

@Data
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InvestigationResponse implements Serializable {

  private String feedbackDetails;
  private String createDate;
}
