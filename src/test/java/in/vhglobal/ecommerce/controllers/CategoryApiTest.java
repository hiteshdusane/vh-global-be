package in.vhglobal.ecommerce.controllers;

import in.vhglobal.ecommerce.dtos.mappers.*;
import in.vhglobal.ecommerce.dtos.request.*;
import in.vhglobal.ecommerce.entities.products.*;
import in.vhglobal.ecommerce.services.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class CategoryApiTest {
    private ProductMainCategoryService mains;
    private ProductSubCategoryService subs;
    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        mains = mock(ProductMainCategoryService.class);
        subs = mock(ProductSubCategoryService.class);
        mvc = MockMvcBuilders.standaloneSetup(new ProductsController(mains, subs, null, null)).build();
    }

    @Test
    void deleteDefaultsToSoftAndAcceptsHardDelete() throws Exception {
        for (String type : new String[]{"main-category", "sub-category"}) {
            String url = "/api/v1/products/" + type + "/id";
            mvc.perform(delete(url)).andExpect(status().isOk());
            mvc.perform(delete(url).param("hardDelete", "false")).andExpect(status().isOk());
            mvc.perform(delete(url).param("hardDelete", "true")).andExpect(status().isOk());
            mvc.perform(delete(url).param("hardDelete", "invalid")).andExpect(status().isBadRequest());
        }
        verify(mains, times(2)).delete("id", false);
        verify(mains).delete("id", true);
        verify(subs, times(2)).deleteProductSubCategory("id", false);
        verify(subs).deleteProductSubCategory("id", true);
    }

    @Test
    void getEndpointsDefaultToVisibleAndAllowDeleted() throws Exception {
        mvc.perform(get("/api/v1/products/main-category")).andExpect(status().isOk());
        mvc.perform(get("/api/v1/products/main-category").param("includeDeleted", "true")).andExpect(status().isOk());
        verify(mains).getAll(false);
        verify(mains).getAll(true);
        for (boolean flag : new boolean[]{false, true}) {
            mvc.perform(get("/api/v1/products/main-category/id").param("includeDeleted", String.valueOf(flag))).andExpect(status().isOk());
            mvc.perform(get("/api/v1/products/sub-category/id").param("includeDeleted", String.valueOf(flag))).andExpect(status().isOk());
            verify(mains).getById("id", flag);
            verify(subs).getById("id", flag);
        }
    }

    @Test
    void paginatedEndpointsAcceptOmittedNullAndExplicitFlags() throws Exception {
        var pageable = PageRequest.of(0, 50, Sort.by("name"));
        when(subs.getAll(eq(pageable), eq(false), anyBoolean())).thenReturn(Page.empty(pageable));
        when(subs.getByMainCategoryId(eq("main"), eq(pageable), eq(false), anyBoolean())).thenReturn(Page.empty(pageable));
        for (String body : new String[]{"{}", "{\"includeDeleted\":null}", "{\"includeDeleted\":true}"}) {
            mvc.perform(post("/api/v1/products/sub-category-all").contentType(MediaType.APPLICATION_JSON).content(body)).andExpect(status().isOk());
            mvc.perform(post("/api/v1/products/sub-category-by-main-category").param("mainProductCategoryId", "main")
                    .contentType(MediaType.APPLICATION_JSON).content(body)).andExpect(status().isOk());
        }
        verify(subs, times(2)).getAll(pageable, false, false);
        verify(subs).getAll(pageable, false, true);
        verify(subs, times(2)).getByMainCategoryId("main", pageable, false, false);
        verify(subs).getByMainCategoryId("main", pageable, false, true);
    }

    @Test
    void mappingCreatesActiveCategoriesAndExposesDeletedStatus() {
        var mainMapper = Mappers.getMapper(ProductMainCategoryMapper.class);
        var subMapper = Mappers.getMapper(ProductSubCategoryMapper.class);
        assertThat(mainMapper.toEntity(new ProductMainCategoryRequest()).getStatus()).isEqualTo(ProductStatus.ACTIVE);
        assertThat(subMapper.toEntity(new ProductSubCategoryRequest()).getStatus()).isEqualTo(ProductStatus.ACTIVE);
        assertThat(mainMapper.toResponse(ProductMainCategory.builder().status(ProductStatus.DELETED).build()).getStatus()).isEqualTo(ProductStatus.DELETED);
        assertThat(subMapper.toResponse(ProductSubCategory.builder().status(null).build()).getStatus()).isNull();
    }
}
