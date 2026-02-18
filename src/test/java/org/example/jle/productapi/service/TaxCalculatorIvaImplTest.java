package org.example.jle.productapi.service;

import org.example.jle.productapi.config.Constants;
import org.example.jle.productapi.service.impl.TaxCalculatorIvaImpl;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TaxCalculatorIvaImplTest {

    private final TaxCalculator taxCalculator = new TaxCalculatorIvaImpl();

    @Test
    void whenCalculateIva_thenSuccess() {
        Double price = 100.0;
        Double expected = price * Constants.IVA;

        assertEquals(expected, taxCalculator.calculateTax(price));
    }

    @Test
    void whenPriceIsZero_thenReturnZeroTax() {
        assertEquals(0.0, taxCalculator.calculateTax(0.0));
    }
}
