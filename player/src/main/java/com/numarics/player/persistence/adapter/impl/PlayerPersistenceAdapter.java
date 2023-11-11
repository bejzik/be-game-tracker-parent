package com.numarics.player.persistence.adapter.impl;

import java.util.UUID;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import com.numarics.player.model.Player;
import com.numarics.player.persistence.adapter.IPlayerPersistence;
import com.numarics.player.persistence.entity.PlayerEntity;
import com.numarics.player.persistence.repository.PlayerRepository;

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

  @Override
  public Player getPlayerById(UUID playerId) {
    return playerRepository.findById(playerId)
            .map(this::mapToDomain).orElse(null);
  }

  @Override
  public void deletePlayerById(UUID playerId) {
    playerRepository.deleteById(playerId);
  }

  private PlayerEntity mapToEntity(Player player) {
    return modelMapper.map(player, PlayerEntity.class);
  }

  private Player mapToDomain(PlayerEntity entity) {
    return modelMapper.map(entity, Player.class);
  }
}
