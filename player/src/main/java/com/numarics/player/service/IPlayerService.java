package com.numarics.player.service;

import com.numarics.player.dto.PlayerRegistrationRequest;
import com.numarics.player.model.Player;

public interface IPlayerService {

  Player registerPlayer(PlayerRegistrationRequest request);
}
