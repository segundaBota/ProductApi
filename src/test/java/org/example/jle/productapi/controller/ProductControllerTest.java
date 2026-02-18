package org.example.jle.productapi.controller;

import org.example.jle.productapi.config.SecurityConfiguration;
import org.example.jle.productapi.controller.converter.RestProductConverter;
import org.example.jle.productapi.domain.model.Product;
import org.example.jle.productapi.exception.ProductAlreadyExistException;
import org.example.jle.productapi.exception.ProductNotFoundException;
import org.example.jle.productapi.service.ProductService;
import org.example.jle.products.model.ProductDto;
import org.example.jle.products.model.ProductIdResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = ProductController.class)
@Import(SecurityConfiguration.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class ProductControllerTest {

    private static final String PRODUCTS_ENDPOINT = "/products";
    private static final String PRODUCTS_ENDPOINT_ID = "/products/{id}";
    private static final String PRODUCTS_SEARCH_ENDPOINT = "/products/search";
    private static final UUID PRODUCT_ID = UUID.randomUUID();
    private static final String PRODUCT_NAME = "ProductName";
    private static final String BODY = "{\"name\": \"NewProductName\", \"price\": 5.0}";

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
        @DisplayName("When  is successful then return all products")
        @WithMockUser(roles = "USER")
        void whenRequestIsSuccessfulThenReturnAllProducts() throws Exception {
            when(productService.getAllProducts()).thenReturn(buildProductsResponse());

            mockMvc.perform(get(PRODUCTS_ENDPOINT)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(2));
        }

        @Test
        @DisplayName("When user is not authenticated then return 401")
        void whenUserIsNotAuthenticatedThenReturn401() throws Exception {
            mockMvc.perform(get(PRODUCTS_ENDPOINT)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("When user has no USER role then return 403")
        @WithMockUser(roles = "ADMIN") // Un Admin que no tenga rol USER (según tu lógica) o un GUEST
        void whenUserHasNoUserRoleThenReturn403() throws Exception {
            mockMvc.perform(get(PRODUCTS_ENDPOINT)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("When service throws exception then return 500")
        @WithMockUser(roles = "USER")
        void whenServiceThrowsExceptionThenReturn500() throws Exception {
            // Aseguramos que el mock lance la excepción en esta ejecución
            when(productService.getAllProducts()).thenThrow(new RuntimeException("Unexpected error"));

            mockMvc.perform(get(PRODUCTS_ENDPOINT)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isInternalServerError());
        }
    }

    @Nested
    @DisplayName("When get product by id")
    class WhenGetProductById {
        @Test
        @DisplayName("When  is successful then return product (200)")
        @WithMockUser(roles = "USER")
        void whenRequestIsSuccessfulThenReturnProduct() throws Exception {
            // Given
            Product product = Product.builder().id(PRODUCT_ID).name(PRODUCT_NAME).build();
            ProductDto productDto = new ProductDto();
            productDto.setId(PRODUCT_ID);
            productDto.setName(PRODUCT_NAME);

            when(productService.getProductById(PRODUCT_ID)).thenReturn(product);
            when(productConverter.convertToProductDto(product)).thenReturn(productDto);

            // When & Then
            mockMvc.perform(get(PRODUCTS_ENDPOINT_ID, PRODUCT_ID)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(PRODUCT_ID.toString()))
                    .andExpect(jsonPath("$.name").value(PRODUCT_NAME));
        }

        @Test
        @DisplayName("When id format is invalid then return bad  (400)")
        @WithMockUser(roles = "USER")
        void whenIdFormatIsInvalidThenReturn400() throws Exception {
            mockMvc.perform(get(PRODUCTS_ENDPOINT_ID, "InvalidIdFormat")
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("When user is not authenticated then return unauthorized (401)")
        void whenUserIsNotAuthenticatedThenReturn401() throws Exception {
            mockMvc.perform(get(PRODUCTS_ENDPOINT_ID, PRODUCT_ID)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("When user has no USER role then return forbidden (403)")
        @WithMockUser(roles = "GUEST")
        void whenUserHasNoUserRoleThenReturn403() throws Exception {
            mockMvc.perform(get(PRODUCTS_ENDPOINT_ID, PRODUCT_ID)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("When product does not exist then return not found (404)")
        @WithMockUser(roles = "USER")
        void whenProductDoesNotExistThenReturn404() throws Exception {
            when(productService.getProductById(PRODUCT_ID)).thenThrow(new ProductNotFoundException(PRODUCT_ID));

            mockMvc.perform(get(PRODUCTS_ENDPOINT_ID, PRODUCT_ID)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("When service fails then return internal server error (500)")
        @WithMockUser(roles = "USER")
        void whenServiceFailsThenReturn500() throws Exception {
            when(productService.getProductById(PRODUCT_ID)).thenThrow(new RuntimeException("Database down"));

            mockMvc.perform(get(PRODUCTS_ENDPOINT_ID, PRODUCT_ID)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isInternalServerError());
        }
    }

    @Nested
    @DisplayName("When create product")
    class WhenCreateProduct {
        @Test
        @DisplayName("When product is valid then return 201 Created")
        @WithMockUser(roles = "ADMIN")
        void whenProductIsValidThenReturn201() throws Exception {
            UUID newId = UUID.randomUUID();
            ProductIdResponse response = new ProductIdResponse();
            response.setId(newId);

            when(productConverter.convertToProduct(any())).thenReturn(Product.builder().build());
            when(productService.createProduct(any())).thenReturn(newId);
            when(productConverter.convertToProductIdResponse(newId)).thenReturn(response);

            mockMvc.perform(post(PRODUCTS_ENDPOINT)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(BODY))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(newId.toString()));
        }

        @Test
        @DisplayName("When name already exists then return 409 Conflict")
        @WithMockUser(roles = "ADMIN")
        void whenNameExistsThenReturn409() throws Exception {
            when(productConverter.convertToProduct(any())).thenReturn(Product.builder().name("Existing").build());
            when(productService.createProduct(any())).thenThrow(new ProductAlreadyExistException("Existing"));

            mockMvc.perform(post(PRODUCTS_ENDPOINT)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(BODY))
                    .andExpect(status().isConflict());
        }

        @Test
        @DisplayName("When user is not authorized then return 401")
        void whenUserIsNotAuthorizedThenReturn401() throws Exception {
            mockMvc.perform(post(PRODUCTS_ENDPOINT)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(BODY))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("When user has no ADMIN role then return 403")
        @WithMockUser(roles = "USER")
        void whenUserIsNotAdminThenReturn403() throws Exception {
            mockMvc.perform(post(PRODUCTS_ENDPOINT)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(BODY))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("When service fails then return 500 Internal Server Error")
        @WithMockUser(roles = "ADMIN")
        void whenServiceFailsThenReturn500() throws Exception {
            when(productService.createProduct(any())).thenThrow(new RuntimeException("Database down"));

            mockMvc.perform(post(PRODUCTS_ENDPOINT)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(BODY))
                    .andExpect(status().isInternalServerError());
        }
    }

    @Nested
    @DisplayName("When update product")
    class WhenUpdateProduct {
        @Test
        @DisplayName("When update is successful then return 204 No Content")
        @WithMockUser(roles = "ADMIN")
        void whenUpdateIsSuccessfulThenReturn204() throws Exception {
            when(productConverter.convertToProduct(any())).thenReturn(Product.builder().build());
            when(productService.updateProduct(any(), any())).thenReturn(Product.builder().build());

            mockMvc.perform(put(PRODUCTS_ENDPOINT_ID, PRODUCT_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(BODY))
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("When product not found then return 404")
        @WithMockUser(roles = "ADMIN")
        void whenNotFoundThenReturn404() throws Exception {
            when(productConverter.convertToProduct(any())).thenReturn(Product.builder().build());
            when(productService.updateProduct(any(), any())).thenThrow(new ProductNotFoundException(PRODUCT_ID));

            mockMvc.perform(put(PRODUCTS_ENDPOINT_ID, PRODUCT_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(BODY))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("When user is not admin then return 403")
        @WithMockUser(roles = "USER")
        void whenUserIsNotAdminThenReturn403() throws Exception {
            mockMvc.perform(put(PRODUCTS_ENDPOINT_ID, PRODUCT_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(BODY))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("When ID format is invalid then return 400")
        @WithMockUser(roles = "ADMIN")
        void whenInvalidIdThenReturn400() throws Exception {
            mockMvc.perform(put(PRODUCTS_ENDPOINT_ID, "not-a-uuid")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("When delete product")
    class WhenDeleteProduct {
        @Test
        @DisplayName("When delete is successful then return 204 No Content")
        @WithMockUser(roles = "ADMIN")
        void whenDeleteSuccessfulThenReturn204() throws Exception {
            mockMvc.perform(delete(PRODUCTS_ENDPOINT_ID, PRODUCT_ID))
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("When product does not exist then return 404")
        @WithMockUser(roles = "ADMIN")
        void whenNotExistsThenReturn404() throws Exception {
            doThrow(new ProductNotFoundException(PRODUCT_ID))
                    .when(productService).deleteProductById(PRODUCT_ID);

            mockMvc.perform(delete(PRODUCTS_ENDPOINT_ID, PRODUCT_ID))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("When user is unauthorized then return 401")
        void whenUnauthorizedThenReturn401() throws Exception {
            mockMvc.perform(delete(PRODUCTS_ENDPOINT_ID, PRODUCT_ID))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("When user has not correct role then return 403")
        @WithMockUser(roles = "USER")
        void whenHasNotCorrectRoleThenReturn403() throws Exception {
            mockMvc.perform(delete(PRODUCTS_ENDPOINT_ID, PRODUCT_ID))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("When error then return 500")
        @WithMockUser(roles = "ADMIN")
        void whenErrorThenReturn500() throws Exception {
            doThrow(new RuntimeException())
                    .when(productService).deleteProductById(PRODUCT_ID);

            mockMvc.perform(delete(PRODUCTS_ENDPOINT_ID, PRODUCT_ID))
                    .andExpect(status().isInternalServerError());
        }
    }

    @Nested
    @DisplayName("When search product")
    class WhenSearchProduct {

        @Test
        @DisplayName("When search is successful then return 200 OK")
        @WithMockUser(roles = "USER")
        void whenSearchSuccessfulThenReturn200() throws Exception {
            // Given
            Product product = Product.builder().id(PRODUCT_ID).name(PRODUCT_NAME).build();
            ProductDto productDto = new ProductDto();
            productDto.setId(PRODUCT_ID);
            productDto.setName(PRODUCT_NAME);

            when(productService.searchProducts(any())).thenReturn(List.of(product));
            when(productConverter.convertToProductDto(any())).thenReturn(productDto);

            // When & Then
            mockMvc.perform(get(PRODUCTS_SEARCH_ENDPOINT )
                            .param("name", PRODUCT_NAME)
                            .param("price", "99.99")
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(1))
                    .andExpect(jsonPath("$[0].name").value(PRODUCT_NAME));
        }

        @Test
        @DisplayName("When price format is invalid then return 400 Bad Request")
        @WithMockUser(roles = "USER")
        void whenPriceFormatIsInvalidThenReturn400() throws Exception {
            mockMvc.perform(get(PRODUCTS_SEARCH_ENDPOINT )
                            .param("price", "notANumber")
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("When user is not authenticated then return 401 Unauthorized")
        void whenUserIsNotAuthenticatedThenReturn401() throws Exception {
            mockMvc.perform(get(PRODUCTS_SEARCH_ENDPOINT )
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("When user has no USER role then return 403 Forbidden")
        @WithMockUser(roles = "GUEST")
        void whenUserHasNoUserRoleThenReturn403() throws Exception {
            mockMvc.perform(get(PRODUCTS_SEARCH_ENDPOINT )
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("When service throws exception then return 500 Internal Server Error")
        @WithMockUser(roles = "USER")
        void whenServiceThrowsExceptionThenReturn500() throws Exception {
            when(productService.searchProducts(any())).thenThrow(new RuntimeException("Search failed"));

            mockMvc.perform(get(PRODUCTS_SEARCH_ENDPOINT )
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isInternalServerError());
        }
    }

    private List<Product> buildProductsResponse() {
        return List.of(
                Product.builder().id(UUID.randomUUID()).name("P1").build(),
                Product.builder().id(UUID.randomUUID()).name("P2").build()
        );
    }
}