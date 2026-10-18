package com.tertiary.verify.data_model.model.entities;

import com.tertiary.verify.data_model.model.entities.common.IDataEntity;
import com.tertiary.verify.data_model.model.entities.enums.ReportTypeEnum;
import com.tertiary.verify.data_model.model.entities.enums.ReportedInstitutionsStatus;
import java.util.Date;
import java.util.List;
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
@Table(
    name = "reported_institutions",
    indexes = {
      @Index(name = "rep_inst_name_idx", columnList = "institution_name"),
      @Index(name = "rep_inst_known_as_idx", columnList = "known_as"),
      @Index(name = "rep_inst_ref_num_idx", columnList = "ref_number")
    })
public class ReportedInstitutions implements IDataEntity {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", unique = true, nullable = false)
  private Long id;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "create_date", length = 19)
  private Date createDate;

  @Column(name = "last_update_date", length = 19)
  private Date lastUpdateDate;

  @Enumerated
  @Column(name = "report_type")
  private ReportTypeEnum reportType;

  @Column(name = "institution_name")
  private String institutionName;

  @Column(name = "known_as")
  private String knownAs;

  @Column(name = "additional_info")
  private String additionalInfo;

  @Fetch(FetchMode.JOIN)
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "address", referencedColumnName = "id")
  private Address address;

  @Column(name = "ref_number")
  private String refNumber;

  @Enumerated
  @Column(name = "status")
  private ReportedInstitutionsStatus status;

  @Column(name = "deleted", nullable = false, columnDefinition = ("boolean default false"))
  private Boolean deleted;

  /** Report Information */
  @Column(name = "reporter_name")
  private String reporterName;

  @Column(name = "reporter_surname")
  private String reporterSurname;

  @Column(name = "reporter_email")
  private String reporterEmail;

  @Column(name = "reporter_cell")
  private String reporterCell;

  @Fetch(FetchMode.JOIN)
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "investigator_id", referencedColumnName = "id")
  private User investigator;

  @Fetch(FetchMode.JOIN)
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "institution_id", referencedColumnName = "id")
  private Institution institution;

  @OneToMany(
      mappedBy = "reportedInstitution",
      cascade = CascadeType.REMOVE,
      fetch = FetchType.EAGER)
  private List<Investigation> InvestigationList;
}
