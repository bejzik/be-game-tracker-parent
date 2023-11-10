package com.numarics.player.controller;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.numarics.player.dto.PlayerRegistrationRequest;
import com.numarics.player.model.Player;
import com.numarics.player.service.IPlayerService;

@RestController
@RequestMapping(PlayerController.PLAYER_CONTROLLER_URL)
public class PlayerController {

  private final IPlayerService playerService;

  public static final String PLAYER_CONTROLLER_URL = "/api/player";

  public PlayerController(IPlayerService playerService) {
    this.playerService = playerService;
  }

  @PostMapping("/register")
  public Player registerPlayer(@RequestBody @Valid PlayerRegistrationRequest request) {
    return playerService.registerPlayer(request);
  }
}
