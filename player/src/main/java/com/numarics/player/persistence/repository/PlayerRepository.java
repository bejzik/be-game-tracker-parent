package com.numarics.player.persistence.repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import com.numarics.player.persistence.entity.PlayerEntity;

public interface PlayerRepository extends JpaRepository<PlayerEntity, UUID> {
}
