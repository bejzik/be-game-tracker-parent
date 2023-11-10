package com.numarics.player.configuration;

import com.numarics.player.exception.GameTrackerError;
import com.numarics.player.exception.GameTrackerException;

public class Validation {
  public static <T> void notNull(T value, GameTrackerError exception) {
    if (value == null) {
      throw new GameTrackerException(exception);
    }
  }
}
