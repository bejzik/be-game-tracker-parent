package com.numarics.game.service.impl;

import org.springframework.stereotype.Service;
import com.numarics.game.dto.PlayGameRequest;
import com.numarics.game.model.Game;
import com.numarics.game.persistence.adapter.IGamePersistence;
import com.numarics.game.service.IGameService;

@Service
public class GameServiceImpl implements IGameService {

  private final IGamePersistence persistence;

  public GameServiceImpl(IGamePersistence persistence) {
    this.persistence = persistence;
  }

  @Override
  public Game playGame(PlayGameRequest request) {
    return new Game();
  }
}
