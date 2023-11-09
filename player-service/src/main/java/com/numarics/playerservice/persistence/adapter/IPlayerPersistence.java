package com.numarics.playerservice.persistence.adapter;

import com.numarics.playerservice.model.Player;

public interface IPlayerPersistence {

  Player registerPlayer(Player player);
}
