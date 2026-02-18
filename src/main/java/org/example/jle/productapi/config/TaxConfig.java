package org.example.jle.productapi.config;

import org.example.jle.productapi.service.TaxCalculator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class TaxConfig {

    @Value("${app.tax.type}")
    private String taxType;

    @Bean
    public TaxCalculator taxCalculator(Map<String, TaxCalculator> taxCalculators) {
        return taxCalculators.get(taxType);
    }
}
