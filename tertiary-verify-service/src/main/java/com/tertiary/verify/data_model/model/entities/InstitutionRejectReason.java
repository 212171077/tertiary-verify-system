package com.tertiary.verify.data_model.model.entities;

import com.tertiary.verify.data_model.model.entities.common.IDataEntity;
import com.tertiary.verify.data_model.model.entities.lookup.RejectReason;
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
    name = "institution_reject_reason",
    indexes = {@Index(name = "inst_id_idx", columnList = "institution_id")})
public class InstitutionRejectReason implements IDataEntity {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", unique = true, nullable = false)
  private Long id;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "create_date", length = 19)
  private Date createDate;

  @Fetch(FetchMode.JOIN)
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "institution_id", referencedColumnName = "id")
  private Institution institution;

  @Fetch(FetchMode.JOIN)
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "reject_reason_id", referencedColumnName = "id")
  private RejectReason rejectReason;
}
