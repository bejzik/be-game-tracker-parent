package com.numarics.game.persistence.repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import com.numarics.game.persistence.entity.GameEntity;

public interface GameRepository extends JpaRepository<GameEntity, UUID> {
}
