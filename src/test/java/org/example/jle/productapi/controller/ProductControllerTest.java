package org.example.jle.productapi.controller;

import org.example.jle.productapi.controller.converter.RestProductConverter;
import org.example.jle.productapi.service.ProductService;
import org.example.jle.products.model.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.when;

@WebMvcTest(value = ProductController.class)
@AutoConfigureMockMvc
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    @MockitoBean
    private RestProductConverter productConverter;

    @Nested
    @DisplayName("When get all products")
    class WhenGetAllProducts {

        @Test
        @DisplayName("When request is successful then return all products")
        void whenRequestIsSuccessfulThenReturnAllProducts() {
            when(productService.getAllProducts()).thenReturn(buildProductsResponse());
        }
    }

    private List<Product> buildProductsResponse() {
        return Collections.EMPTY_LIST;
    }
}
