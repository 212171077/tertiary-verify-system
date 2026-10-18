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
@Table(
    name = "gender",
    indexes = {
      @Index(name = "gender_code_idx", columnList = "code"),
      @Index(name = "gender_g_name_idx", columnList = "gender_name")
    })
public class Gender implements IDataEntity {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", unique = true, nullable = false)
  private Long id;

  @Column(name = "gender_name", length = 10)
  private String genderName;

  @Column(name = "code", length = 10)
  private String code;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "create_date", length = 19)
  private Date createDate;

  @Column(name = "last_update_date", length = 19)
  private Date lastUpdateDate;

  @JsonIgnore
  @Column(name = "deleted", nullable = false, columnDefinition = ("boolean default false"))
  private Boolean deleted;
}
