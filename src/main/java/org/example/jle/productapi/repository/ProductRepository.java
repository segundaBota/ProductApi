package org.example.jle.productapi.repository;

import org.example.jle.productapi.domain.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProductRepository extends JpaRepository<ProductEntity, UUID> {

    boolean existsByName(String name);
}
