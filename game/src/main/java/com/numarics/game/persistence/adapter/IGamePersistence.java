package com.numarics.game.persistence.adapter;

import java.util.UUID;
import com.numarics.game.model.Game;

public interface IGamePersistence {

  Game saveGame(Game game);

  Game getGameById(UUID gameId);

  void deleteGameById(UUID gameId);
}
