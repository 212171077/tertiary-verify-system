package com.tertiary.verify.data_model.model.beans;

import java.util.Date;
import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TaskResponse {
  private Long id;
  private String description;
  private String objective;
  private String url;
  private Long userId;
  private String image;
  private Date createDate;
  private Date lastUpdateDate;
  private Date deadline;
}
