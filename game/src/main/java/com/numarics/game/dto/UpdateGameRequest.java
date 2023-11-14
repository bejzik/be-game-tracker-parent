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
public class UpdateGameRequest {
  @NotNull(message = "Missing playerId")
  private UUID playerId;
  @NotNull(message = "Missing gameId")
  private UUID gameId;
  @NotNull(message = "Missing status")
  private GameStatus status;
}
