package in.vhglobal.ecommerce.repositories;

import in.vhglobal.ecommerce.entities.products.ProductMainCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface ProductMainCategoryRepository extends JpaRepository<ProductMainCategory, String> {
    @Query("SELECT c FROM ProductMainCategory c WHERE (:includeDeleted = true OR c.status IS NULL " +
            "OR c.status <> in.vhglobal.ecommerce.entities.products.ProductStatus.DELETED)")
    List<ProductMainCategory> findAllVisible(@Param("includeDeleted") boolean includeDeleted);

    @Query("SELECT c FROM ProductMainCategory c WHERE c.productMainCategoryId = :id " +
            "AND (:includeDeleted = true OR c.status IS NULL " +
            "OR c.status <> in.vhglobal.ecommerce.entities.products.ProductStatus.DELETED)")
    Optional<ProductMainCategory> findVisibleById(@Param("id") String id, @Param("includeDeleted") boolean includeDeleted);
}
