package com.numarics.player.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PlayerRegistrationRequest {
  private UUID id;
  private UUID gameId;
  @NotNull(message = "Missing playerName")
  private String playerName;
}
