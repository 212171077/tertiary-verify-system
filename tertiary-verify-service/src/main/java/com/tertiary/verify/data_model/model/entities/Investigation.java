package com.tertiary.verify.data_model.model.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tertiary.verify.data_model.model.entities.common.IDataEntity;
import java.util.Date;
import javax.persistence.*;
import lombok.*;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "investigation")
public class Investigation implements IDataEntity {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", unique = true, nullable = false)
  private Long id;

  @Column(name = "description", length = 5000)
  private String description;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "create_date", length = 19)
  private Date createDate;

  @Column(name = "last_update_date", length = 19)
  private Date lastUpdateDate;

  @Fetch(FetchMode.JOIN)
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "reported_institution_id", referencedColumnName = "id")
  private ReportedInstitutions reportedInstitution;

  @JsonIgnore
  @Column(name = "deleted", nullable = false, columnDefinition = ("boolean default false"))
  private Boolean deleted;
}
