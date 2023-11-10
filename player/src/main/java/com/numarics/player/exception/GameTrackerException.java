package com.numarics.player.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GameTrackerException extends RuntimeException {
  private Error error;
  private Throwable throwable;

  public GameTrackerException(Error error) {
    this.error = error;
  }

  public GameTrackerException(Error error, Throwable throwable) {
    this.error = error;
    this.throwable = throwable;
  }
}
