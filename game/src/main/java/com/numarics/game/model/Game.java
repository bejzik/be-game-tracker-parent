package com.numarics.game.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Game {
  private UUID id;
  private String name;
  private GameStatus status;
  private OffsetDateTime createdAt;
  private OffsetDateTime updatedAt;
}
