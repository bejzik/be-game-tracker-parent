package com.numarics.playerservice.service;

import com.numarics.playerservice.dto.PlayerRegistrationRequest;
import com.numarics.playerservice.model.Player;

public interface IPlayerService {

  Player registerPlayer(PlayerRegistrationRequest request);
}
