package org.example.jle.productapi.service;

import org.example.jle.productapi.config.Constants;
import org.example.jle.productapi.service.impl.TaxCalculatorItbisImpl;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TaxCalculatorItbisImplTest {

    private final TaxCalculator taxCalculator = new TaxCalculatorItbisImpl();

    @Test
    void whenCalculateItbis_thenSuccess() {
        Double price = 100.0;
        Double expected = price * Constants.ITBIS;

        assertEquals(expected, taxCalculator.calculateTax(price));
    }
}
