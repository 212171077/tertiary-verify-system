package com.tertiary.verify.data_model.model.beans.lookup;

import java.util.Date;
import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventResponse {

  private Long id;

  private String code;

  private String title;

  private String subTitle;

  private String imageUrl;

  private String description;

  private Date createDate;

  private Date dateAndTime;

  private Date lastUpdateDate;

  private Boolean active;

  private String location;
}
