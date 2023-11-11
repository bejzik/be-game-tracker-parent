package com.numarics.player.service;

import java.util.UUID;
import com.numarics.player.dto.PlayerRegistrationRequest;
import com.numarics.player.model.Player;

public interface IPlayerService {

  Player registerPlayer(PlayerRegistrationRequest request);

  Player getPlayer(UUID playerId);

  void deletePlayer(UUID playerId);
}
