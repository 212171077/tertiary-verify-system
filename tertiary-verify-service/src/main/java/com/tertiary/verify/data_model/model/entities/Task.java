package com.tertiary.verify.data_model.model.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tertiary.verify.data_model.model.entities.common.IDataEntity;
import java.util.Date;
import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(
    name = "task",
    indexes = {
      @Index(name = "user_deleted_idx", columnList = "user_id, deleted"),
      @Index(name = "user_deleted_completed_idx", columnList = "user_id, deleted, completed")
    })
public class Task implements IDataEntity {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", unique = true, nullable = false)
  private Long id;

  @Column(name = "description", length = 100)
  private String description;

  @Column(name = "objective", length = 100)
  private String objective;

  @Column(name = "url", length = 100)
  private String url;

  @Fetch(FetchMode.JOIN)
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", referencedColumnName = "id")
  private User user;

  @JsonIgnore
  @Column(name = "completed", nullable = false, columnDefinition = ("boolean default false"))
  private boolean completed;

  @Column(name = "image", length = 200)
  private String image;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "create_date", length = 19)
  private Date createDate;

  @Column(name = "last_update_date", length = 19)
  private Date lastUpdateDate;

  @Column(name = "deadline_date", length = 19)
  private Date deadline;

  @JsonIgnore
  @Column(name = "class_name", length = 200)
  private String className;

  @JsonIgnore
  @Column(name = "class_id", unique = true, nullable = false)
  private Long classId;

  @JsonIgnore
  @Column(name = "deleted", nullable = false, columnDefinition = ("boolean default false"))
  private Boolean deleted;

  @Fetch(FetchMode.JOIN)
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "created_by_user_id", referencedColumnName = "id")
  private User createdByUser;
}
