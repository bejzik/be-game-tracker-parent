package com.numarics.playerservice.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GameTrackerException extends RuntimeException {
  private Error error;
  private String message;
  private Throwable throwable;

  public GameTrackerException(Error error, String message) {
    this.error = error;
    this.message = message;
  }

  public GameTrackerException(Error error, String message, Throwable throwable) {
    this.error = error;
    this.message = message;
    this.throwable = throwable;
  }
}
