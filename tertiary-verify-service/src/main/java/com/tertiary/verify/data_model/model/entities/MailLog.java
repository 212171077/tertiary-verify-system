package com.tertiary.verify.data_model.model.entities;

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
@Table(name = "mail_log")
public class MailLog implements IDataEntity {

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

  @Column(name = "from_email")
  private String fromEmail;

  @Column(name = "to_email")
  private String toEmail;

  @Column(name = "cc_email")
  private String ccEmails;

  @Column(name = "subject")
  private String subject;

  @Column(name = "content_mssg", length = 5000)
  private String contentMssg;

  @Column(name = "error")
  private Boolean error;

  @Column(name = "error_message", length = 5000)
  private String errorMessage;

  @Column(name = "deleted", nullable = false, columnDefinition = ("boolean default false"))
  private Boolean deleted;

  public MailLog(
      String fromEmail,
      String toEmail,
      String ccEmails,
      String subject,
      String contentMssg,
      Boolean error,
      String errorMessage) {
    super();
    this.fromEmail = fromEmail;
    this.toEmail = toEmail;
    this.ccEmails = ccEmails;
    this.subject = subject;
    this.contentMssg = contentMssg;
    this.error = error;
    this.errorMessage = errorMessage;
  }
}
