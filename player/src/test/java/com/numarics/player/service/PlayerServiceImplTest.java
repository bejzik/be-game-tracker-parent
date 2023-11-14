package com.numarics.player.service;

import java.util.Arrays;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import com.numarics.commons.exception.GameTrackerException;
import com.numarics.player.dto.PlayerRegistrationRequest;
import com.numarics.player.model.Player;
import com.numarics.player.persistence.adapter.IPlayerPersistence;
import com.numarics.player.service.impl.PlayerServiceImpl;

import static com.numarics.commons.exception.GameTrackerError.PLAYER_SERVICE_MISSING_DATA;
import static com.numarics.commons.exception.GameTrackerError.PLAYER_SERVICE_PLAYER_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PlayerServiceImplTest {

  @Mock
  private IPlayerPersistence persistence;

  @Mock
  private ModelMapper modelMapper;

  @InjectMocks
  private PlayerServiceImpl playerService;

  @DisplayName("GIVEN request is null WHEN registerPlayer is called THEN throw Exception")
  @Test
  void registerPlayer_requestNull() {
    var exception = assertThrows(GameTrackerException.class,
            () -> playerService.registerPlayer(null));
    assertEquals(exception.getError().code(), PLAYER_SERVICE_MISSING_DATA.code());
  }

  @DisplayName("GIVEN playerName is not sent WHEN registerPlayer is called THEN throw Exception")
  @Test
  void registerPlayer_missingPlayerName() {
    // given
    var request = PlayerRegistrationRequest.builder().build();

    // when
    var exception = assertThrows(GameTrackerException.class,
            () -> playerService.registerPlayer(request));
    assertEquals(exception.getError().code(), PLAYER_SERVICE_MISSING_DATA.code());
  }

  @DisplayName("GIVEN playerName only WHEN registerPlayer is called THEN create new player without the game")
  @Test
  void registerPlayer_noGame() {
    // given
    var request = PlayerRegistrationRequest.builder().playerName("test").build();
    var player = new Player(UUID.randomUUID(), "test", null);
    when(persistence.registerPlayer(any())).thenReturn(player);

    // when
    var response = playerService.registerPlayer(request);

    // then
    assertNotNull(response);
    assertEquals(response.getName(), player.getName());
    assertEquals(response.getGameId(), player.getGameId());
  }

  @DisplayName("GIVEN playerName and gameId WHEN registerPlayer is called THEN create new player for the game")
  @Test
  void registerPlayer() {
    // given
    var request = PlayerRegistrationRequest.builder().gameId(UUID.randomUUID()).playerName("test").build();
    var player = new Player(UUID.randomUUID(), "test", request.getGameId());
    when(persistence.registerPlayer(any())).thenReturn(player);

    // when
    var response = playerService.registerPlayer(request);

    // then
    assertNotNull(response);
    assertEquals(response.getName(), player.getName());
    assertEquals(response.getGameId(), player.getGameId());
  }

  @DisplayName("GIVEN playerId and gameId WHEN registerPlayer is called THEN register existing player for the game")
  @Test
  void registerPlayer_playerRegistered() {
    // given
    var request = PlayerRegistrationRequest.builder().id(UUID.randomUUID()).gameId(UUID.randomUUID()).playerName("test").build();
    var player = new Player(request.getId(), request.getPlayerName(), request.getGameId());
    when(persistence.registerPlayer(any())).thenReturn(player);

    // when
    var response = playerService.registerPlayer(request);

    // then
    assertNotNull(response);
    assertEquals(response.getName(), player.getName());
    assertEquals(response.getGameId(), player.getGameId());
  }

  @DisplayName("GIVEN player id is not sent WHEN getPlayer is called THEN throw Exception")
  @Test
  void getPlayer_missingPlayerId() {
    var exception = assertThrows(GameTrackerException.class,
            () -> playerService.getPlayer(null));
    assertEquals(exception.getError().code(), PLAYER_SERVICE_MISSING_DATA.code());
  }

  @DisplayName("GIVEN player id that does not exist WHEN getPlayer is called THEN throw Exception")
  @Test
  void getPlayer_playerNotFound() {
    var exception = assertThrows(GameTrackerException.class,
            () -> playerService.getPlayer(UUID.randomUUID()));
    assertEquals(exception.getError().code(), PLAYER_SERVICE_PLAYER_NOT_FOUND.code());
  }

  @DisplayName("GIVEN player id WHEN getPlayer is called THEN return player")
  @Test
  void getPlayer() {
    // given
    var player = new Player(UUID.randomUUID(), "test", UUID.randomUUID());
    when(persistence.getPlayerById(any())).thenReturn(player);

    // when
    var response = playerService.getPlayer(player.getId());

    // then
    assertNotNull(response);
    assertEquals(response.getName(), player.getName());
    assertEquals(response.getGameId(), player.getGameId());
  }

  @DisplayName("GIVEN player id is not sent WHEN deletePlayer is called THEN throw Exception")
  @Test
  void deletePlayer_missingPlayerId() {
    var exception = assertThrows(GameTrackerException.class,
            () -> playerService.deletePlayer(null));
    assertEquals(exception.getError().code(), PLAYER_SERVICE_MISSING_DATA.code());
  }

  @DisplayName("GIVEN player id that does not exist WHEN deletePlayer is called THEN throw Exception")
  @Test
  void deletePlayer_playerNotFound() {
    var exception = assertThrows(GameTrackerException.class,
            () -> playerService.getPlayer(UUID.randomUUID()));
    assertEquals(exception.getError().code(), PLAYER_SERVICE_PLAYER_NOT_FOUND.code());
  }

  @DisplayName("GIVEN player id WHEN deletePlayer is called THEN player is deleted")
  @Test
  void deletePlayer() {
    // given
    var player = new Player(UUID.randomUUID(), "test", UUID.randomUUID());
    doNothing().when(persistence).deletePlayerById(any());
    when(persistence.getPlayerById(any())).thenReturn(player);
    // when
    playerService.deletePlayer(player.getId());
  }

  @DisplayName("GIVEN nothing WHEN searchPlayers is called THEN expect all players are returned")
  @Test
  void searchPlayers_noFilter() throws Exception {
    var player1 = Player.builder().id(UUID.randomUUID()).name("Mike").gameId(UUID.randomUUID()).build();
    var player2 = Player.builder().id(UUID.randomUUID()).name("John").gameId(UUID.randomUUID()).build();
    var list = Arrays.asList(player1, player2);
    when(persistence.findPlayersByNameContaining(null)).thenReturn(list);

    var actualList = playerService.searchPlayers(null);

    assertNotNull(actualList);
    assertFalse(actualList.isEmpty());
    assertEquals(2, actualList.size());
  }

  @DisplayName("GIVEN name WHEN searchPlayers is called THEN expect players fltered by name are returned")
  @Test
  void searchPlayers_withFilter() throws Exception {
    var player1 = Player.builder().id(UUID.randomUUID()).name("Mike").gameId(UUID.randomUUID()).build();
    var player2 = Player.builder().id(UUID.randomUUID()).name("Mike2").gameId(UUID.randomUUID()).build();
    var list = Arrays.asList(player1, player2);
    when(playerService.searchPlayers(any())).thenReturn(list);

    var actualList = playerService.searchPlayers(null);

    assertNotNull(actualList);
    assertFalse(actualList.isEmpty());
    assertEquals(2, actualList.size());
  }
}
