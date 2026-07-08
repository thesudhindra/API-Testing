package com.playground.enterprise.resilience.repository;

import com.playground.enterprise.resilience.entity.CircuitBreakerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CircuitBreakerRepository extends JpaRepository<CircuitBreakerEntity, String> {
}
