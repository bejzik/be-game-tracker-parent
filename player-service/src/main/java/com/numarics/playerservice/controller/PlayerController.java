package com.numarics.playerservice.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import com.numarics.playerservice.dto.PlayerRegistrationRequest;
import com.numarics.playerservice.service.IPlayerService;

@RestController
@RequestMapping("/api/player")
public class PlayerController {

  private final IPlayerService playerService;

  public PlayerController(IPlayerService playerService) {
    this.playerService = playerService;
  }

  @PostMapping("/register")
  @ResponseStatus(HttpStatus.CREATED)
  public void registerPlayer(@RequestBody PlayerRegistrationRequest request) {
    playerService.registerPlayer(request);
  }
}
