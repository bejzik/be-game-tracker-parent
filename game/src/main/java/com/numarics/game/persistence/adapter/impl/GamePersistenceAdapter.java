package com.numarics.game.persistence.adapter.impl;

import java.util.UUID;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.numarics.game.model.Game;
import com.numarics.game.model.GameStatus;
import com.numarics.game.persistence.adapter.IGamePersistence;
import com.numarics.game.persistence.entity.GameEntity;
import com.numarics.game.persistence.repository.GameRepository;

import static com.numarics.game.persistence.repository.GameSpecification.searchGamesByCriteria;

@Service
public class GamePersistenceAdapter implements IGamePersistence {

  private final GameRepository gameRepository;
  private final ModelMapper modelMapper;

  public GamePersistenceAdapter(GameRepository gameRepository, ModelMapper modelMapper) {
    this.gameRepository = gameRepository;
    this.modelMapper = modelMapper;
  }

  @Override
  public Game saveGame(Game game) {
    return mapToDomain(gameRepository.save(mapToEntity(game)));
  }

  @Override
  public Game getGameById(UUID gameId) {
    return gameRepository.findById(gameId)
            .map(this::mapToDomain).orElse(null);
  }

  @Override
  public void deleteGameById(UUID gameId) {
    gameRepository.deleteById(gameId);
  }

  @Override
  public Page<Game> findByGameNameAndStatus(String name, GameStatus status, Pageable pageable) {
    return gameRepository.findAll(searchGamesByCriteria(name, status), pageable).map(this::mapToDomain);
  }

  private GameEntity mapToEntity(Game game) {
    return modelMapper.map(game, GameEntity.class);
  }

  private Game mapToDomain(GameEntity entity) {
    return modelMapper.map(entity, Game.class);
  }

}
