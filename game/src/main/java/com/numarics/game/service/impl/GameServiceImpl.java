package com.numarics.game.service.impl;

import java.util.UUID;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.numarics.commons.configuration.Validation;
import com.numarics.commons.exception.GameTrackerError;
import com.numarics.commons.exception.GameTrackerException;
import com.numarics.game.client.model.PlayerRegistrationRequest;
import com.numarics.game.client.PlayerServiceApi;
import com.numarics.game.client.model.Player;
import com.numarics.game.dto.GamePlayerResponse;
import com.numarics.game.dto.PlayGameRequest;
import com.numarics.game.dto.UpdateGameRequest;
import com.numarics.game.model.Game;
import com.numarics.game.model.GameStatus;
import com.numarics.game.persistence.adapter.IGamePersistence;
import com.numarics.game.service.IGameService;

import static com.numarics.commons.exception.GameTrackerError.*;

@Service
public class GameServiceImpl implements IGameService {

  private final IGamePersistence persistence;
  private final PlayerServiceApi playerServiceApi;
  private final ModelMapper modelMapper;

  private static final String GAME_NAME_PREFIX = "game_";
  private static final String PLAYER_NAME_PREFIX = "player_";

  public GameServiceImpl(IGamePersistence persistence, PlayerServiceApi playerServiceApi, ModelMapper modelMapper) {
    this.persistence = persistence;
    this.playerServiceApi = playerServiceApi;
    this.modelMapper = modelMapper;
  }

  @Transactional
  @Override
  public GamePlayerResponse playGame(PlayGameRequest request) {
    Validation.notNull(request, GameTrackerError.GAME_SERVICE_MISSING_DATA);

    var game = Game.builder()
            .name(generateName(request.getGameName(), GAME_NAME_PREFIX))
            .status(GameStatus.NEW).build();
    game = persistence.saveGame(game);

    Player player = null;
    if (request.getPlayerId() != null) {
      player = playerServiceApi.getPlayerById(request.getPlayerId());
      if (player.getGameId() == null) {
        player.setGameId(game.getId());
        playerServiceApi.registerPlayer(PlayerRegistrationRequest.builder()
                        .gameId(game.getId()).id(player.getId()).playerName(player.getName()).build());
      } else {
        throw new GameTrackerException(GAME_SERVICE_PLAYER_REGISTERED_FOR_ANOTHER_GAME);
      }
    } else {
      player = playerServiceApi.registerPlayer(
              PlayerRegistrationRequest.builder()
                      .gameId(game.getId())
                      .playerName(generateName(request.getPlayerName(), PLAYER_NAME_PREFIX))
                      .build());
    }

    return mapToResponse(game, player);
  }

  @Override
  public Game getGame(UUID gameId) {
    var game = persistence.getGameById(gameId);
    Validation.notNull(game, GAME_SERVICE_GAME_NOT_FOUND);
    return game;
  }

  @Override
  public Game updateGame(UpdateGameRequest request) {
    Validation.notNull(request, GameTrackerError.GAME_SERVICE_MISSING_DATA);

    var game = persistence.getGameById(request.getGameId());
    Validation.notNull(game, GAME_SERVICE_GAME_NOT_FOUND);

    if (!game.getStatus().checkIsValidTransition(request.getStatus())) {
      throw new GameTrackerException(GAME_SERVICE_GAME_COULD_NOT_BE_UPDATED);
    }

    game.setStatus(request.getStatus());
    return persistence.saveGame(game);
  }

  @Transactional
  @Override
  public void deleteGame(UUID gameId) {
    var game = persistence.getGameById(gameId);
    Validation.notNull(game, GAME_SERVICE_GAME_NOT_FOUND);
    game.setStatus(GameStatus.DROPPED);

    persistence.saveGame(game);
  }

  @Override
  public Page<GamePlayerResponse> searchGames(String gameName, GameStatus status, String playerName, Pageable pageable) {
    var response =  persistence.findByGameNameAndStatus(gameName, status, pageable);
    var players = playerServiceApi.searchPlayers(playerName);

    return response.map(game -> {
      var optionalPlayer = players.stream().filter(player -> game.getId().equals(player.getGameId())).findFirst();
      return optionalPlayer.map(player -> mapToResponse(game, player)).orElse(null);
    });

  }

  private String generateName(String name, String prefix) {
    return name != null ? name : prefix + UUID.randomUUID();
  }

  private GamePlayerResponse mapToResponse(Game game, Player player) {
    var response = modelMapper.map(game, GamePlayerResponse.class);
    response.setPlayerId(player.getId());
    response.setPlayerName(player.getName());
    return response;
  }

  private GamePlayerResponse mapToResponse(Game game) {
    return modelMapper.map(game, GamePlayerResponse.class);
  }
}
