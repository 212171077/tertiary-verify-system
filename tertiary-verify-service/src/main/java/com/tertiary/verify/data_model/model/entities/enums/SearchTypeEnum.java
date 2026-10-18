package com.tertiary.verify.data_model.model.entities.enums;

public enum SearchTypeEnum {
  SearchByInstituion("Institution"),
  SearchByAccreditationNumber("Accreditation Number"),
  SearhcByCourse("Course");

  private final String displayName;

  SearchTypeEnum(String displayNameX) {
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
