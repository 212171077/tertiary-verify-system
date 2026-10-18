package com.tertiary.verify.data_model.model.beans;

import javax.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ContactUsRequest {

  @NotNull(message = "Please enter your full name")
  @NotBlank(message = "Please enter your full name")
  @NotEmpty(message = "Please enter your full name")
  @Size(max = 100, message = "Full name should not exceed 100 characters")
  @Pattern(regexp = "^(?=.*[a-zA-Z])(?!\\d+$).{2,}$", message = "Please enter valid full name")
  @Size(min = 2, message = "Full name should be at least 2 characters")
  private String fullName;

  @NotNull(message = "Please enter email address")
  @NotBlank(message = "Please enter email address")
  @NotEmpty(message = "Please enter email address")
  @Email(message = "Please enter a valid email address")
  private String email;

  @Pattern(
      regexp = "^(\\+27|0)[6-8]\\d{8}$",
      message = "Please enter a valid South African cell number")
  private String cellNumber;

  @NotNull(message = "Please enter a message")
  @NotBlank(message = "Please enter a message")
  @NotEmpty(message = "Please enter a message")
  @Size(max = 500, message = "Message should not exceed 500 characters")
  private String message;
}
