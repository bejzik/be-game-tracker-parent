package com.numarics.player.exception;

import org.springframework.http.HttpStatus;

public interface Error {
  HttpStatus status();
  String code();

  String message();
}
