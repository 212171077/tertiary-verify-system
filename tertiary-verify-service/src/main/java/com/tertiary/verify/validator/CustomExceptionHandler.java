package com.tertiary.verify.validator;

import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

  private static final Logger LOGGER = LoggerFactory.getLogger(CustomExceptionHandler.class);

  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(
      MethodArgumentNotValidException ex,
      HttpHeaders headers,
      HttpStatus status,
      WebRequest request) {

    Map<String, Object> responseBody = new LinkedHashMap<>();

    responseBody.put("responseCode", status.value());
    responseBody.put("responseMessage", status.getReasonPhrase());

    Map<String, String> errors = new HashMap<>();

    String className = null;
    for (ObjectError error : ex.getBindingResult().getAllErrors()) {

      String fieldName;
      if (error instanceof FieldError) {
        fieldName = ((FieldError) error).getField();
      } else {
        fieldName = error.getObjectName();
        Object[] arguments = error.getArguments();
        if (arguments != null && arguments.length >= 2) {
          fieldName = arguments[1].toString();
        }
      }
      String errorMessage = error.getDefaultMessage();
      className = error.getObjectName();
      errors.put(fieldName, errorMessage);
    }

    responseBody.put("errors", errors);

    LOGGER.info("Validation failure. [Web request: {}, Errors: {}]", className, errors);

    return new ResponseEntity<>(responseBody, headers, status);
  }
}
