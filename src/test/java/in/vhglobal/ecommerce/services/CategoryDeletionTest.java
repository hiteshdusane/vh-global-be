package in.vhglobal.ecommerce.services;

import in.vhglobal.ecommerce.dtos.mappers.*;
import in.vhglobal.ecommerce.entities.products.*;
import in.vhglobal.ecommerce.exceptions.NotFoundApiException;
import in.vhglobal.ecommerce.repositories.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@DataJpaTest(properties = "spring.jpa.hibernate.ddl-auto=create-drop")
@Import({ProductMainCategoryServiceImpl.class, ProductSubCategoryServiceImpl.class, ProductServiceImpl.class})
class CategoryDeletionTest {
    @Autowired private ProductMainCategoryService mainService;
    @Autowired private ProductSubCategoryService subService;
    @Autowired private ProductMainCategoryRepository mains;
    @Autowired private ProductSubCategoryRepository subs;
    @Autowired private ProductRepository products;
    @Autowired private ProductAdditionalInfoRepository infos;
    @Autowired private TestEntityManager em;
    @MockitoBean private ImageStorageService images;
    @MockitoBean private ProductMapper productMapper;
    @MockitoBean private ProductMainCategoryMapper mainMapper;
    @MockitoBean private ProductSubCategoryMapper subMapper;
    private String mainId;
    private String subId;
    private String productId;

    @BeforeEach
    void setUp() {
        var main = em.persist(ProductMainCategory.builder().name("Main").status(null).build());
        var sub = em.persist(ProductSubCategory.builder().name("Sub").productMainCategory(main).status(null).build());
        var product = em.persist(Product.builder().name("Product").price(10.0).minimumQuantity(1)
                .productSubCategory(sub).imagePath("primary.jpg").build());
        em.persist(ProductAdditionalInfo.builder().product(product).imagePath("extra.jpg").build());
        mainId = main.getProductMainCategoryId();
        subId = sub.getProductSubCategoryId();
        productId = product.getProductId();
        em.flush();
        em.clear();
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void softDeleteCascadesAndPreservesImagesAndRows(boolean deleteMain) {
        if (deleteMain) {
            mainService.delete(mainId);
            mainService.delete(mainId); // Already-deleted categories remain safe to delete.
        } else {
            subService.deleteProductSubCategory(subId);
            subService.deleteProductSubCategory(subId);
        }
        em.flush();
        em.clear();
        assertThat(mains.findById(mainId).orElseThrow().getStatus())
                .isEqualTo(deleteMain ? ProductStatus.DELETED : null);
        assertThat(subs.findById(subId).orElseThrow().getStatus()).isEqualTo(ProductStatus.DELETED);
        assertThat(products.findById(productId).orElseThrow().getStatus()).isEqualTo(ProductStatus.DELETED);
        assertThat(infos.count()).isEqualTo(1);
        assertThat(subs.findVisibleById(subId, false)).isEmpty();
        assertThat(subs.findVisibleById(subId, true)).isPresent();
        if (deleteMain) {
            assertThat(mains.findVisibleById(mainId, false)).isEmpty();
            assertThat(mains.findVisibleById(mainId, true)).isPresent();
        }
        verifyNoInteractions(images);
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void hardDeleteAfterSoftDeleteRemovesDescendantsAndPhotos(boolean deleteMain) {
        if (deleteMain) {
            mainService.delete(mainId, false);
            mainService.delete(mainId, true);
        } else {
            subService.deleteProductSubCategory(subId, false);
            subService.deleteProductSubCategory(subId, true);
        }
        em.flush();
        em.clear();
        assertThat(mains.existsById(mainId)).isEqualTo(!deleteMain);
        assertThat(subs.existsById(subId)).isFalse();
        assertThat(products.existsById(productId)).isFalse();
        assertThat(infos.count()).isZero();
        verify(images).delete("primary.jpg");
        verify(images).delete("extra.jpg");
    }

    @Test
    void filtersIncludeLegacyNullsAndCountBeforePagination() {
        em.persist(ProductMainCategory.builder().name("Deleted").status(ProductStatus.DELETED).build());
        var main = mains.findById(mainId).orElseThrow();
        em.persist(ProductSubCategory.builder().name("Deleted").productMainCategory(main).status(ProductStatus.DELETED).build());
        em.persist(ProductSubCategory.builder().name("Active").productMainCategory(main).build());
        em.flush();
        em.clear();
        assertThat(mains.findAllVisible(false)).hasSize(1);
        assertThat(mains.findAllVisible(true)).hasSize(2);
        assertThat(mains.findVisibleById(mainId, false)).isPresent();
        assertThat(subs.findVisibleById(subId, false)).isPresent();
        var page = subs.findAllVisible(false, PageRequest.of(0, 1, Sort.by("name")));
        assertThat(page.getTotalElements()).isEqualTo(2);
        assertThat(page.getContent()).extracting(ProductSubCategory::getName).containsExactly("Active");
        assertThat(subs.findVisibleByMainCategoryId(mainId, false, PageRequest.of(0, 10)).getTotalElements()).isEqualTo(2);
        assertThat(subs.findVisibleByMainCategoryId(mainId, true, PageRequest.of(0, 10)).getTotalElements()).isEqualTo(3);
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void missingCategoriesReturnNotFound(boolean hardDelete) {
        assertThatThrownBy(() -> mainService.delete("missing", hardDelete)).isInstanceOf(NotFoundApiException.class);
        assertThatThrownBy(() -> subService.deleteProductSubCategory("missing", hardDelete)).isInstanceOf(NotFoundApiException.class);
        verifyNoInteractions(images);
    }
}
