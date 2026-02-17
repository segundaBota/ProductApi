package org.example.jle.productapi.exception;

public class ProductAlreadyExistException extends RuntimeException {

    public ProductAlreadyExistException(String message) {
        super("Product already exists with name: " + message);
    }
}
