package org.example.jle.productapi.service;

import org.example.jle.productapi.config.Constants;
import org.example.jle.productapi.service.impl.TaxCalculatorIvaImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class TaxCalculatorIvaImplTest {

    private final TaxCalculator taxCalculator = new TaxCalculatorIvaImpl();

    @Test
    void whenCalculateIva_thenSuccess() {
        Double price = 100.0;
        Double expected = price * Constants.IVA;

        assertEquals(expected, taxCalculator.calculateTax(price));
    }
}
