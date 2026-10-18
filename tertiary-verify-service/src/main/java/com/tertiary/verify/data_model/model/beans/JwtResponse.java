package com.tertiary.verify.data_model.model.beans;

import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class JwtResponse {
  private String token;
  private String type = "Bearer";
  private Long id;
  private String username;
  private String email;
  private List<String> roles;
  private boolean changePassword;

  public JwtResponse(
      String accessToken,
      Long id,
      String username,
      String email,
      List<String> roles,
      boolean changePassword) {
    this.token = accessToken;
    this.id = id;
    this.username = username;
    this.email = email;
    this.roles = roles;
    this.changePassword = changePassword;
  }
}
