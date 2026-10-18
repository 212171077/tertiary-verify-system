package com.tertiary.verify.data_model.model.beans;

import com.tertiary.verify.data_model.validator.CourseIdAndCourseDescNotNull;
import com.tertiary.verify.data_model.validator.InstIdAndInstNameNotNull;
import javax.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@InstIdAndInstNameNotNull
@CourseIdAndCourseDescNotNull
public class ReportCourseRequest {

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

  private Long instId;
  private String instName;
  private String instKnownAs;
  private Long courseId;
  private String courseType;
  private String courseLevel;
  private String courseDesc;
}
