package com.numarics.game.persistence.repository;

import jakarta.persistence.criteria.Predicate;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;
import com.numarics.game.model.GameStatus;
import com.numarics.game.persistence.entity.GameEntity;

public class GameSpecification {

  public static Specification<GameEntity> searchGamesByCriteria(String gameName, GameStatus status) {
    return (root, query, criteriaBuilder) -> {
      List<Predicate> predicates = new ArrayList<>();

      if (gameName != null) {
        predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + gameName.toLowerCase() + "%"));
      }

      if (status != null) {
        predicates.add(criteriaBuilder.equal(root.get("status"), status));
      }

      return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    };
  }
}
