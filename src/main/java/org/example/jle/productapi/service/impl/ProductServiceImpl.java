package org.example.jle.productapi.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.jle.productapi.repository.entity.ProductEntity;
import org.example.jle.productapi.repository.entity.converter.ProductEntityToProductConverter;
import org.example.jle.productapi.domain.model.Product;
import org.example.jle.productapi.exception.ProductAlreadyExistException;
import org.example.jle.productapi.exception.ProductNotFoundException;
import org.example.jle.productapi.repository.ProductRepository;
import org.example.jle.productapi.service.ProductService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductEntityToProductConverter converter;

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll().stream()
                .map(converter::convert)
                .toList();
    }

    @Override
    public Product getProductById(UUID id) {
        return productRepository.findById(id)
                .map(converter::convert)
                .orElseThrow(() -> new ProductNotFoundException(id));
    }

    @Override
    @Transactional
    public UUID createProduct(Product product) {

        if (productRepository.existsByName(product.getName())) {
            throw new ProductAlreadyExistException(product.getName());
        }
        ProductEntity entity = ProductEntity.builder()
                .name(product.getName())
                .price(product.getPrice())
                .description(product.getDescription())
                .build();

        return productRepository.save(entity).getId();
    }

    @Override
    @Transactional
    public Product updateProduct(UUID id, Product product) {
        ProductEntity entity = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        if (productRepository.existsByName(product.getName()))
            throw new ProductAlreadyExistException(product.getName());
        entity.setName(product.getName());
        entity.setPrice(product.getPrice());
        entity.setDescription(product.getDescription());

        return converter.convert(productRepository.save(entity));

    }

    @Override
    @Transactional
    public void deleteProductById(UUID id) {
        if (!productRepository.existsById(id)) {
            throw new ProductNotFoundException(id);
        }
        productRepository.deleteById(id);
    }
}
