package com.tertiary.verify.data_model.model.entities.enums;

public enum ReportedInstitutionsStatus {
  Submitted("Submitted"),
  UnderInvestigation("Under Investigation"),
  InvestigationPending("Investigation Pending"),
  Closed("Closed");

  private final String displayName;

  ReportedInstitutionsStatus(String displayNameX) {
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
