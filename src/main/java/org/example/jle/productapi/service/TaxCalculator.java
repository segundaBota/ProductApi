package org.example.jle.productapi.service;

@FunctionalInterface
public interface TaxCalculator {

    Double calculateTax(Double price);
}
