package com.numarics.game.service;

import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.RequestParam;
import com.numarics.game.dto.GamePlayerResponse;
import com.numarics.game.dto.PlayGameRequest;
import com.numarics.game.dto.UpdateGameRequest;
import com.numarics.game.model.Game;
import com.numarics.game.model.GameStatus;

public interface IGameService {

  GamePlayerResponse playGame(PlayGameRequest request);

  Game getGame(UUID gameId);

  Game updateGame(UpdateGameRequest request);

  void deleteGame(UUID gameId);

  Page<GamePlayerResponse> searchGames(String gameName,
                                       GameStatus status,
                                       String playerName,
                                       Pageable pageable);
}
