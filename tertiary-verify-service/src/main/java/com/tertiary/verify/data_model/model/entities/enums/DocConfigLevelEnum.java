package com.tertiary.verify.data_model.model.entities.enums;

public enum DocConfigLevelEnum {
  UserLevel("User Level"),
  CompanyLevel("Company Level");

  private final String displayName;

  DocConfigLevelEnum(String displayNameX) {
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
