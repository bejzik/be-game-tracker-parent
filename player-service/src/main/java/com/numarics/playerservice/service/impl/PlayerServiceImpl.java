package com.numarics.playerservice.service.impl;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.numarics.playerservice.dto.PlayerRegistrationRequest;
import com.numarics.playerservice.exception.GameTrackerException;
import com.numarics.playerservice.model.Player;
import com.numarics.playerservice.persistence.adapter.IPlayerPersistence;
import com.numarics.playerservice.service.IPlayerService;

import static com.numarics.playerservice.exception.GateTrackerError.PLAYER_SERVICE_WRONG_DATA;

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
    if(request.getName() == null) {
      throw new GameTrackerException(PLAYER_SERVICE_WRONG_DATA, "Missing player name");
    }
    if(request.getGameId() == null) {
      throw new GameTrackerException(PLAYER_SERVICE_WRONG_DATA, "Missing game id");
    }
    logger.info("started registering player with data: " + request.toString());
    var response = persistence.registerPlayer(mapToDomain(request));
    logger.info("finished registering player with data: " + response.toString());
    return response;
  }

  private Player mapToDomain(PlayerRegistrationRequest request) {
    return modelMapper.map(request, Player.class);
  }
}
