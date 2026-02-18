package org.example.jle.productapi.service;

import org.example.jle.productapi.domain.model.Product;
import org.example.jle.productapi.exception.ProductAlreadyExistException;
import org.example.jle.productapi.exception.ProductNotFoundException;
import org.example.jle.productapi.repository.ProductRepository;
import org.example.jle.productapi.repository.entity.ProductEntity;
import org.example.jle.productapi.repository.entity.converter.ProductEntityToProductConverter;
import org.example.jle.productapi.service.impl.ProductServiceImpl;
import org.example.jle.productapi.service.impl.TaxCalculatorIvaImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProductServiceImplTest {

    private static final UUID UUID_1 = UUID.randomUUID();
    private static final UUID UUID_2 = UUID.randomUUID();
    private static final String NAME_1 = "Product 1";
    private static final String NAME_2 = "Product 2";
    private static final String DESCRIPTION_1 = "Description 1";
    private static final String DESCRIPTION_2 = "Description 2";
    private static final Double PRICE_1 = 10.0;
    private static final Double PRICE_2 = 20.0;

    @Mock
    private ProductRepository productRepository;

    private final ProductEntityToProductConverter converter =
            Mappers.getMapper(ProductEntityToProductConverter.class);

    private final TaxCalculator taxCalculator = new TaxCalculatorIvaImpl();

    ProductServiceImpl productService;

    @BeforeEach
    void setup() {
        productService = new ProductServiceImpl(productRepository, converter, taxCalculator);
    }

    @Test
    void whenGetAllProducts_thenReturnAllProducts() {
        when(productRepository.findAll()).thenReturn(buildMockProductEntityList());

        List<Product> products = productService.getAllProducts();
        assertEquals(2, products.size());
        assertEquals(UUID_1, products.getFirst().getId());
        assertEquals(NAME_1, products.getFirst().getName());
        assertEquals(DESCRIPTION_1, products.getFirst().getDescription());
        assertEquals(PRICE_1, products.getFirst().getPrice());
        assertEquals(UUID_2, products.get(1).getId());
        assertEquals(NAME_2, products.get(1).getName());
        assertEquals(DESCRIPTION_2, products.get(1).getDescription());
        assertEquals(PRICE_2, products.get(1).getPrice());
    }

    @Test
    void whenGetProductById_thenReturnProduct() {
        when(productRepository.findById(UUID_1)).thenReturn(Optional.of(buildMockProductEntityList().getFirst()));

        Product product = productService.getProductById(UUID_1);
        assertEquals(UUID_1, product.getId());
        assertEquals(NAME_1, product.getName());
        assertEquals(DESCRIPTION_1, product.getDescription());
        assertEquals(PRICE_1, product.getPrice());
    }

    @Test
    void whenCreateProduct_thenReturnId() {
        when(productRepository.findByName(NAME_1)).thenReturn(Optional.empty());
        when(productRepository.save(any())).thenReturn(buildMockProductEntityList().getFirst());

        UUID id = productService.createProduct(buildProduct());

        assertEquals(UUID_1, id);
    }

    @Test
    void whenCreateProduct_thenProductIsSavedIncludingTaxes() {
        Product productToCreate = buildProduct();

        Double expectedPriceWithTaxes = PRICE_1 + taxCalculator.calculateTax(PRICE_1);

        ProductEntity entityToReturn = ProductEntity.builder()
                .id(UUID_1)
                .name(NAME_1)
                .price(expectedPriceWithTaxes)
                .build();

        when(productRepository.findByName(NAME_1)).thenReturn(Optional.empty());
        when(productRepository.save(any(ProductEntity.class))).thenReturn(entityToReturn);

        productService.createProduct(productToCreate);

        ArgumentCaptor<ProductEntity> entityCaptor = ArgumentCaptor.forClass(ProductEntity.class);
        verify(productRepository).save(entityCaptor.capture());
        ProductEntity capturedEntity = entityCaptor.getValue();

        assertEquals(expectedPriceWithTaxes, capturedEntity.getPrice());
    }

    @Test
    void whenCreateExistingProduct_thenThrowException() {
        when(productRepository.findByName(any())).thenReturn(Optional.of(buildMockProductEntityList().getFirst()));

        assertThrows(ProductAlreadyExistException.class,
                () -> productService.createProduct(buildProduct()));
    }

    @Test
    void whenUpdateProduct_thenReturnUpdatedProduct() {
        ProductEntity productEntity = buildMockProductEntityList().getFirst();
        when(productRepository.findById(UUID_1)).thenReturn(Optional.of(productEntity));
        when(productRepository.existsByNameAndIdNot(any(), any())).thenReturn(false);
        when(productRepository.save(any())).thenReturn(productEntity);

        Product updatedProduct = productService.updateProduct(UUID_1, Product.builder().name(NAME_2).price(PRICE_2).description(DESCRIPTION_2).build());

        assertEquals(UUID_1, updatedProduct.getId());
        assertEquals(NAME_2, updatedProduct.getName());
        assertEquals(DESCRIPTION_2, updatedProduct.getDescription());
        assertEquals(PRICE_2, updatedProduct.getPrice());
    }

    @Test
    void whenUpdateNonExistingProduct_thenThrowException() {
        when(productRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class,
                () -> productService.updateProduct(UUID_1, Product.builder().name(NAME_2).price(PRICE_2).description(DESCRIPTION_2).build()));
    }

    @Test
    void whenUpdateNonExistingProductWithExistingName_thenThrowException() {
        ProductEntity productEntity = buildMockProductEntityList().getFirst();
        when(productRepository.findById(UUID_1)).thenReturn(Optional.of(productEntity));
        when(productRepository.existsByNameAndIdNot(any(), any())).thenReturn(true);

        assertThrows(ProductAlreadyExistException.class,
                () -> productService.updateProduct(UUID_1, Product.builder().name(NAME_2).price(PRICE_2).description(DESCRIPTION_2).build()));
    }

    @Test
    void whenDeleteProductNotFound_thenThrowException() {
        when(productRepository.existsById(any())).thenReturn(false);

        assertThrows(ProductNotFoundException.class, () -> productService.deleteProductById(UUID_1));
    }

    @Test
    void whenSearchProductsWithFilters_thenReturnFilteredList() {
        Map<String, String> filters = new HashMap<>();
        filters.put("name", NAME_1);
        filters.put("price", PRICE_1.toString());
        List<ProductEntity> entities = List.of(buildMockProductEntityList().getFirst());
        when(productRepository.findAll(any(Specification.class)))
                .thenReturn(entities);

        List<Product> result = productService.searchProducts(filters);

        assertEquals(1, result.size());
        assertEquals(NAME_1, result.getFirst().getName());
        assertEquals(PRICE_1, result.getFirst().getPrice());
        verify(productRepository).findAll(any(Specification.class));
    }

    @Test
    void whenSearchProductsWithEmptyFilters_thenReturnAll() {
        Map<String, String> emptyFilters = new HashMap<>();

        when(productRepository.findAll(any(Specification.class)))
                .thenReturn(buildMockProductEntityList());

        List<Product> result = productService.searchProducts(emptyFilters);

        assertEquals(2, result.size());
        verify(productRepository).findAll(any(Specification.class));
    }

    @Test
    void whenSearchProductsReturnsEmpty_thenReturnEmptyList() {
        Map<String, String> filters = Map.of("name", "NonExistent");

        when(productRepository.findAll(any(Specification.class)))
                .thenReturn(List.of());

        List<Product> result = productService.searchProducts(filters);

        assertEquals(0, result.size());
        verify(productRepository).findAll(any(Specification.class));
    }

    private List<ProductEntity> buildMockProductEntityList() {
        return List.of(
                ProductEntity.builder()
                        .id(UUID_1)
                        .name(NAME_1)
                        .description(DESCRIPTION_1)
                        .price(PRICE_1)
                        .build(),
                ProductEntity.builder()
                        .id(UUID_2)
                        .name(NAME_2)
                        .description(DESCRIPTION_2)
                        .price(PRICE_2)
                        .build()
        );
    }

    private Product buildProduct() {
        return Product.builder()
                .id(UUID.randomUUID())
                .name(NAME_1)
                .description(DESCRIPTION_1)
                .price(PRICE_1).build();
    }
}
