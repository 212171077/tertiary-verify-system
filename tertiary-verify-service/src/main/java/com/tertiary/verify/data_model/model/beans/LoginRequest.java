package com.tertiary.verify.data_model.model.beans;

import java.io.Serializable;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest implements Serializable {
  @NotBlank private String username;
  @NotBlank private String password;
}
