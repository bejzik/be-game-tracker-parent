package com.numarics.game.controller;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.numarics.commons.exception.GameTrackerException;
import com.numarics.game.dto.GamePlayerResponse;
import com.numarics.game.dto.PlayGameRequest;
import com.numarics.game.dto.UpdateGameRequest;
import com.numarics.game.model.Game;
import com.numarics.game.model.GameStatus;
import com.numarics.game.service.IGameService;

import static com.numarics.commons.exception.GameTrackerError.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GameController.class)
public class GameControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private IGameService gameService;

  // POST /game/playGame

  @DisplayName("GIVEN gameName and playerName WHEN playGame is called THEN register player for new game")
  @Test
  void postPlayGame_registerPlayer() throws Exception {
    var request = PlayGameRequest.builder().gameName("Tetris").playerName("John").build();
    var response = GamePlayerResponse.builder()
            .playerName(request.getPlayerName())
            .name(request.getGameName())
            .id(UUID.randomUUID())
            .status(GameStatus.NEW)
            .playerId(UUID.randomUUID())
            .createdAt(OffsetDateTime.now())
            .updatedAt(OffsetDateTime.now()).build();

    when(gameService.playGame(any())).thenReturn(response);

    this.mockMvc.perform(post(GameController.GAME_CONTROLLER_URL + "/play")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").isNotEmpty())
            .andExpect(jsonPath("$.name").value(request.getGameName()))
            .andExpect(jsonPath("$.playerName").value(request.getPlayerName()));
  }

  @DisplayName("GIVEN gameName and playerId WHEN playGame is called THEN update player with game id")
  @Test
  void postPlayGame_registeredPlayer() throws Exception {
    var request = PlayGameRequest.builder().gameName("Tetris").playerId(UUID.randomUUID()).build();
    var response = GamePlayerResponse.builder()
            .playerName("John")
            .name(request.getGameName())
            .id(UUID.randomUUID())
            .status(GameStatus.NEW)
            .playerId(request.getPlayerId())
            .createdAt(OffsetDateTime.now())
            .updatedAt(OffsetDateTime.now()).build();

    when(gameService.playGame(any())).thenReturn(response);

    this.mockMvc.perform(post(GameController.GAME_CONTROLLER_URL + "/play")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").isNotEmpty())
            .andExpect(jsonPath("$.name").value(request.getGameName()));
  }

  @DisplayName("GIVEN player is already registered for another game WHEN playGame is called THEN expect bad request")
  @Test
  void postPlayGame_playerRegisteredToAnotherGame() throws Exception {
    var request = PlayGameRequest.builder().gameName("Tetris").playerId(UUID.randomUUID()).build();

    when(gameService.playGame(any())).thenThrow(new GameTrackerException(GAME_SERVICE_PLAYER_REGISTERED_FOR_ANOTHER_GAME));

    this.mockMvc.perform(post(GameController.GAME_CONTROLLER_URL + "/play")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value(GAME_SERVICE_PLAYER_REGISTERED_FOR_ANOTHER_GAME.code()));
  }

  // GET /game/{{gameId}}

  @DisplayName("GIVEN game id that does not exist WHEN getGame is called THEN expect not found")
  @Test
  void getGame_notFound() throws Exception {
    when(gameService.getGame(any())).thenThrow(new GameTrackerException(GAME_SERVICE_GAME_NOT_FOUND));

    this.mockMvc.perform(get(GameController.GAME_CONTROLLER_URL + "/{gameId}", UUID.randomUUID().toString()))
            .andDo(print())
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.code").value(GAME_SERVICE_GAME_NOT_FOUND.code()));
  }

  @DisplayName("GIVEN game id WHEN getGame is called THEN expect game is returned")
  @Test
  void getGame() throws Exception {
    var game = getNewGame("Tetris");
    when(gameService.getGame(any())).thenReturn(game);

    this.mockMvc.perform(get(GameController.GAME_CONTROLLER_URL + "/{gameId}", UUID.randomUUID().toString()))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").isNotEmpty())
            .andExpect(jsonPath("$.name").value(game.getName()));
  }

  // PUT /game/play
  @DisplayName("GIVEN gameId and not allowed new status WHEN updateGame is called THEN expect bad request")
  @Test
  void updateGame_notAllowedTransition() throws Exception {
    var request = UpdateGameRequest.builder().gameId(UUID.randomUUID()).status(GameStatus.NEW).build();
    when(gameService.updateGame(any())).thenThrow(new GameTrackerException(GAME_SERVICE_GAME_COULD_NOT_BE_UPDATED));

    this.mockMvc.perform(put(GameController.GAME_CONTROLLER_URL + "/play")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.code").value(GAME_SERVICE_GAME_COULD_NOT_BE_UPDATED.code()));
  }

  @DisplayName("GIVEN gameId and new status WHEN updateGame is called THEN expect game is updated")
  @Test
  void updateGame() throws Exception {
    var game = getNewGame("Tic-Tac-Toe");
    var request = UpdateGameRequest.builder().gameId(game.getId()).status(GameStatus.FINISHED).build();
    game.setStatus(GameStatus.FINISHED);
    when(gameService.updateGame(any())).thenReturn(game);

    this.mockMvc.perform(put(GameController.GAME_CONTROLLER_URL + "/play")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value(GameStatus.FINISHED.toString()));
  }


  // DELETE /game/{{gameId}}
  @DisplayName("GIVEN game id that does not exist WHEN deleteGame is called THEN expect not found")
  @Test
  void deleteGame_notFound() throws Exception {
    when(gameService.deleteGame(any())).thenThrow(new GameTrackerException(GAME_SERVICE_GAME_NOT_FOUND));

    this.mockMvc.perform(delete(GameController.GAME_CONTROLLER_URL + "/{gameId}", UUID.randomUUID().toString()))
            .andDo(print())
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.code").value(GAME_SERVICE_GAME_NOT_FOUND.code()));
  }

  @DisplayName("GIVEN game id WHEN deleteGame is called THEN expect game is dropped")
  @Test
  void deleteGame() throws Exception {
    var game = getNewGame("Tetris");
    game.setStatus(GameStatus.DROPPED);
    when(gameService.deleteGame(any())).thenReturn(game);

    this.mockMvc.perform(delete(GameController.GAME_CONTROLLER_URL + "/{gameId}", UUID.randomUUID().toString()))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value(GameStatus.DROPPED.toString()));
  }

  // GET /game/search

  @DisplayName("GIVEN nothing WHEN searchGames is called THEN expect all games are returned")
  @Test
  void searchGames_noFilter() throws Exception {
    var game1 = GamePlayerResponse.builder()
                    .id(UUID.randomUUID())
            .name("Tetris").status(GameStatus.NEW).createdAt(OffsetDateTime.now()).updatedAt(OffsetDateTime.now())
            .playerId(UUID.randomUUID()).playerName("John").build();
    var game2 = GamePlayerResponse.builder()
            .id(UUID.randomUUID())
            .name("Tic-Tac-Toe").status(GameStatus.NEW).createdAt(OffsetDateTime.now()).updatedAt(OffsetDateTime.now())
            .playerId(UUID.randomUUID()).playerName("Mike").build();
    var pagedResponse = new PageImpl<>(Arrays.asList(game1, game2));
    when(gameService.searchGames(any(), any(), any(), any())).thenReturn(pagedResponse);

    this.mockMvc.perform(get(GameController.GAME_CONTROLLER_URL + "/search"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.empty").value(false))
            .andExpect(jsonPath("$.numberOfElements").value(2));
  }

  @DisplayName("GIVEN filters WHEN searchGames is called THEN expect games filtered are returned")
  @Test
  void searchGames_withFilter() throws Exception {
    var pageable = PageRequest.of(0, 8, Sort.by("name").ascending());
    var gameName = "T";
    var status = GameStatus.NEW;
    var playerName = "I";
    var game1 = GamePlayerResponse.builder()
            .id(UUID.randomUUID())
            .name("Tetris").status(GameStatus.NEW).createdAt(OffsetDateTime.now()).updatedAt(OffsetDateTime.now())
            .playerId(UUID.randomUUID()).playerName("John").build();
    var game2 = GamePlayerResponse.builder()
            .id(UUID.randomUUID())
            .name("Tic-Tac-Toe").status(GameStatus.NEW).createdAt(OffsetDateTime.now()).updatedAt(OffsetDateTime.now())
            .playerId(UUID.randomUUID()).playerName("Mike").build();
    var pagedResponse = new PageImpl<>(Arrays.asList(game1, game2), pageable, 2);
    when(gameService.searchGames(gameName, status, playerName, pageable)).thenReturn(pagedResponse);

    this.mockMvc.perform(get(GameController.GAME_CONTROLLER_URL + "/search")
                    .param("gameName", gameName)
                    .param("playerName", playerName)
                    .param("status", status.toString()))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.empty").value(false))
            .andExpect(jsonPath("$.numberOfElements").value(2));
  }

  private Game getNewGame(String name) {
    return Game.builder()
            .id(UUID.randomUUID())
            .name(name)
            .status(GameStatus.NEW)
            .createdAt(OffsetDateTime.now())
            .updatedAt(OffsetDateTime.now()).build();
  }
}
