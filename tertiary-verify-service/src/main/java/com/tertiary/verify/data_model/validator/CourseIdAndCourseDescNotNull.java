package com.tertiary.verify.data_model.validator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CourseIdAndCourseDescValidator.class)
public @interface CourseIdAndCourseDescNotNull {
  String message() default "Both fields (courseId and courseDesc) cannot be null";

  String description() default "courseId and courseDesc";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
