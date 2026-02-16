package org.example.jle.productapi.controller;

import lombok.RequiredArgsConstructor;
import org.example.jle.productapi.controller.converter.RestProductConverter;
import org.example.jle.productapi.service.ProductService;
import org.example.jle.products.api.ProductsApi;
import org.example.jle.products.model.Product;
import org.example.jle.products.model.ProductIdResponse;
import org.example.jle.products.model.ProductRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class ProductController implements ProductsApi {

    private final ProductService service;
    private final RestProductConverter converter;

    @Override
    public ResponseEntity<List<Product>> getProducts() {
        return ResponseEntity.ok(service.getAllProducts());
    }

    @Override
    public ResponseEntity<Product> getProductById(UUID id) {
        return ResponseEntity.ok(service.getProductById(id));
    }

}
