package com.tertiary.verify.data_model.model.beans;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfileImageRequest {
  @NotBlank
  @Size(max = 7000000)
  private String image;

  @NotBlank
  private String contentType;
}
