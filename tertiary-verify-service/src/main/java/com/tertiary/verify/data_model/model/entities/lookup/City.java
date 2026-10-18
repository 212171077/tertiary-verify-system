package com.tertiary.verify.data_model.model.entities.lookup;

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
@Table(name = "city", indexes = @Index(name = "city_desc_idx", columnList = "description"))
public class City implements IDataEntity {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", unique = true, nullable = false)
  private Long id;

  @Column(name = "description", length = 100)
  private String description;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "create_date", length = 19)
  private Date createDate;

  @Fetch(FetchMode.JOIN)
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "province_id", referencedColumnName = "id")
  private Province province;

  @Column(name = "last_update_date", length = 19)
  private Date lastUpdateDate;

  @JsonIgnore
  @Column(name = "deleted", nullable = false, columnDefinition = ("boolean default false"))
  private Boolean deleted;
}
