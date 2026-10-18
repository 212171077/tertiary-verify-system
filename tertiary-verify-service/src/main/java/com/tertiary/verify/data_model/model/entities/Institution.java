package com.tertiary.verify.data_model.model.entities;

import com.tertiary.verify.data_model.model.entities.common.IDataEntity;
import com.tertiary.verify.data_model.model.entities.enums.ApprovalStatusEnum;
import com.tertiary.verify.data_model.model.entities.lookup.InstitutionType;
import java.util.Date;
import java.util.List;
import java.util.UUID;
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
    name = "institution",
    indexes = {
      @Index(name = "inst_name_idx", columnList = "institution_name"),
      @Index(name = "inst_known_as_idx", columnList = "known_as"),
      @Index(name = "inst_accre_num_idx", columnList = "accreditation_number"),
      @Index(name = "supervisor_deleted_idx", columnList = "supervisor_user_id, deleted")
    })
public class Institution implements IDataEntity {

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

  @Fetch(FetchMode.JOIN)
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "creator_user_id", referencedColumnName = "id")
  private User creatorUser;

  @Fetch(FetchMode.JOIN)
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "supervisor_user_id", referencedColumnName = "id")
  private User supervisor;

  @Fetch(FetchMode.JOIN)
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "approved_by_user_id", referencedColumnName = "id")
  private User approvedBy;

  @Fetch(FetchMode.JOIN)
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "institutionType_id", referencedColumnName = "id")
  private InstitutionType institutionType;

  @Column(name = "institution_name")
  private String institutionName;

  @Column(name = "known_as")
  private String knownAs;

  @Column(name = "contact_number")
  private String contactNumber;

  @Column(name = "alternative_contact_number")
  private String alternativeContactNumber;

  @Column(name = "email")
  private String email;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "accreditation_start_date", length = 19)
  private Date accreditationStartDate;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "accreditation_end_date", length = 19)
  private Date accreditationEndDate;

  @Column(name = "accreditation_number")
  private String accreditationNumber;

  @OneToMany(mappedBy = "institution", cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
  private List<Address> addressList;

  @Enumerated
  @Column(name = "status")
  private ApprovalStatusEnum status;

  @Column(name = "qr_code")
  private String qrCode;

  @Column(name = "status_updated_by_scheduler")
  private Boolean statusUpdatedByScheduler;

  @Column(name = "image")
  private String image;

  @Column(name = "deleted", nullable = false, columnDefinition = ("boolean default false"))
  private Boolean deleted;

  @Column(name = "uuid", length = 50)
  private String uuid = UUID.randomUUID().toString();

  @Column(name = "apply_link", length = 250)
  private String applyLink;

  @Column(name = "prioritize", nullable = false, columnDefinition = ("boolean default false"))
  private boolean prioritize;

  @Fetch(FetchMode.JOIN)
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "prioritized_by_user_id", referencedColumnName = "id")
  private User prioritizedBy;
}
