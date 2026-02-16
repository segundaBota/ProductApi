package org.example.jle.productapi.service;

import org.example.jle.products.model.Product;

import java.util.List;
import java.util.UUID;

public interface ProductService {

    public List<Product> getAllProducts();

    public Product getProductById(UUID id);

    public UUID createProduct(Product productRequest);

    public Product updateProduct(Product product);

    public void deleteProductById(UUID id);
}
