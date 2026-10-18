package com.tertiary.verify.data_model.model.beans;

import java.io.Serializable;
import java.util.Date;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SignupRequest implements Serializable {
  @NotBlank
  @Size(max = 50)
  @Email
  private String email;

  private String idNumber;
  private String name;
  private String surname;
  private String cellNumber;
  private Date password;
  private String alternativeCellNumber;
  private String securityQuestion;
  private String securityAnswer;
  private String officeCellNumber;
  private String association;
  private String idType;
}
