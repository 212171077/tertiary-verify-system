package com.tertiary.verify.data_model.model.beans;

import java.io.Serializable;
import java.util.ArrayList;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BogusCourses implements Serializable {
  private static final long serialVersionUID = 1L;
  private Long institutionID;
  private ArrayList<Long> courseList;
  private String reporterName;
  private String reporterSurname;
  private String reporterPhone;
  private String reporterEmail;
}
