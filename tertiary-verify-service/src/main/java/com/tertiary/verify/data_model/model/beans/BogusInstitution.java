package com.tertiary.verify.data_model.model.beans;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BogusInstitution implements Serializable {
  private static final long serialVersionUID = 1L;
  private String instName;
  private String knownAs;
  private String addressLine1;
  private String addressLine2;
  private String addressLine3;
  private String province;
  private String suburbArea;
  private String postalCode;
  private String reporterName;
  private String reporterSurname;
  private String reporterPhone;
  private String reporterEmail;
}
