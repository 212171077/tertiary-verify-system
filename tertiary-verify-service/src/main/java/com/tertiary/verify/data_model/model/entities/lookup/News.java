package com.tertiary.verify.data_model.model.entities.lookup;

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
@Table(name = "news", indexes = @Index(name = "news_active_idx", columnList = "active"))
public class News implements IDataEntity {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", unique = true, nullable = false)
  private Long id;

  @Column(name = "code", length = 10)
  private String code;

  @Column(name = "title", length = 500)
  private String title;

  @Column(name = "sub_title", length = 500)
  private String subTitle;

  @Column(name = "image_url", length = 500)
  private String imageUrl;

  @Lob
  @Column(name = "description")
  private String description;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "create_date", length = 19)
  private Date createDate;

  @Column(name = "last_update_date", length = 19)
  private Date lastUpdateDate;

  @Column(name = "active", columnDefinition = ("boolean default true"))
  private Boolean active;

  @JsonIgnore
  @Column(name = "deleted", nullable = false, columnDefinition = ("boolean default false"))
  private boolean deleted;
}
