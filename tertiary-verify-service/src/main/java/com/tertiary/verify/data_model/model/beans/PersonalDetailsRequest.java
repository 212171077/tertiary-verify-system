package com.tertiary.verify.data_model.model.beans;

import java.io.Serializable;
import javax.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PersonalDetailsRequest implements Serializable {

  private Long id;

  @NotNull(message = "Please enter your name")
  @NotBlank(message = "Please enter your name")
  @NotEmpty(message = "Please enter your name")
  @Size(max = 50, message = "Name should not exceed 50 characters")
  @Pattern(regexp = "^(?=.*[a-zA-Z])(?!\\d+$).{2,}$", message = "Please enter valid name")
  @Size(min = 2, message = "Name should be at least 2 characters")
  @Pattern(regexp = "^(?=.*[a-zA-Z])(?!\\d+$).{2,}$", message = "Please enter valid name")
  private String name;

  @NotNull(message = "Please enter your surname")
  @NotBlank(message = "Please enter your surname")
  @NotEmpty(message = "Please enter your surname")
  @Size(max = 50, message = "Surname should not exceed 50 characters")
  @Pattern(regexp = "^(?=.*[a-zA-Z])(?!\\d+$).{2,}$", message = "Please enter valid surname")
  @Size(min = 2, message = "Surname should be at least 2 characters")
  private String surname;

  @NotNull(message = "Please enter email address")
  @NotBlank(message = "Please enter email address")
  @NotEmpty(message = "Please enter email address")
  @Email(message = "Please enter a valid email address")
  private String email;

  @Pattern(
      regexp = "^(\\+27|0)[6-8]\\d{8}$",
      message = "Please enter a valid South African cell number")
  private String cellNumber;

  @NotNull(message = "Please select your title")
  private DropDown title;

  @NotNull(message = "Please select your gender")
  private DropDown gender;

  private String idNumber;
}
