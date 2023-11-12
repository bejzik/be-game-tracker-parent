package com.numarics.game.service;

import com.numarics.game.dto.PlayGameRequest;
import com.numarics.game.model.Game;

public interface IGameService {

  Game playGame(PlayGameRequest request);
}
