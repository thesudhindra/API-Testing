package com.playground.playground.config.repository;

import com.playground.playground.config.entity.PlaygroundConfigEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaygroundConfigRepository extends JpaRepository<PlaygroundConfigEntity, String> {
}
