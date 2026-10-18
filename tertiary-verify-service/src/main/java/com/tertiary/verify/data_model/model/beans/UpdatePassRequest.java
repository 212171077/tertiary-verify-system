package com.tertiary.verify.data_model.model.beans;

import java.io.Serializable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePassRequest implements Serializable {

  @NotNull private Long userId;

  @NotBlank
  @Pattern(
      regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$",
      message =
          "Password must contain at least 8 characters, including one uppercase letter, one lowercase letter, one digit, and one special character.")
  private String newPassword;

  @NotBlank private String oldPassword;
  @NotBlank private String confirmPassword;
}
