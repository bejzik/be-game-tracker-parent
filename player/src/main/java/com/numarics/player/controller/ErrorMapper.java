package com.numarics.player.controller;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.numarics.commons.exception.GameTrackerException;
import com.numarics.player.model.ErrorResponse;
import com.numarics.player.service.impl.PlayerServiceImpl;

import static com.numarics.commons.exception.GameTrackerError.GAME_TRACKER_GENERAL_EXCEPTION;
import static com.numarics.commons.exception.GameTrackerError.GAME_TRACKER_VALIDATION_FIELDS;

@RestControllerAdvice
public class ErrorMapper {

  private final Logger logger = LoggerFactory.getLogger(PlayerServiceImpl.class);

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
    logger.error(exception.getMessage(), exception.getCause());
    return new ResponseEntity<> (
            new ErrorResponse(exception.getError().code(),
                    exception.getError().message()),
                    exception.getError().status());
  }
}
