package org.example.jle.productapi.dto;


import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class ProductDto {

    private UUID id;
    private String name;
    private String description;
    private Double price;
}
