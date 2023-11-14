package com.numarics.player.controller;

import jakarta.validation.Valid;

import java.util.List;
import java.util.UUID;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

  @GetMapping("/{playerId}")
  public Player getPlayer(@PathVariable("playerId") UUID playerId) {
    return playerService.getPlayer(playerId);
  }

  @DeleteMapping("/{playerId}")
  public void deletePlayer(@PathVariable("playerId") UUID playerId) {
    playerService.deletePlayer(playerId);
  }

  @GetMapping("/search")
  public List<Player> searchPlayers(@RequestParam(value = "name", required = false) String name) {
    return playerService.searchPlayers(name);
  }
}
