package org.example.jle.productapi.service;


import org.example.jle.productapi.domain.model.Product;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ProductService {

    List<Product> getAllProducts();

    Product getProductById(UUID id);

    UUID createProduct(Product productRequest);

    Product updateProduct(UUID id, Product product);

    void deleteProductById(UUID id);

    List<Product> searchProducts(Map<String, String> filters);
}
