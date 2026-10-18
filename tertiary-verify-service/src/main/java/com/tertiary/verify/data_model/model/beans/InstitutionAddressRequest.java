package com.tertiary.verify.data_model.model.beans;

import java.io.Serializable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InstitutionAddressRequest implements Serializable {

  private Long id;

  @NotNull private Long institutionId;

  @NotNull(
      message =
          "Please enter address line 1, e.g street name and number, building name, or complex name")
  @NotBlank(
      message =
          "Please enter address line 1, e.g street name and number, building name, or complex name")
  @NotEmpty(
      message =
          "Please enter address line 1, e.g street name and number, building name, or complex name")
  private String addressLine1;

  @NotNull(message = "Please enter address line 2, e.g suburb or neighborhood")
  @NotBlank(message = "Please enter address line 2, e.g suburb or neighborhood")
  @NotEmpty(message = "Please enter address line 2, e.g suburb or neighborhood")
  private String addressLine2;

  private String addressLine3;

  private String addressLine4;

  private String examinationNumber;

  @NotNull(message = "Please enter city")
  @NotBlank(message = "Please enter city")
  @NotEmpty(message = "Please enter city")
  private String city;

  @NotNull(message = "Please enter postal code")
  @NotBlank(message = "Please enter postal code")
  @NotEmpty(message = "Please enter postal code")
  @Pattern(
      regexp = "\\d{4}",
      message = "Invalid postal code. Please enter a valid South African postal code.")
  private String code;

  @NotNull(message = "Please enter province")
  @NotBlank(message = "Please enter province")
  @NotEmpty(message = "Please enter province")
  private String province;
}
