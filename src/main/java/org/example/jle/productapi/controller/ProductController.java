package org.example.jle.productapi.controller;

import lombok.RequiredArgsConstructor;
import org.example.jle.productapi.controller.converter.RestProductConverter;
import org.example.jle.productapi.service.ProductService;
import org.example.jle.products.api.ProductsApi;
import org.example.jle.products.model.ProductDto;
import org.example.jle.products.model.ProductIdResponse;
import org.example.jle.products.model.ProductRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Validated
public class ProductController implements ProductsApi {

    private final ProductService productService;
    private final RestProductConverter productConverter;

    @Override
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<ProductDto>> getProducts() {
        return ResponseEntity.ok(productService.getAllProducts().stream()
                .map(productConverter::convertToProductDto).toList());
    }

    @Override
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ProductDto> getProductById(UUID id) {
        return ResponseEntity.ok(productConverter.convertToProductDto(productService.getProductById(id)));
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductIdResponse> createProduct(ProductRequest productRequest) {
        UUID id = productService.createProduct(productConverter.convertToProduct(productRequest));
        return ResponseEntity.status(HttpStatus.CREATED).body(productConverter.convertToProductIdResponse(id));
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> updateProduct(UUID id, ProductRequest productRequest) {
        productService.updateProduct(id, productConverter.convertToProduct(productRequest));
        return ResponseEntity.noContent().build();
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteProduct(UUID id) {
        productService.deleteProductById(id);
        return ResponseEntity.noContent().build();
    }
}