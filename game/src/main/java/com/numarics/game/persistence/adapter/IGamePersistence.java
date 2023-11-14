package com.numarics.game.persistence.adapter;

import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.numarics.game.model.Game;
import com.numarics.game.model.GameStatus;

public interface IGamePersistence {

  Game saveGame(Game game);

  Game getGameById(UUID gameId);

  void deleteGameById(UUID gameId);

  Page<Game> findByGameNameAndStatus(String name, GameStatus status, Pageable pageable);
}
