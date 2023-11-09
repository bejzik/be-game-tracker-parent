package com.numarics.playerservice.persistence.adapter.impl;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import com.numarics.playerservice.model.Player;
import com.numarics.playerservice.persistence.adapter.IPlayerPersistence;
import com.numarics.playerservice.persistence.entity.PlayerEntity;
import com.numarics.playerservice.persistence.repository.PlayerRepository;

@Service
public class PlayerPersistenceAdapter implements IPlayerPersistence {

  private final PlayerRepository playerRepository;
  private final ModelMapper modelMapper;

  public PlayerPersistenceAdapter(PlayerRepository playerRepository, ModelMapper modelMapper) {
    this.playerRepository = playerRepository;
    this.modelMapper = modelMapper;
  }

  @Override
  public Player registerPlayer(Player player) {
    return mapToDomain(playerRepository.save(mapToEntity(player)));
  }

  private PlayerEntity mapToEntity(Player player) {
    return modelMapper.map(player, PlayerEntity.class);
  }

  private Player mapToDomain(PlayerEntity entity) {
    return modelMapper.map(entity, Player.class);
  }
}
