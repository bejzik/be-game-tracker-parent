package com.numarics.game.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import com.numarics.game.model.GameStatus;

@Getter
@Setter
@Entity
@Table(name = "game")
@NoArgsConstructor
public class GameEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id", nullable = false)
  @JdbcTypeCode(SqlTypes.UUID)
  private UUID id;

  private String name;
  @Enumerated(EnumType.STRING)
  private GameStatus status;
  private OffsetDateTime createdAt;
  private OffsetDateTime updatedAt;

  @PrePersist
  public void onPrePersist() {
    var now = OffsetDateTime.now();
    createdAt = now;
    updatedAt = now;
  }

  @PreUpdate
  public void onPreUpdate() {
    updatedAt = OffsetDateTime.now();
  }
}
