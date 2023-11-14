package com.numarics.game.service;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import com.numarics.commons.exception.GameTrackerException;
import com.numarics.game.client.PlayerServiceApi;
import com.numarics.game.client.model.Player;
import com.numarics.game.dto.PlayGameRequest;
import com.numarics.game.dto.UpdateGameRequest;
import com.numarics.game.model.Game;
import com.numarics.game.model.GameStatus;
import com.numarics.game.persistence.adapter.IGamePersistence;
import com.numarics.game.service.impl.GameServiceImpl;

import static com.numarics.commons.exception.GameTrackerError.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GameServiceImplTest {

  @Mock
  IGamePersistence persistence;

  @Spy
  ModelMapper modelMapper;

  @Mock
  PlayerServiceApi playerServiceApi;

  @InjectMocks
  private GameServiceImpl gameService;

  @DisplayName("GIVEN request is null WHEN playGame is called THEN throw Exception")
  @Test
  void playGame_requestNull() {
    var exception = assertThrows(GameTrackerException.class,
            () -> gameService.playGame(null));
    assertEquals(exception.getError().code(), GAME_SERVICE_MISSING_DATA.code());
  }

  @DisplayName("GIVEN player is not registered WHEN playGame is called THEN register player and create game")
  @Test
  void playGame_registerPlayer() {
    // given
    var request = PlayGameRequest.builder().gameName("Tetris").playerName("Mike").build();

    var game = Game.builder()
            .id(UUID.randomUUID())
            .name(request.getGameName())
            .status(GameStatus.NEW)
            .createdAt(OffsetDateTime.now())
            .updatedAt(OffsetDateTime.now()).build();
    var player = Player.builder().id(UUID.randomUUID()).name(request.getPlayerName()).gameId(game.getId()).build();
    when(persistence.saveGame(any())).thenReturn(game);
    when(playerServiceApi.registerPlayer(
            any()))
            .thenReturn(player);

    // when
    var response = gameService.playGame(request);

    assertNotNull(response);
    assertEquals(response.getName(), game.getName());
    assertEquals(response.getPlayerName(), player.getName());
  }

  @DisplayName("GIVEN player is registered WHEN playGame is called THEN register player for newly created game")
  @Test
  void playGame_alreadyRegisteredPlayer() {
    // given
    var request = PlayGameRequest.builder().gameName("Tetris").playerId(UUID.randomUUID()).build();

    var game = Game.builder()
            .id(UUID.randomUUID())
            .name(request.getGameName())
            .status(GameStatus.NEW)
            .createdAt(OffsetDateTime.now())
            .updatedAt(OffsetDateTime.now()).build();
    var player = Player.builder().id(UUID.randomUUID()).name("Test").gameId(null).build();
    when(persistence.saveGame(any())).thenReturn(game);
    when(playerServiceApi.getPlayerById(
            any()))
            .thenReturn(player);

    var registeredPlayer = Player.builder().id(player.getId()).name(player.getName()).gameId(game.getId()).build();
    when(playerServiceApi.registerPlayer(
            any()))
            .thenReturn(registeredPlayer);

    // when
    var response = gameService.playGame(request);

    assertNotNull(response);
    assertEquals(response.getName(), game.getName());
    assertEquals(response.getPlayerName(), player.getName());
  }

  @DisplayName("GIVEN player is registered to another game WHEN playGame is called THEN register player throws error")
  @Test
  void playGame_alreadyRegisteredPlayerToAnotherGame() {
    // given
    var request = PlayGameRequest.builder().gameName("Tetris").playerId(UUID.randomUUID()).build();

    var game = getNewGame(request.getGameName());

    var player = Player.builder().id(UUID.randomUUID()).name("Test").gameId(UUID.randomUUID()).build();
    when(persistence.saveGame(any())).thenReturn(game);
    when(playerServiceApi.getPlayerById(
            any()))
            .thenReturn(player);

    // when
    var exception = assertThrows(GameTrackerException.class,
            () -> gameService.playGame(request));
    assertEquals(exception.getError().code(), GAME_SERVICE_PLAYER_REGISTERED_FOR_ANOTHER_GAME.code());
  }

  @DisplayName("GIVEN game id that does not exist WHEN getGame is called THEN throw Exception")
  @Test
  void getGame_gameNotFound() {
    when(persistence.getGameById(any())).thenReturn(null);
    var exception = assertThrows(GameTrackerException.class,
            () -> gameService.getGame(UUID.randomUUID()));
    assertEquals(exception.getError().code(), GAME_SERVICE_GAME_NOT_FOUND.code());
  }

  @DisplayName("GIVEN game id WHEN getGame is called THEN return game")
  @Test
  void getGame() {
    var game = getNewGame("Tetris");
    when(persistence.getGameById(any())).thenReturn(game);
    var response = gameService.getGame(UUID.randomUUID());
    assertNotNull(response);
    assertEquals(response.getName(), game.getName());
  }

  @DisplayName("GIVEN game id that does not exist WHEN updateGame is called THEN throw error")
  @Test
  void updateGame_notFound() {
    when(persistence.getGameById(any())).thenReturn(null);
    var request = UpdateGameRequest.builder().gameId(UUID.randomUUID()).status(GameStatus.FINISHED).build();
    var exception = assertThrows(GameTrackerException.class,
            () -> gameService.updateGame(request));
    assertEquals(exception.getError().code(), GAME_SERVICE_GAME_NOT_FOUND.code());
  }

  @DisplayName("GIVEN game when transition is not allowed WHEN updateGame is called THEN throw error")
  @Test
  void updateGame_notAllowedTransition() {
    when(persistence.getGameById(any())).thenReturn(getNewGame("Tetris"));
    var request = UpdateGameRequest.builder().gameId(UUID.randomUUID()).status(GameStatus.NEW).build();
    var exception = assertThrows(GameTrackerException.class,
            () -> gameService.updateGame(request));
    assertEquals(exception.getError().code(), GAME_SERVICE_GAME_COULD_NOT_BE_UPDATED.code());
  }

  @DisplayName("GIVEN game id WHEN updateGame is called THEN return updated game")
  @Test
  void updateGame() {
    var game = getNewGame("Tetris");
    when(persistence.getGameById(any())).thenReturn(game);

    var updatedGame = getNewGame("Tetris");
    updatedGame.setStatus(GameStatus.FINISHED);
    when(persistence.saveGame(any())).thenReturn(updatedGame);

    var request = UpdateGameRequest.builder().gameId(UUID.randomUUID()).status(GameStatus.FINISHED).build();
    var response = gameService.updateGame(request);
    assertNotNull(response);
    assertEquals(response.getStatus(), GameStatus.FINISHED);
  }

  @DisplayName("GIVEN game id that does not exist WHEN deleteGame is called THEN throw error")
  @Test
  void deleteGame_notFound() {
    when(persistence.getGameById(any())).thenReturn(null);
    var exception = assertThrows(GameTrackerException.class,
            () -> gameService.deleteGame(UUID.randomUUID()));
    assertEquals(exception.getError().code(), GAME_SERVICE_GAME_NOT_FOUND.code());
  }

  @DisplayName("GIVEN game id WHEN deleteGame is called THEN delete game")
  @Test
  void deleteGame() {
    var game = getNewGame("Tetris");
    when(persistence.getGameById(any())).thenReturn(game);

    when(persistence.saveGame(any())).thenReturn(
            Game.builder().id(game.getId())
                    .name(game.getName())
                    .updatedAt(OffsetDateTime.now())
                    .createdAt(game.getCreatedAt())
                    .status(GameStatus.DROPPED).build());

    var actual = gameService.deleteGame(UUID.randomUUID());

    assertNotNull(actual);
    assertEquals(actual.getStatus(), GameStatus.DROPPED);
  }

  @DisplayName("GIVEN game name, status and playerName WHEN searchGames is called THEN return games paginated")
  @Test
  void searchGames() {
    var pageable = PageRequest.of(0, 2);
    var gameName = "tetris";
    var status = GameStatus.NEW;
    var playerName = "Player";

    var game1 = getNewGame("Tetris");
    var game2 = getNewGame("Tetris2");
    var games = Arrays.asList(game1, game2);
    var pagedResponse = new PageImpl<>(games);

    var player1 = Player.builder().id(UUID.randomUUID()).name("Player1").gameId(game1.getId()).build();
    var player2 = Player.builder().id(UUID.randomUUID()).name("Player2").gameId(game2.getId()).build();
    var players = Arrays.asList(player1, player2);

    when(persistence.findByGameNameAndStatus(gameName, status, pageable)).thenReturn(pagedResponse);
    when(playerServiceApi.searchPlayers(playerName)).thenReturn(players);

    var actualResponse = gameService.searchGames(gameName, GameStatus.NEW, playerName, pageable);

    assertNotNull(actualResponse);
    assertEquals(actualResponse.stream().count(), 2);
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
