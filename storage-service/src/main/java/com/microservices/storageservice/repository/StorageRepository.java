package com.microservices.storageservice.repository;

import com.microservices.storageservice.entity.Storage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StorageRepository extends JpaRepository<Storage, Long> {
}
