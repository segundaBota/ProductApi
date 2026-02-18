package org.example.jle.productapi.service;


import org.example.jle.productapi.domain.model.Product;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ProductService {

    public List<Product> getAllProducts();

    public Product getProductById(UUID id);

    public UUID createProduct(Product productRequest);

    public Product updateProduct(UUID id,Product product);

    public void deleteProductById(UUID id);

    List<Product> searchProducts(Map<String, String> filters);
}
