package com.numarics.player.persistence;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import com.numarics.player.persistence.entity.PlayerEntity;
import com.numarics.player.persistence.repository.PlayerRepository;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class PlayerPersistenceAdapterTest {

  @Autowired
  private PlayerRepository repository;

  @DisplayName("WHEN ")
  @Test
  void verifySavingToDb() {
    var player = new PlayerEntity();
    player.setName("test");
    player.setGameId("game");

    Assertions.assertNull(player.getId());
    repository.save(player);

    Assertions.assertNotNull(player.getId());
  }
}
