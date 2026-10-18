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
public class ReportInstitutionRequest {
  @NotNull(message = "Please enter your name")
  @NotBlank(message = "Please enter your name")
  @NotEmpty(message = "Please enter your name")
  @Size(max = 50, message = "Name should not exceed 50 characters")
  @Pattern(regexp = "^(?=.*[a-zA-Z])(?!\\d+$).{2,}$", message = "Please enter valid name")
  @Size(min = 2, message = "Name should be at least 2 characters")
  private String userName;

  @NotNull(message = "Please enter your surname")
  @NotBlank(message = "Please enter your surname")
  @NotEmpty(message = "Please enter your surname")
  @Size(max = 50, message = "Surname should not exceed 50 characters")
  @Pattern(regexp = "^(?=.*[a-zA-Z])(?!\\d+$).{2,}$", message = "Please enter valid surname")
  @Size(min = 2, message = "Surname should be at least 2 characters")
  private String userSurname;

  @NotNull(message = "Please enter email address")
  @NotBlank(message = "Please enter email address")
  @NotEmpty(message = "Please enter email address")
  @Email(message = "Please enter a valid email address")
  private String userEmail;

  @Pattern(
      regexp = "^(\\+27|0)[6-8]\\d{8}$",
      message = "Please enter a valid South African cell number")
  private String userCellNumber;

  @NotNull(message = "Please enter institution name")
  @NotBlank(message = "Please enter institution name")
  @NotEmpty(message = "Please enter institution name")
  private String instName;

  @NotNull(message = "Please enter institution known as name")
  @NotBlank(message = "Please enter institution known as name")
  @NotEmpty(message = "Please enter institution known as name")
  private String instKnownAs;

  @NotNull(message = "Please enter address line 1")
  @NotBlank(message = "Please enter address line 1")
  @NotEmpty(message = "Please enter address line 1")
  private String instAddressLine1;

  @NotNull(message = "Please enter address line 2")
  @NotBlank(message = "Please enter address line 2")
  @NotEmpty(message = "Please enter address line 2")
  private String instAddressLine2;

  private String instAddressLine3;

  @NotNull(message = "Please select province")
  @NotBlank(message = "Please select province")
  @NotEmpty(message = "Please select province")
  private String provinceName;

  @NotNull(message = "Please enter suburb")
  @NotBlank(message = "Please enter suburb")
  @NotEmpty(message = "Please enter suburb")
  private String instSuburb;

  @NotNull(message = "Please enter suburb")
  @NotBlank(message = "Please enter suburb")
  @NotEmpty(message = "Please enter suburb")
  @Pattern(
      regexp = "\\d{4}",
      message = "Invalid suburb code. Please enter a valid South African suburb code.")
  private String instPostalCode;

  @NotNull(message = "Please enter additional information")
  @NotBlank(message = "Please enter additional information")
  @NotEmpty(message = "Please enter additional information")
  private String additionalInfo;
}
