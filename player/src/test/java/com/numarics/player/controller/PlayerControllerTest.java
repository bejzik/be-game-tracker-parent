package com.numarics.player.controller;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.numarics.commons.exception.GameTrackerException;
import com.numarics.player.dto.PlayerRegistrationRequest;
import com.numarics.player.model.Player;
import com.numarics.player.service.IPlayerService;

import static com.numarics.commons.exception.GameTrackerError.GAME_TRACKER_VALIDATION_FIELDS;
import static com.numarics.commons.exception.GameTrackerError.PLAYER_SERVICE_PLAYER_NOT_FOUND;
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

  @DisplayName("GIVEN playerName is not sent WHEN register is called THEN expect bad request")
  @Test
  void registerPlayerWithoutGameId() throws Exception {
    var request = PlayerRegistrationRequest.builder().build();

    when(playerService.registerPlayer(any())).thenThrow(new GameTrackerException(GAME_TRACKER_VALIDATION_FIELDS));

    this.mockMvc.perform(post(PlayerController.PLAYER_CONTROLLER_URL + "/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value(GAME_TRACKER_VALIDATION_FIELDS.code()));
  }

  @DisplayName("GIVEN playerName and gameId WHEN register is called THEN new player is registered")
  @Test
  void registerPlayer() throws Exception {
    var gameId = UUID.randomUUID();
    var request = PlayerRegistrationRequest.builder().gameId(gameId).playerName(UUID.randomUUID().toString()).build();
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

  @DisplayName("GIVEN playerId, playerName and gameId WHEN register is called THEN existing player is registered")
  @Test
  void registerPlayer_existing() throws Exception {
    var gameId = UUID.randomUUID();
    var playerId = UUID.randomUUID();
    var request = PlayerRegistrationRequest.builder().id(playerId).gameId(gameId).playerName(UUID.randomUUID().toString()).build();
    var player = Player.builder().id(playerId).name("test").gameId(gameId).build();

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

    this.mockMvc.perform(get(PlayerController.PLAYER_CONTROLLER_URL + "/{playerId}", UUID.randomUUID().toString()))
            .andDo(print())
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.code").value(PLAYER_SERVICE_PLAYER_NOT_FOUND.code()));
  }

  @DisplayName("GIVEN player id WHEN getPlayer is called THEN expect player is returned")
  @Test
  void getPlayer() throws Exception {
    var player = Player.builder().id(UUID.randomUUID()).name("test").gameId(UUID.randomUUID()).build();
    when(playerService.getPlayer(any())).thenReturn(player);

    this.mockMvc.perform(get(PlayerController.PLAYER_CONTROLLER_URL + "/{playerId}", UUID.randomUUID().toString()))
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

    this.mockMvc.perform(delete(PlayerController.PLAYER_CONTROLLER_URL + "/{playerId}", UUID.randomUUID().toString()))
            .andDo(print())
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.code").value(PLAYER_SERVICE_PLAYER_NOT_FOUND.code()));
  }

  @DisplayName("GIVEN player id WHEN deletePlayer is called THEN expect player is deleted")
  @Test
  void deletePlayer() throws Exception {
    doNothing().when(playerService).deletePlayer(any());
    this.mockMvc.perform(get(PlayerController.PLAYER_CONTROLLER_URL + "/{playerId}", UUID.randomUUID().toString()))
            .andDo(print())
            .andExpect(status().isOk());
  }

  @DisplayName("GIVEN nothing WHEN searchPlayers is called THEN expect all players are returned")
  @Test
  void searchPlayers_noFilter() throws Exception {
    var player1 = Player.builder().id(UUID.randomUUID()).name("Mike").gameId(UUID.randomUUID()).build();
    var player2 = Player.builder().id(UUID.randomUUID()).name("John").gameId(UUID.randomUUID()).build();
    var list = Arrays.asList(player1, player2);
    when(playerService.searchPlayers(null)).thenReturn(list);

    this.mockMvc.perform(get(PlayerController.PLAYER_CONTROLLER_URL + "/search"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2));
  }

  @DisplayName("GIVEN name WHEN searchPlayers is called THEN expect players fltered by name are returned")
  @Test
  void searchPlayers_withFilter() throws Exception {
    var player1 = Player.builder().id(UUID.randomUUID()).name("Mike").gameId(UUID.randomUUID()).build();
    var player2 = Player.builder().id(UUID.randomUUID()).name("Mike2").gameId(UUID.randomUUID()).build();
    var list = Arrays.asList(player1, player2);
    when(playerService.searchPlayers(any())).thenReturn(list);

    this.mockMvc.perform(get(PlayerController.PLAYER_CONTROLLER_URL + "/search").param("name", "Mike"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2));
  }
}
