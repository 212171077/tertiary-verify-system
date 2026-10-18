package com.tertiary.verify.data_model.validator;

import java.lang.reflect.Field;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class InstIdAndInstNameValidator
    implements ConstraintValidator<InstIdAndInstNameNotNull, Object> {
  @Override
  public boolean isValid(Object object, ConstraintValidatorContext context) {
    try {
      Field instIdField = object.getClass().getDeclaredField("instId");
      Field instNameField = object.getClass().getDeclaredField("instName");
      instIdField.setAccessible(true);
      instNameField.setAccessible(true);
      Long instIdValue = (Long) instIdField.get(object);
      String instNameValue = (String) instNameField.get(object);

      return instIdValue != null || (instNameValue != null && !instNameValue.isEmpty());
    } catch (NoSuchFieldException | IllegalAccessException e) {
      // Handle the exception as per your requirements
      e.printStackTrace();
      return false;
    }
  }
}
