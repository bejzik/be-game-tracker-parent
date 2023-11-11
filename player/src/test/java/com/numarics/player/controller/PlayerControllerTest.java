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

import static com.numarics.player.exception.GameTrackerError.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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

  @DisplayName("GIVEN game id is not sent WHEN register is called THEN expect bad request")
  @Test
  void registerPlayerWithoutGameId() throws Exception {
    var request = PlayerRegistrationRequest.builder().build();

    when(playerService.registerPlayer(any())).thenThrow(new GameTrackerException(PLAYER_SERVICE_VALIDATION_FIELDS));

    this.mockMvc.perform(post(PlayerController.PLAYER_CONTROLLER_URL + "/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value(PLAYER_SERVICE_VALIDATION_FIELDS.code()));
  }

  @DisplayName("GIVEN game id WHEN register is called THEN player is registered")
  @Test
  void registerPlayer() throws Exception {
    var gameId = UUID.randomUUID();
    var request = PlayerRegistrationRequest.builder().gameId(gameId).build();
    var player = Player.builder().id(UUID.randomUUID()).name("test").gameId(gameId).build();

    when(playerService.registerPlayer(any())).thenReturn(player);

    this.mockMvc.perform(post(PlayerController.PLAYER_CONTROLLER_URL + "/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").isNotEmpty())
            .andExpect(jsonPath("$.gameId").isNotEmpty());
  }

  @DisplayName("GIVEN player id that does not exist WHEN getPlayer is called THEN expect not found")
  @Test
  void getPlayer_notFound() throws Exception {
    when(playerService.getPlayer(any())).thenThrow(new GameTrackerException(PLAYER_SERVICE_PLAYER_NOT_FOUND));

    this.mockMvc.perform(get(PlayerController.PLAYER_CONTROLLER_URL)
                    .param("playerId", UUID.randomUUID().toString()))
            .andDo(print())
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.code").value(PLAYER_SERVICE_PLAYER_NOT_FOUND.code()));
  }

  @DisplayName("GIVEN player id WHEN getPlayer is called THEN expect player is returned")
  @Test
  void getPlayer() throws Exception {
    var player = Player.builder().id(UUID.randomUUID()).name("test").gameId(UUID.randomUUID()).build();
    when(playerService.getPlayer(any())).thenReturn(player);

    this.mockMvc.perform(get(PlayerController.PLAYER_CONTROLLER_URL)
                    .param("playerId", UUID.randomUUID().toString()))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").isNotEmpty())
            .andExpect(jsonPath("$.name").value(player.getName()))
            .andExpect(jsonPath("$.gameId").isNotEmpty());
  }

  @DisplayName("GIVEN player id that does not exist WHEN delete player is called THEN expect not found")
  @Test
  void deletePlayer_notFoundPlayer() throws Exception {
    doThrow(new GameTrackerException(PLAYER_SERVICE_PLAYER_NOT_FOUND)).when(playerService).deletePlayer(any());

    this.mockMvc.perform(delete(PlayerController.PLAYER_CONTROLLER_URL)
                    .param("playerId", UUID.randomUUID().toString()))
            .andDo(print())
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.code").value(PLAYER_SERVICE_PLAYER_NOT_FOUND.code()));
  }

  @DisplayName("GIVEN player id WHEN deletePlayer is called THEN expect player is deleted")
  @Test
  void deletePlayer() throws Exception {
    doNothing().when(playerService).deletePlayer(any());
    this.mockMvc.perform(get(PlayerController.PLAYER_CONTROLLER_URL)
                    .param("playerId", UUID.randomUUID().toString()))
            .andDo(print())
            .andExpect(status().isOk());
  }
}
