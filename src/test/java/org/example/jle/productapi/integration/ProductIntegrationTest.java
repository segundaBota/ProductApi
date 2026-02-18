package org.example.jle.productapi.integration;

import org.example.jle.productapi.repository.ProductRepository;
import org.example.jle.productapi.repository.entity.ProductEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class ProductIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    private UUID savedId;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();
        ProductEntity entity = ProductEntity.builder()
                .name("Integration Test Product")
                .description("Desc")
                .price(100.0)
                .build();
        savedId = productRepository.save(entity).getId();
    }

    @Test
    @WithMockUser(roles = "USER")
    void testGetAllProducts() throws Exception {
        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Integration Test Product"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreateProduct() throws Exception {
        String json = """
                {
                  "name": "New Integration Product",
                  "description": "New Desc",
                  "price": 50.0
                }
                """;

        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    @WithMockUser(roles = "USER")
    void testGetProductById() throws Exception {
        mockMvc.perform(get("/products/{id}", savedId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedId.toString()));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateProduct() throws Exception {
        String json = """
                {
                  "name": "Updated Name",
                  "description": "Updated Desc",
                  "price": 75.0
                }
                """;

        mockMvc.perform(put("/products/{id}", savedId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "USER"})
    void testDeleteProduct() throws Exception {
        mockMvc.perform(delete("/products/{id}", savedId))
                .andExpect(status().isNoContent());
        
        mockMvc.perform(get("/products/{id}", savedId))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "USER")
    void testSearchProducts() throws Exception {
        mockMvc.perform(get("/products/search")
                        .param("name", "Integration")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }
}