package com.tertiary.verify.data_model.model.beans;

import com.tertiary.verify.data_model.model.entities.enums.AddressTypeEnum;
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
public class AddressDetailsRequest implements Serializable {

  @NotNull private Long userId;

  @NotNull private AddressTypeEnum addressType;

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

  @NotNull(message = "Please enter town, e.g township, city, or provincial information")
  @NotBlank(message = "Please enter town, e.g township, city, or provincial information")
  @NotEmpty(message = "Please enter town, e.g township, city, or provincial information")
  private String addressLine3;

  private String addressLine4;

  @NotNull(message = "Please select city")
  private DropDown city;

  @NotNull(message = "Please enter postal code")
  @NotBlank(message = "Please enter postal code")
  @NotEmpty(message = "Please enter postal code")
  @Pattern(
      regexp = "\\d{4}",
      message = "Invalid postal code. Please enter a valid South African postal code.")
  private String code;

  @NotNull(message = "Please select province")
  private DropDown province;
}
