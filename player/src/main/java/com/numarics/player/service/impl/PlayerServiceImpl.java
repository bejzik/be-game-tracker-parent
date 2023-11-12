package com.numarics.player.service.impl;

import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.numarics.commons.configuration.Validation;
import com.numarics.player.dto.PlayerRegistrationRequest;
import com.numarics.player.model.Player;
import com.numarics.player.persistence.adapter.IPlayerPersistence;
import com.numarics.player.service.IPlayerService;

import static com.numarics.commons.exception.GameTrackerError.PLAYER_SERVICE_MISSING_DATA;
import static com.numarics.commons.exception.GameTrackerError.PLAYER_SERVICE_PLAYER_NOT_FOUND;

@Service
public class PlayerServiceImpl implements IPlayerService {

  private final IPlayerPersistence persistence;

  private final Logger logger = LoggerFactory.getLogger(PlayerServiceImpl.class);

  public PlayerServiceImpl(IPlayerPersistence persistence) {
    this.persistence = persistence;
  }

  @Override
  public Player registerPlayer(PlayerRegistrationRequest request) {
    Validation.notNull(request, PLAYER_SERVICE_MISSING_DATA);
    Validation.notNull(request.getGameId(), PLAYER_SERVICE_MISSING_DATA);

    var player = Player.builder().name(generatePlayerName()).gameId(request.getGameId()).build();

    var response = persistence.registerPlayer(player);
    logger.info("Registered player with data: " + response.toString());
    return response;
  }

  @Override
  public Player getPlayer(UUID playerId) {
    Validation.notNull(playerId, PLAYER_SERVICE_MISSING_DATA);

    var response = persistence.getPlayerById(playerId);
    Validation.notNull(response, PLAYER_SERVICE_PLAYER_NOT_FOUND);
    logger.info("Fetched player with data: " + response);
    return response;
  }

  @Override
  public void deletePlayer(UUID playerId) {
    Validation.notNull(playerId, PLAYER_SERVICE_MISSING_DATA);

    var player = persistence.getPlayerById(playerId);
    Validation.notNull(player, PLAYER_SERVICE_PLAYER_NOT_FOUND);

    persistence.deletePlayerById(player.getId());
    logger.info("Deleted player with data: [playerId: " + playerId + "]");
  }

  private String generatePlayerName() {
    return "player_" + UUID.randomUUID();
  }
}
