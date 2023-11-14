package com.numarics.game.client;

import jakarta.validation.Valid;

import java.util.List;
import java.util.UUID;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import com.numarics.game.client.model.PlayerRegistrationRequest;
import com.numarics.game.client.model.Player;

@FeignClient(name = "player-service", url = "http://localhost:8081/api/player")
public interface PlayerServiceApi {

  @PostMapping("/register")
  Player registerPlayer(@RequestBody @Valid PlayerRegistrationRequest request);
  @GetMapping("/{playerId}")
  Player getPlayerById(@PathVariable("playerId") UUID playerId);
  @GetMapping("/search")
  List<Player> searchPlayers(@RequestParam(value = "name", required = false) String name);
}
