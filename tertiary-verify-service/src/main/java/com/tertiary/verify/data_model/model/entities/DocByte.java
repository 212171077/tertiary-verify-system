package com.tertiary.verify.data_model.model.entities;

import static javax.persistence.GenerationType.IDENTITY;

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
@Table(name = "doc_byte")
public class DocByte implements IDataEntity {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = IDENTITY)
  @Column(name = "id", unique = true, nullable = false)
  private Long id;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "create_date", length = 19)
  private Date createDate;

  @Column(name = "last_update_date", length = 19)
  private Date lastUpdateDate;

  @Column(name = "target_class")
  private String targetClass;

  @Column(name = "target_key")
  private Long targetKey;

  @Lob
  @Column(name = "byte_data")
  private byte[] byteData;

  @Column(name = "file_name")
  private String fileName;

  @Column(name = "content_type")
  private String contentType;
}
