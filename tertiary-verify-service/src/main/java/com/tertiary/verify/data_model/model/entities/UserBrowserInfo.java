package com.tertiary.verify.data_model.model.entities;

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
@Table(name = "user_browser_info")
public class UserBrowserInfo implements IDataEntity {
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

  @Column(name = "user_agent", columnDefinition = "LONGTEXT")
  private String userAgent;

  @Column(name = "operating_system", columnDefinition = "LONGTEXT")
  private String operatingSystem;

  @Column(name = "browser_name", columnDefinition = "LONGTEXT")
  private String browserName;

  @Column(name = "ip_address", columnDefinition = "LONGTEXT")
  private String ipAddress;

  @Column(name = "full_url", columnDefinition = "LONGTEXT")
  private String fullURL;

  @Column(name = "referer", columnDefinition = "LONGTEXT")
  private String referer;

  @Column(name = "calling_class", columnDefinition = "LONGTEXT")
  private String callingClass;

  @Column(name = "calling_method", columnDefinition = "LONGTEXT")
  private String callingMethod;

  @Column(name = "latitude")
  private Double latitude;

  @Column(name = "longitude")
  private Double longitude;

  @Column(name = "ip_address_decoded", columnDefinition = "LONGTEXT")
  private String ipAddressDecoded;

  @Fetch(FetchMode.JOIN)
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", referencedColumnName = "id")
  private User user;
}
