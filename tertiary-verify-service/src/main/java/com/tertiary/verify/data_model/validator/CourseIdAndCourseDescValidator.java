package com.tertiary.verify.data_model.validator;

import java.lang.reflect.Field;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class CourseIdAndCourseDescValidator
    implements ConstraintValidator<CourseIdAndCourseDescNotNull, Object> {
  @Override
  public boolean isValid(Object object, ConstraintValidatorContext context) {
    try {
      Field courseIdField = object.getClass().getDeclaredField("courseId");
      Field courseDescField = object.getClass().getDeclaredField("courseDesc");
      courseIdField.setAccessible(true);
      courseDescField.setAccessible(true);
      Long courseIdValue = (Long) courseIdField.get(object);
      String courseDescValue = (String) courseDescField.get(object);

      return courseIdValue != null || (courseDescValue != null && !courseDescValue.isEmpty());
    } catch (NoSuchFieldException | IllegalAccessException e) {
      // Handle the exception as per your requirements
      e.printStackTrace();
      return false;
    }
  }
}
