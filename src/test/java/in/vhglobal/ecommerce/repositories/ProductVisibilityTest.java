package in.vhglobal.ecommerce.repositories;

import in.vhglobal.ecommerce.entities.products.Product;
import in.vhglobal.ecommerce.entities.products.ProductMainCategory;
import in.vhglobal.ecommerce.entities.products.ProductStatus;
import in.vhglobal.ecommerce.entities.products.ProductSubCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = "spring.jpa.hibernate.ddl-auto=create-drop")
class ProductVisibilityTest {

    @Autowired private ProductRepository repository;
    @Autowired private TestEntityManager entityManager;

    private ProductSubCategory category;
    private Product active;
    private Product legacy;
    private Product deleted;

    @BeforeEach
    void setUp() {
        ProductMainCategory main = new ProductMainCategory();
        main.setName("Main");
        entityManager.persist(main);
        category = category("First", main);
        active = product("A", ProductStatus.ACTIVE, category);
        legacy = product("B", null, category);
        deleted = product("C", ProductStatus.DELETED, category);
        product("D", ProductStatus.ACTIVE, category("Other", main));
        entityManager.flush();
        entityManager.clear();
    }

    @Test
    void defaultFilterIncludesLegacyNullsAndAppliesBeforePaginationAndCount() {
        var first = repository.findAllVisible(false, PageRequest.of(0, 2, Sort.by("name")));
        assertThat(first.getContent()).extracting(Product::getProductId)
                .containsExactly(active.getProductId(), legacy.getProductId());
        assertThat(first.getTotalElements()).isEqualTo(3);
        var second = repository.findAllVisible(false, PageRequest.of(1, 2, Sort.by("name")));
        assertThat(second.getContent()).extracting(Product::getName).containsExactly("D");
        assertThat(second.getTotalElements()).isEqualTo(3);
        assertThat(repository.findAllVisible(true, PageRequest.of(0, 10)).getTotalElements()).isEqualTo(4);
    }

    @Test
    void subCategoryFilterIncludesNullsAndNeverLeaksOtherCategories() {
        var page = repository.findVisibleBySubCategoryId(category.getProductSubCategoryId(), false,
                PageRequest.of(0, 1, Sort.by("name")));
        assertThat(page.getContent()).extracting(Product::getProductId).containsExactly(active.getProductId());
        assertThat(page.getTotalElements()).isEqualTo(2);
        assertThat(repository.findVisibleBySubCategoryId(category.getProductSubCategoryId(), true,
                PageRequest.of(0, 10)).getContent()).extracting(Product::getProductId)
                .containsExactlyInAnyOrder(active.getProductId(), legacy.getProductId(), deleted.getProductId());
    }

    @Test
    void idLookupHidesDeletedUnlessExplicitlyIncluded() {
        assertThat(repository.findVisibleById(active.getProductId(), false)).isPresent();
        assertThat(repository.findVisibleById(legacy.getProductId(), false)).isPresent();
        assertThat(repository.findVisibleById(deleted.getProductId(), false)).isEmpty();
        assertThat(repository.findVisibleById(deleted.getProductId(), true)).isPresent();
        assertThat(repository.findVisibleById("missing", true)).isEmpty();
    }

    private ProductSubCategory category(String name, ProductMainCategory main) {
        ProductSubCategory result = new ProductSubCategory();
        result.setName(name);
        result.setProductMainCategory(main);
        return entityManager.persist(result);
    }

    private Product product(String name, ProductStatus status, ProductSubCategory subCategory) {
        return entityManager.persist(Product.builder().name(name).price(10.0).minimumQuantity(1)
                .status(status).productSubCategory(subCategory).build());
    }
}
