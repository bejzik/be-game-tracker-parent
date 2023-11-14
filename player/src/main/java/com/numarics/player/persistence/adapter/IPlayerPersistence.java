package com.numarics.player.persistence.adapter;

import java.util.List;
import java.util.UUID;
import com.numarics.player.model.Player;

public interface IPlayerPersistence {

  Player registerPlayer(Player player);

  Player getPlayerById(UUID playerId);

  void deletePlayerById(UUID playerId);

  List<Player> findPlayersByNameContaining(String name);
}
