package com.tertiary.verify.data_model.model.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tertiary.verify.data_model.model.entities.common.IDataEntity;
import java.util.Date;
import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(
    name = "statistic",
    indexes = {
      @Index(name = "create_date_idx", columnList = "create_date"),
      @Index(name = "description_idx", columnList = "description"),
      @Index(name = "title_idx", columnList = "title")
    })
public class Statistic implements IDataEntity {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", unique = true, nullable = false)
  private Long id;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "create_date", length = 19)
  private Date createDate;

  @Column(name = "echo_ref")
  private String echoRef;

  @Column(name = "title")
  private String title;

  @Column(name = "description")
  private String description;

  @Column(name = "channel")
  private String channel;

  @JsonIgnore
  @Column(name = "deleted", nullable = false, columnDefinition = ("boolean default false"))
  private Boolean deleted;
}
