package in.vhglobal.ecommerce.controllers;

import in.vhglobal.ecommerce.dtos.mappers.ProductMapper;
import in.vhglobal.ecommerce.dtos.reponse.ProductResponse;
import in.vhglobal.ecommerce.dtos.request.ProductRequest;
import in.vhglobal.ecommerce.entities.products.Product;
import in.vhglobal.ecommerce.entities.products.ProductStatus;
import in.vhglobal.ecommerce.services.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ProductRetrievalTest {

    private ProductService service;
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        service = mock(ProductService.class);
        mvc = MockMvcBuilders.standaloneSetup(new ProductsController(null, null, service, null)).build();
    }

    static Stream<Arguments> requestFlags() {
        return Stream.of(
                Arguments.of("{}", false),
                Arguments.of("{\"includeDeleted\":false}", false),
                Arguments.of("{\"includeDeleted\":null}", false),
                Arguments.of("{\"includeDeleted\":true}", true));
    }

    @ParameterizedTest
    @MethodSource("requestFlags")
    void listEndpointsAcceptOldRequestsAndHandleNullFlag(String body, boolean includeDeleted) throws Exception {
        var pageable = PageRequest.of(0, 50, Sort.by("name"));
        when(service.getAllProducts(pageable, false, includeDeleted)).thenReturn(Page.empty(pageable));
        when(service.getBySubCategoryId("category", pageable, false, includeDeleted)).thenReturn(Page.empty(pageable));

        mvc.perform(post("/api/v1/products/all").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk());
        mvc.perform(post("/api/v1/products/by-sub-category").param("subProductCategoryId", "category")
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk());

        verify(service).getAllProducts(pageable, false, includeDeleted);
        verify(service).getBySubCategoryId("category", pageable, false, includeDeleted);
    }

    @Test
    void idEndpointDefaultsToFalseAndAllowsExplicitOptIn() throws Exception {
        when(service.getProductById(eq("product"), anyBoolean())).thenReturn(new ProductResponse());
        mvc.perform(get("/api/v1/products/product")).andExpect(status().isOk());
        mvc.perform(get("/api/v1/products/product").param("includeDeleted", "false")).andExpect(status().isOk());
        mvc.perform(get("/api/v1/products/product").param("includeDeleted", "true")).andExpect(status().isOk());
        verify(service, times(2)).getProductById("product", false);
        verify(service).getProductById("product", true);
    }

    @Test
    void deleteEndpointDefaultsToSoftAndAllowsExplicitHardDelete() throws Exception {
        mvc.perform(delete("/api/v1/products/product")).andExpect(status().isOk());
        mvc.perform(delete("/api/v1/products/product").param("hardDelete", "false")).andExpect(status().isOk());
        mvc.perform(delete("/api/v1/products/product").param("hardDelete", "true")).andExpect(status().isOk());
        verify(service, times(2)).deleteProduct("product", false);
        verify(service).deleteProduct("product", true);
    }

    @Test
    void creationDefaultsToActiveAndResponsePreservesLegacyNullStatus() {
        ProductMapper mapper = Mappers.getMapper(ProductMapper.class);
        assertThat(mapper.toEntity(new ProductRequest()).getStatus()).isEqualTo(ProductStatus.ACTIVE);
        assertThat(new Product().getStatus()).isEqualTo(ProductStatus.ACTIVE);
        assertThat(Product.builder().build().getStatus()).isEqualTo(ProductStatus.ACTIVE);
        Product legacy = Product.builder().status(null).build();
        assertThat(mapper.toResponse(legacy).getStatus()).isNull();
        legacy.setStatus(ProductStatus.DELETED);
        assertThat(mapper.toResponse(legacy).getStatus()).isEqualTo(ProductStatus.DELETED);
    }
}
