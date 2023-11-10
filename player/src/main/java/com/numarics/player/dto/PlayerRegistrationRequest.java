package com.numarics.player.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PlayerRegistrationRequest {
  @NotNull(message = "Missing player name")
  private String name;
  @NotNull(message = "Missing game id")
  private String gameId;
}
