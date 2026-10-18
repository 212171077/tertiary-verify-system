package com.tertiary.verify.data_model.model.beans;

import com.tertiary.verify.data_model.model.entities.common.IDataEntity;
import java.util.Date;
import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MailLogResponse implements IDataEntity {

  private Date createDate;
  private String fromEmail;
  private String toEmail;
  private String subject;
  private Boolean error;
  private String errorMessage;
  private String status;
}
