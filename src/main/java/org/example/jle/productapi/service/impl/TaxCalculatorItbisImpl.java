package org.example.jle.productapi.service.impl;

import org.example.jle.productapi.service.TaxCalculator;
import org.springframework.stereotype.Service;

import static org.example.jle.productapi.config.Constants.ITBIS;

@Service("ITBIS")
public class TaxCalculatorItbisImpl implements TaxCalculator {

    @Override
    public Double calculateTax(Double price) {
        return price * ITBIS;
    }
}
