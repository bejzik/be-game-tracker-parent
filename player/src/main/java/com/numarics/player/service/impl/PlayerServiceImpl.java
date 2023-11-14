package com.numarics.player.service.impl;

import java.util.List;
import java.util.UUID;
import org.modelmapper.ModelMapper;
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
  private final ModelMapper modelMapper;

  public PlayerServiceImpl(IPlayerPersistence persistence, ModelMapper modelMapper) {
    this.persistence = persistence;
    this.modelMapper = modelMapper;
  }

  @Override
  public Player registerPlayer(PlayerRegistrationRequest request) {
    Validation.notNull(request, PLAYER_SERVICE_MISSING_DATA);
    Validation.notNull(request.getPlayerName(), PLAYER_SERVICE_MISSING_DATA);

    var player = mapToDomain(request);

    return persistence.registerPlayer(player);
  }

  @Override
  public Player getPlayer(UUID playerId) {
    Validation.notNull(playerId, PLAYER_SERVICE_MISSING_DATA);

    var response = persistence.getPlayerById(playerId);
    Validation.notNull(response, PLAYER_SERVICE_PLAYER_NOT_FOUND);
    return response;
  }

  @Override
  public void deletePlayer(UUID playerId) {
    Validation.notNull(playerId, PLAYER_SERVICE_MISSING_DATA);

    var player = persistence.getPlayerById(playerId);
    Validation.notNull(player, PLAYER_SERVICE_PLAYER_NOT_FOUND);

    persistence.deletePlayerById(player.getId());
  }

  @Override
  public List<Player> searchPlayers(String name) {
    return persistence.findPlayersByNameContaining(name);
  }

  private Player mapToDomain(PlayerRegistrationRequest request) {
    return modelMapper.map(request, Player.class);
  }
}
