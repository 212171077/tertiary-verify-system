package com.tertiary.verify.data_model.model.beans;

import java.io.Serializable;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdatedImageRequest implements Serializable {
  @NotNull private String imageName;
  @NotNull private Long id;
  @NotNull private ImageType imageType;
}
