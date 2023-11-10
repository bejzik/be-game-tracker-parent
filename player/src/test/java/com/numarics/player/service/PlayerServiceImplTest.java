package com.numarics.player.service;

import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import com.numarics.player.dto.PlayerRegistrationRequest;
import com.numarics.player.exception.GameTrackerException;
import com.numarics.player.model.Player;
import com.numarics.player.persistence.adapter.IPlayerPersistence;
import com.numarics.player.service.impl.PlayerServiceImpl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PlayerServiceImplTest {

  @Mock
  private IPlayerPersistence persistence;
  @Mock
  private ModelMapper modelMapper;

  @InjectMocks
  private PlayerServiceImpl playerService;

  @DisplayName("GIVEN request is null WHEN register is called THEN throw Exception")
  @Test
  void testPlayerServiceRegister_requestNull() {
    assertThrows(GameTrackerException.class,
            () -> playerService.registerPlayer(null));
  }

  @DisplayName("GIVEN game name WHEN register is called THEN throw Exception")
  @Test
  void testPlayerServiceRegister_missingPlayerName() {
    // given
    var request = new PlayerRegistrationRequest(null, "test");

    // when
    assertThrows(GameTrackerException.class,
            () -> playerService.registerPlayer(request));
  }

  @DisplayName("GIVEN player name WHEN register is called THEN throw Exception")
  @Test
  void testPlayerServiceRegister_missingGame() {
    // given
    var request = new PlayerRegistrationRequest("test", null);

    // when
    assertThrows(GameTrackerException.class,
            () -> playerService.registerPlayer(request));
  }

  @DisplayName("GIVEN player and game WHEN register is called THEN create new player")
  @Test
  void testPlayerServiceRegister() {
    // given
    var request = new PlayerRegistrationRequest("test", "test");
    var player = new Player(UUID.randomUUID(), "test", "test");
    when(persistence.registerPlayer(any())).thenReturn(player);

    // when
    var response = playerService.registerPlayer(request);

    // then
    assertNotNull(response);
    assertEquals(response.getName(), player.getName());
    assertEquals(response.getGameId(), player.getGameId());
  }


}
