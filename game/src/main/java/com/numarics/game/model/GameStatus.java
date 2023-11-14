package com.numarics.game.model;

import java.util.Objects;
import org.springframework.stereotype.Component;
import com.numarics.commons.exception.GameTrackerException;

import static com.numarics.commons.exception.GameTrackerError.GAME_SERVICE_GAME_COULD_NOT_BE_UPDATED;

@Component
public enum GameStatus {
  NEW {
    @Override
    public boolean checkIsValidTransition(GameStatus newStatus) {
      return Objects.requireNonNull(newStatus) != GameStatus.NEW;
    }
  },
  FINISHED {
    @Override
    public boolean checkIsValidTransition(GameStatus newStatus) {
      switch (newStatus) {
        case NEW, FINISHED -> {
          return false;
        }
        default -> {
          return true;
        }
      }
    }
  },
  DROPPED {
    @Override
    public boolean checkIsValidTransition(GameStatus newStatus) {
      throw new GameTrackerException(GAME_SERVICE_GAME_COULD_NOT_BE_UPDATED);
    }
  };

  public abstract boolean checkIsValidTransition(GameStatus newStatus);
}
