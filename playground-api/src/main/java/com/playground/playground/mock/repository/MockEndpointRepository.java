package com.playground.playground.mock.repository;

import com.playground.playground.mock.entity.MockEndpointEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MockEndpointRepository extends JpaRepository<MockEndpointEntity, String> {

    List<MockEndpointEntity> findByEnabledTrue();

    Optional<MockEndpointEntity> findByPathAndHttpMethod(String path, String httpMethod);
}
