package com.tertiary.verify.data_model.validator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = InstIdAndInstNameValidator.class)
public @interface InstIdAndInstNameNotNull {
  String message() default "Both fields (instId and instName) cannot be null";

  String description() default "instId and instName";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
