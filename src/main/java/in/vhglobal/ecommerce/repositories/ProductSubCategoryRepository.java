package in.vhglobal.ecommerce.repositories;

import in.vhglobal.ecommerce.entities.products.ProductSubCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProductSubCategoryRepository extends JpaRepository<ProductSubCategory, String> {

    Page<ProductSubCategory> findAll(Pageable pageable);

    @Query("SELECT s FROM ProductSubCategory s JOIN FETCH s.productMainCategory WHERE s.productSubCategoryId = :productSubCategoryId")
    Optional<ProductSubCategory> findByIdWithMainCategory(@Param("productSubCategoryId") String productSubCategoryId);

    Page<ProductSubCategory> findByProductMainCategory_ProductMainCategoryId(String mainCategoryId, Pageable pageable);
    @Query("SELECT s FROM ProductSubCategory s WHERE (:includeDeleted = true OR s.status IS NULL " +
            "OR s.status <> in.vhglobal.ecommerce.entities.products.ProductStatus.DELETED)")
    Page<ProductSubCategory> findAllVisible(@Param("includeDeleted") boolean includeDeleted, Pageable pageable);

    @Query("SELECT s FROM ProductSubCategory s JOIN FETCH s.productMainCategory WHERE s.productSubCategoryId = :id " +
            "AND (:includeDeleted = true OR s.status IS NULL " +
            "OR s.status <> in.vhglobal.ecommerce.entities.products.ProductStatus.DELETED)")
    Optional<ProductSubCategory> findVisibleById(@Param("id") String id, @Param("includeDeleted") boolean includeDeleted);

    @Query("SELECT s FROM ProductSubCategory s WHERE s.productMainCategory.productMainCategoryId = :mainCategoryId " +
            "AND (:includeDeleted = true OR s.status IS NULL " +
            "OR s.status <> in.vhglobal.ecommerce.entities.products.ProductStatus.DELETED)")
    Page<ProductSubCategory> findVisibleByMainCategoryId(@Param("mainCategoryId") String mainCategoryId,
            @Param("includeDeleted") boolean includeDeleted, Pageable pageable);
}
