package com.tertiary.verify.data_model.model.entities.enums;

public enum ApprovalStatusEnum {
  Approved("APPROVED", "The institution has been officially accepted"),
  Rejected("REJECTED", "The institution has been declined or not accepted"),
  PendingApproval("PENDING", "The institution is awaiting review and approval"),
  Active("ACTIVE", "The institution is currently in an operational or usable state"),
  InActive("IN-ACTIVE", "The institution  is not currently in use or operational"),
  Expired("EXPIRED", "The institution has reached the end of its validity period or duration"),
  EndDateExpired(
      "END-DATE-EXPIRED",
      "The institution accreditation end date has passed, resulting in expiration"),
  RegistrationInProgress("REGISTERING", "Registration process is currently ongoing"),
  UpdateInProgress("UPDATING", "Update or modification process is currently underway"),
  ProvisionalApproved(
      "PROV-APPROVED", "The institution application has been conditionally approved");

  private final String displayName;
  private final String description;

  ApprovalStatusEnum(String displayName, String description) {
    this.displayName = displayName;
    this.description = description;
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
    return displayName;
  }

  public String getDescription() {
    return description;
  }
}
