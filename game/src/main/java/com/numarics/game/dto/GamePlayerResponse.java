package com.numarics.game.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;
import com.numarics.game.model.GameStatus;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GamePlayerResponse {
  private UUID id;
  private String name;
  private GameStatus status;
  private OffsetDateTime createdAt;
  private OffsetDateTime updatedAt;
  private UUID playerId;
  private String playerName;
}
