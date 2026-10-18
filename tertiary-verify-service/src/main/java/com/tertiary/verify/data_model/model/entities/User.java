package com.tertiary.verify.data_model.model.entities;

import com.tertiary.verify.data_model.model.entities.common.IDataEntity;
import com.tertiary.verify.data_model.model.entities.enums.UsersStatusEnum;
import com.tertiary.verify.data_model.model.entities.lookup.Gender;
import com.tertiary.verify.data_model.model.entities.lookup.Title;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
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
    name = "users",
    indexes = {
      @Index(name = "users_email_idx", columnList = "email"),
      @Index(name = "users_username_idx", columnList = "username")
    })
public class User implements IDataEntity {
  private static final long serialVersionUID = 1L;

  @Id
  @Column(name = "id")
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "create_date", length = 19)
  private Date createDate;

  @Column(name = "last_update_date", length = 19)
  private Date lastUpdateDate;

  @Column(name = "last_login_date", length = 19)
  private Date lastLoginDate;

  @Column(name = "rsa_id", length = 20)
  private String rsaId;

  @Column(name = "date_of_birth")
  private Date dob;

  @Fetch(FetchMode.JOIN)
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "title_id", referencedColumnName = "id")
  private Title title;

  @Fetch(FetchMode.JOIN)
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "gender_id", referencedColumnName = "id")
  private Gender gender;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "surname", nullable = false)
  private String surname;

  @Column(name = "cell_number", nullable = false)
  private String cellNumber;

  @Column(name = "email")
  private String email;

  @Column(name = "username", length = 250)
  private String username;

  @Column(name = "password", length = 250)
  private String password;

  @Column(name = "enabled")
  private Boolean enabled;

  @Enumerated
  @Column(name = "status")
  private UsersStatusEnum status;

  @Fetch(FetchMode.JOIN)
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "residential_address", referencedColumnName = "id")
  private Address residentialAddress;

  @Fetch(FetchMode.JOIN)
  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "postal_address", referencedColumnName = "id")
  private Address postalAddress;

  @Fetch(FetchMode.JOIN)
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "creator_user_id", referencedColumnName = "id")
  private User creatorUser;

  @Column(name = "image")
  private String image;

  @Column(name = "change_password")
  private Boolean changePassword;

  @Column(name = "deleted", nullable = false, columnDefinition = ("boolean default false"))
  private Boolean deleted;

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(
      name = "user_roles",
      joinColumns = @JoinColumn(name = "user_id"),
      inverseJoinColumns = @JoinColumn(name = "role_id"))
  private Set<Role> roles = new HashSet<>();
}
