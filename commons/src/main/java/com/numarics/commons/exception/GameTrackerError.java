package com.numarics.commons.exception;

import org.springframework.http.HttpStatus;

public enum GameTrackerError implements Error {

  GAME_TRACKER_GENERAL_EXCEPTION {
    @Override
    public HttpStatus status() {
      return HttpStatus.INTERNAL_SERVER_ERROR;
    }
    @Override
    public String code() {
      return this.name();
    }

    @Override
    public String message() {
      return "Unknown error";
    }
  },

  GAME_TRACKER_VALIDATION_FIELDS {
    @Override
    public HttpStatus status() {
      return HttpStatus.BAD_REQUEST;
    }
    @Override
    public String code() {
      return this.name();
    }

    @Override
    public String message() {
      return "Failed validation of request";
    }
  },

  PLAYER_SERVICE_MISSING_DATA {
    @Override
    public HttpStatus status() {
      return HttpStatus.BAD_REQUEST;
    }

    @Override
    public String code() {
      return this.name();
    }

    @Override
    public String message() {
      return "Missing data";
    }
  },

  PLAYER_SERVICE_PLAYER_NOT_FOUND {
    @Override
    public HttpStatus status() {
      return HttpStatus.NOT_FOUND;
    }

    @Override
    public String code() {
      return this.name();
    }

    @Override
    public String message() {
      return "Player not found";
    }
  },

  GAME_SERVICE_MISSING_DATA {
    @Override
    public HttpStatus status() {
      return HttpStatus.BAD_REQUEST;
    }

    @Override
    public String code() {
      return this.name();
    }

    @Override
    public String message() {
      return "Missing data";
    }
  },

  GAME_SERVICE_GAME_NOT_FOUND {
    @Override
    public HttpStatus status() {
      return HttpStatus.NOT_FOUND;
    }

    @Override
    public String code() {
      return this.name();
    }

    @Override
    public String message() {
      return "Game not found";
    }
  }
}
