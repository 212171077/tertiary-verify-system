package com.tertiary.verify.data_model.model.entities.enums;

public enum EmailTemplateTypeEnum {
  Approval("Approval"),
  Reject("Reject"),
  FinalApproval("Final Approval"),
  FinalReject("Final Reject"),
  Acknowlegement("Acknowlegement");

  private final String displayName;

  EmailTemplateTypeEnum(String displayNameX) {
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
