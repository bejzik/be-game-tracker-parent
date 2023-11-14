package com.numarics.game.controller;

import java.util.ArrayList;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.numarics.commons.exception.GameTrackerException;
import com.numarics.commons.model.ErrorResponse;

import static com.numarics.commons.exception.GameTrackerError.GAME_TRACKER_GENERAL_EXCEPTION;
import static com.numarics.commons.exception.GameTrackerError.GAME_TRACKER_VALIDATION_FIELDS;

@RestControllerAdvice
public class ErrorMapper {

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handle(Exception e) {
    return handle(new GameTrackerException(GAME_TRACKER_GENERAL_EXCEPTION, e));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  ResponseEntity<ErrorResponse> handle(MethodArgumentNotValidException e) {
    List<String> errors = new ArrayList<>();
    for (FieldError error : e.getBindingResult().getFieldErrors()) {
      errors.add(error.getField() + ": " + error.getDefaultMessage());
    }
    for (ObjectError error : e.getBindingResult().getGlobalErrors()) {
      errors.add(error.getObjectName() + ": " + error.getDefaultMessage());
    }
    return new ResponseEntity<> (
            new ErrorResponse(GAME_TRACKER_VALIDATION_FIELDS.code(),
                    errors.toString()),
            HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(GameTrackerException.class)
  ResponseEntity<ErrorResponse> handle(GameTrackerException exception) {
    return new ResponseEntity<> (
            new ErrorResponse(exception.getError().code(),
                    exception.getError().message()),
                    exception.getError().status());
  }
}
