package com.numarics.commons.configuration;


import com.numarics.commons.exception.GameTrackerError;
import com.numarics.commons.exception.GameTrackerException;

public class Validation {
  public static <T> void notNull(T value, GameTrackerError exception) {
    if (value == null) {
      throw new GameTrackerException(exception);
    }
  }
}
