package com.numarics.playerservice.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.numarics.playerservice.exception.GameTrackerException;
import com.numarics.playerservice.model.ErrorResponse;
import com.numarics.playerservice.service.impl.PlayerServiceImpl;

import static com.numarics.playerservice.exception.GateTrackerError.PLAYER_SERVICE_GENERAL_EXCEPTION;

@RestControllerAdvice
public class ErrorMapper {

  private final Logger logger = LoggerFactory.getLogger(PlayerServiceImpl.class);

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handle(Exception e) {
    return handle(new GameTrackerException(PLAYER_SERVICE_GENERAL_EXCEPTION, "General error", e));
  }

  @ExceptionHandler(GameTrackerException.class)
  ResponseEntity<ErrorResponse> handle(GameTrackerException exception) {
    logger.error(exception.getMessage(), exception.getCause());
    return new ResponseEntity<> (
            new ErrorResponse(exception.getError().code(),
                    exception.getMessage()),
                    exception.getError().status());
  }

}
