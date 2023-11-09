package com.numarics.playerservice.exception;

import org.springframework.http.HttpStatus;

public interface Error {
  HttpStatus status();
  String code();
}
