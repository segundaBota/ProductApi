package org.example.jle.productapi.service.impl;

import org.example.jle.productapi.service.TaxCalculator;
import org.springframework.stereotype.Service;

import static org.example.jle.productapi.config.Constants.IVA;

@Service("IVA")
public class TaxCalculatorIvaImpl implements TaxCalculator {

    @Override
    public Double calculateTax(Double price) {
        return price * IVA;
    }
}
