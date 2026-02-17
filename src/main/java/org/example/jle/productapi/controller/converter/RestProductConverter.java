package org.example.jle.productapi.controller.converter;

import org.example.jle.productapi.domain.model.Product;
import org.example.jle.products.model.ProductDto;
import org.example.jle.products.model.ProductIdResponse;
import org.example.jle.products.model.ProductRequest;
import org.hibernate.service.spi.ServiceException;
import org.mapstruct.Mapper;

import java.util.UUID;

@Mapper(componentModel = "spring", unexpectedValueMappingException = ServiceException.class)
public interface RestProductConverter {

    Product convertToProduct(ProductRequest productRequest);

    ProductIdResponse convertToProductIdResponse(UUID id);

    ProductDto convertToProductDto(Product product);
}
