package com.tertiary.verify.data_model.model.entities.enums;

public enum UsersStatusEnum {
  Active("Active"),
  InActive("In-Active"),
  EmailNotConfrimed("Email not confrimed");

  private final String displayName;

  UsersStatusEnum(String displayNameX) {
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
