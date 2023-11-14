package com.numarics.game.controller;

import jakarta.validation.Valid;

import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.numarics.game.dto.GamePlayerResponse;
import com.numarics.game.dto.PlayGameRequest;
import com.numarics.game.dto.UpdateGameRequest;
import com.numarics.game.model.Game;
import com.numarics.game.model.GameStatus;
import com.numarics.game.service.IGameService;

@RestController
@RequestMapping(GameController.GAME_CONTROLLER_URL)
public class GameController {

  private final IGameService gameService;

  public static final String GAME_CONTROLLER_URL = "/api/game";

  public GameController(IGameService gameService) {
    this.gameService = gameService;
  }

  @PostMapping("/play")
  public GamePlayerResponse playGame(@RequestBody @Valid PlayGameRequest request) {
    return gameService.playGame(request);
  }

  @GetMapping("/{gameId}")
  public Game playGame(@PathVariable("gameId") UUID gameId) {
    return gameService.getGame(gameId);
  }

  @PutMapping("/play")
  public Game updateGame(@RequestBody @Valid UpdateGameRequest request) {
    return gameService.updateGame(request);
  }

  @DeleteMapping("/{gameId}")
  public Game deleteGame(@PathVariable("gameId") UUID gameId) {
    return gameService.deleteGame(gameId);
  }

  @GetMapping("/search")
  public Page<GamePlayerResponse> searchGames(@RequestParam(value = "gameName", required = false) String gameName,
                                              @RequestParam(value = "status", required = false) GameStatus status,
                                              @RequestParam(value = "playerName", required = false) String playerName,
                                              @PageableDefault(size = 8, sort = "name") Pageable pageable) {
    return gameService.searchGames(gameName, status, playerName, pageable);
  }
}
