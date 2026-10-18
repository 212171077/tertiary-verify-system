package com.tertiary.verify.data_model.model.entities;

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
@Table(name = "image_model")
public class ImageModel {
  @Id
  @Column(name = "id")
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @Column(name = "name")
  private String name;

  @Column(name = "type")
  private String type;

  @Lob
  @Column(name = "pic")
  private byte[] pic;

  @Column(name = "last_update_date", length = 19)
  private Date lastUpdateDate;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "create_date", length = 19)
  private Date createDate;

  public ImageModel(long id, String name, String type, byte[] pic) {
    this.id = id;
    this.name = name;
    this.type = type;
    this.pic = pic;
  }
}
