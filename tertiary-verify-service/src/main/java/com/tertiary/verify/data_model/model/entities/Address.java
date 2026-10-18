package com.tertiary.verify.data_model.model.entities;

import com.tertiary.verify.data_model.model.entities.common.IDataEntity;
import com.tertiary.verify.data_model.model.entities.lookup.City;
import com.tertiary.verify.data_model.model.entities.lookup.Province;
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
@Table(name = "address")
public class Address implements IDataEntity {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", unique = true, nullable = false)
  private Long id;

  @Column(name = "address_line_1")
  private String addressLine1;

  @Column(name = "address_line_2")
  private String addressLine2;

  @Column(name = "address_line_3")
  private String addressLine3;

  @Column(name = "address_line_4")
  private String addressLine4;

  @Fetch(FetchMode.JOIN)
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "city_id", referencedColumnName = "id")
  private City city;

  @Column(name = "suburb")
  private String suburb;

  @Column(name = "code")
  private String code;

  private Double latitude;
  private Double longitude;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "create_date", length = 19)
  private Date createDate;

  @Fetch(FetchMode.JOIN)
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "province_id", referencedColumnName = "id")
  private Province province;

  @Column(name = "last_update_date", length = 19)
  private Date lastUpdateDate;

  @Fetch(FetchMode.JOIN)
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "institution_id", referencedColumnName = "id")
  private Institution institution;

  @Column(name = "examination_number")
  private String examinationNumber;
}
