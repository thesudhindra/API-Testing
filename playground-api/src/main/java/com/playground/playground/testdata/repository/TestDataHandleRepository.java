package com.playground.playground.testdata.repository;

import com.playground.playground.testdata.entity.TestDataHandleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TestDataHandleRepository extends JpaRepository<TestDataHandleEntity, String> {

    List<TestDataHandleEntity> findByNamespace(String namespace);

    void deleteByNamespace(String namespace);
}
