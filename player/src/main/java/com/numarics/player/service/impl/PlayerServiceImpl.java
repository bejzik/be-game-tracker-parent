package com.numarics.player.service.impl;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.numarics.player.configuration.Validation;
import com.numarics.player.dto.PlayerRegistrationRequest;
import com.numarics.player.exception.GameTrackerException;
import com.numarics.player.model.Player;
import com.numarics.player.persistence.adapter.IPlayerPersistence;
import com.numarics.player.service.IPlayerService;

import static com.numarics.player.exception.GameTrackerError.*;

@Service
public class PlayerServiceImpl implements IPlayerService {

  private final IPlayerPersistence persistence;
  private final ModelMapper modelMapper;

  private final Logger logger = LoggerFactory.getLogger(PlayerServiceImpl.class);

  public PlayerServiceImpl(IPlayerPersistence persistence, ModelMapper modelMapper) {
    this.persistence = persistence;
    this.modelMapper = modelMapper;
  }

  @Override
  public Player registerPlayer(PlayerRegistrationRequest request) {
    Validation.notNull(request, PLAYER_SERVICE_MISSING_DATA);
    Validation.notNull(request.getName(), PLAYER_SERVICE_MISSING_DATA);
    Validation.notNull(request.getGameId(), PLAYER_SERVICE_MISSING_DATA);

    var response = persistence.registerPlayer(mapToDomain(request));
    logger.info("Registered player with data: " + response.toString());
    return response;
  }

  private Player mapToDomain(PlayerRegistrationRequest request) {
    return modelMapper.map(request, Player.class);
  }
}
