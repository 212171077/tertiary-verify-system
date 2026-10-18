package com.tertiary.verify.data_model.model.entities.enums;

public enum ActionTakenEnum {
  approve("Approve"),
  reject("Reject"),
  complete("Complete"),
  view("View");

  private final String displayName;

  ActionTakenEnum(String displayNameX) {
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
