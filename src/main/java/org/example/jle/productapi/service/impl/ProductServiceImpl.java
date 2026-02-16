package org.example.jle.productapi.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.jle.productapi.domain.entity.ProductEntity;
import org.example.jle.productapi.domain.entity.converter.ProductEntityToProductConverter;
import org.example.jle.productapi.repository.ProductRepository;
import org.example.jle.productapi.service.ProductService;
import org.example.jle.products.model.Product;
import org.springframework.stereotype.Service;

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
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    @Override
    public UUID createProduct(Product product) {
        return null;
    }

    @Override
    public Product updateProduct(Product product) {
        return null;
    }

    @Override
    public void deleteProductById(UUID id) {
        productRepository.deleteById(id);
    }
}
