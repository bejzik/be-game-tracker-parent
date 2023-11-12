package com.numarics.game.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;
import com.numarics.game.model.GameStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlayGameRequest {
  private UUID playerId;
  private UUID gameId;
  private GameStatus status;
}
