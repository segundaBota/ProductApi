package org.example.jle.productapi.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.jle.productapi.domain.model.Product;
import org.example.jle.productapi.exception.ProductAlreadyExistException;
import org.example.jle.productapi.exception.ProductNotFoundException;
import org.example.jle.productapi.repository.ProductRepository;
import org.example.jle.productapi.repository.entity.ProductEntity;
import org.example.jle.productapi.repository.entity.converter.ProductEntityToProductConverter;
import org.example.jle.productapi.repository.specification.ProductSpecification;
import org.example.jle.productapi.service.ProductService;
import org.example.jle.productapi.service.TaxCalculator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductEntityToProductConverter converter;
    private final TaxCalculator taxCalculator;

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
        productRepository.findByName(product.getName())
                .ifPresent(p -> {
                    throw new ProductAlreadyExistException(product.getName());
                });

        return Optional.of(product)
                .map(this::applyTaxes)
                .map(converter::convertToEntity)
                .map(productRepository::save)
                .map(ProductEntity::getId)
                .orElseThrow();
    }

    @Override
    @Transactional
    public Product updateProduct(UUID id, Product product) {
        ProductEntity updatedEntity = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        Optional.of(product)
                .filter(p -> productRepository.existsByNameAndIdNot(p.getName(), id))
                .ifPresent(p -> {
                    throw new ProductAlreadyExistException(p.getName());
                });

        applyUpdates(updatedEntity, product);

        return converter.convert(productRepository.save(updatedEntity));
    }

    @Override
    @Transactional
    public void deleteProductById(UUID id) {
        if (!productRepository.existsById(id)) {
            throw new ProductNotFoundException(id);
        }
        productRepository.deleteById(id);
    }

    @Override
    public List<Product> searchProducts(Map<String, String> filters) {
        return productRepository.findAll(ProductSpecification.filterBy(filters))
                .stream()
                .map(converter::convert)
                .toList();
    }

    private Product applyTaxes(Product product) {
        return product.toBuilder()
                .price(calculatePriceWithTaxes(product.getPrice()))
                .build();
    }

    private Double calculatePriceWithTaxes(Double price) {
        return price + taxCalculator.calculateTax(price);
    }

    private void applyUpdates(ProductEntity entity, Product product) {
        entity.setName(product.getName());
        entity.setPrice(product.getPrice());
        entity.setDescription(product.getDescription());
    }
}
