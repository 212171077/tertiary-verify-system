package com.tertiary.verify.data_model.model.entities.enums;

import com.tertiary.verify.data_model.model.constants.AppConstants;

// TODO: Auto-generated Javadoc

/** The Enum WspTypeEnum. */
public enum YesNoEnum {
  Yes("Yes") {
    @Override
    public Long getYesNoLookupId() {
      return AppConstants.YES_ID;
    }
  },

  No("No") {
    @Override
    public Long getYesNoLookupId() {
      return AppConstants.NO_ID;
    }
  };

  /** The display name. */
  private final String displayName;

  private Long id;

  /**
   * Instantiates a new wsp type enum.
   *
   * @param displayNameX the display name X
   */
  YesNoEnum(String displayNameX) {
    displayName = displayNameX;
  }

  /**
   * Gets the id passport enum by value.
   *
   * @param value the value
   * @return the id passport enum by value
   */
  public static final YesNoEnum getIdPassportEnumByValue(int value) {
    for (YesNoEnum status : YesNoEnum.values()) {
      if (status.ordinal() == value) return status;
    }

    return null;
  }

  /**
   * Gets the friendly name.
   *
   * @return the friendly name
   */
  public String getFriendlyName() {
    return toString();
  }

  /**
   * Gets the registration name.
   *
   * @return the registration name
   */
  public Long getYesNoLookupId() {
    return id;
  }
}
