package com.numarics.player.controller;

import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.numarics.player.dto.PlayerRegistrationRequest;
import com.numarics.player.exception.GameTrackerException;
import com.numarics.player.model.Player;
import com.numarics.player.service.IPlayerService;

import static com.numarics.player.exception.GameTrackerError.PLAYER_SERVICE_VALIDATION_FIELDS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PlayerController.class)
public class PlayerControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private IPlayerService playerService;

  @DisplayName("GIVEN game without player name WHEN register is called THEN expect bad request")
  @Test
  void registerPlayerWithoutPlayerName() throws Exception {
    var name = "Player 1";
    var request = PlayerRegistrationRequest.builder().name(name).build();

    when(playerService.registerPlayer(any())).thenThrow(new GameTrackerException(PLAYER_SERVICE_VALIDATION_FIELDS));

    this.mockMvc.perform(post(PlayerController.PLAYER_CONTROLLER_URL + "/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value(PLAYER_SERVICE_VALIDATION_FIELDS.code()));
  }

  @DisplayName("GIVEN player name without game id WHEN register is called THEN expect bad request")
  @Test
  void registerPlayerWithoutGameId() throws Exception {
    var name = "Player 1";
    var request = PlayerRegistrationRequest.builder().name(name).build();

    when(playerService.registerPlayer(any())).thenThrow(new GameTrackerException(PLAYER_SERVICE_VALIDATION_FIELDS));

    this.mockMvc.perform(post(PlayerController.PLAYER_CONTROLLER_URL + "/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value(PLAYER_SERVICE_VALIDATION_FIELDS.code()));
  }

  @DisplayName("GIVEN player name and game id WHEN register is called THEN player is registered")
  @Test
  void registerPlayer() throws Exception {
    var name = "Player 1";
    var gameId = "Tic-Tac-Toe";
    var request = PlayerRegistrationRequest.builder().name(name).gameId(gameId).build();
    var player = Player.builder().id(UUID.randomUUID()).name(name).gameId(gameId).build();

    when(playerService.registerPlayer(any())).thenReturn(player);

    this.mockMvc.perform(post(PlayerController.PLAYER_CONTROLLER_URL + "/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value(name))
            .andExpect(jsonPath("$.gameId").value(gameId));
  }
}
