package com.tertiary.verify.data_model.model.entities.enums;

public enum ReportTypeEnum {
  BogusInstitute("Bogus Institute"),
  BogusCourses("Bogus Courses");

  private final String displayName;

  ReportTypeEnum(String displayNameX) {
    displayName = displayNameX;
  }

  /*
   * (non-Javadoc)
   *
   * @see java.lang.Enum#toString()
   */
  @Override
  public String toString() {
    return displayName;
  }

  public String getFriendlyName() {
    return toString();
  }
}
