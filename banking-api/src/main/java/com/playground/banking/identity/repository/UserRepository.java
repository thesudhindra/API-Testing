package com.playground.banking.identity.repository;

import com.playground.banking.identity.entity.UserEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, String> {

    @EntityGraph(attributePaths = "roles")
    @Query("SELECT u FROM UserEntity u WHERE u.tenantId = :tenantId AND u.username = :username")
    Optional<UserEntity> findByTenantIdAndUsername(@Param("tenantId") String tenantId, @Param("username") String username);

    List<UserEntity> findByTenantId(String tenantId);

    boolean existsByTenantIdAndUsername(String tenantId, String username);
}
