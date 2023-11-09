package com.numarics.playerservice.exception;

import org.springframework.http.HttpStatus;

public enum GateTrackerError implements Error {

  PLAYER_SERVICE_GENERAL_EXCEPTION {
    @Override
    public HttpStatus status() {
      return HttpStatus.INTERNAL_SERVER_ERROR;
    }
    @Override
    public String code() {
      return this.name();
    }
  },

  PLAYER_SERVICE_WRONG_DATA {
    @Override
    public HttpStatus status() {
      return HttpStatus.BAD_REQUEST;
    }
    @Override
    public String code() {
      return this.name();
    }
  }
}
