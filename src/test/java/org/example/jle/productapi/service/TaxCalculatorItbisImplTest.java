package org.example.jle.productapi.service;

import org.example.jle.productapi.config.Constants;
import org.example.jle.productapi.service.impl.TaxCalculatorItbisImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class TaxCalculatorItbisImplTest {

    private final TaxCalculator taxCalculator = new TaxCalculatorItbisImpl();

    @Test
    void whenCalculateItbis_thenSuccess() {
        Double price = 100.0;
        Double expected = price * Constants.ITBIS;

        assertEquals(expected, taxCalculator.calculateTax(price));
    }
}
