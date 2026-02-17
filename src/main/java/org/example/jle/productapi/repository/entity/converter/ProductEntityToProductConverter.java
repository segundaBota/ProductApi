package org.example.jle.productapi.repository.entity.converter;

import org.example.jle.productapi.repository.entity.ProductEntity;
import org.example.jle.productapi.domain.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductEntityToProductConverter {

    public Product convert(ProductEntity entity);

    @Mapping(target = "id", ignore = true)
    public ProductEntity convertToEntity(Product product);

}
