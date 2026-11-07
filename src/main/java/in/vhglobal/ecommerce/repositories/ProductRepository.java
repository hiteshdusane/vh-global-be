package in.vhglobal.ecommerce.repositories;

import in.vhglobal.ecommerce.entities.products.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, String> {

    Page<Product> findAll(Pageable pageable);

    Page<Product> findByProductSubCategory_ProductSubCategoryId(String subCategoryId, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE (:includeDeleted = true OR p.status IS NULL " +
            "OR p.status <> in.vhglobal.ecommerce.entities.products.ProductStatus.DELETED)")
    Page<Product> findAllVisible(@Param("includeDeleted") boolean includeDeleted, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.productSubCategory.productSubCategoryId = :subCategoryId " +
            "AND (:includeDeleted = true OR p.status IS NULL " +
            "OR p.status <> in.vhglobal.ecommerce.entities.products.ProductStatus.DELETED)")
    Page<Product> findVisibleBySubCategoryId(@Param("subCategoryId") String subCategoryId,
                                           @Param("includeDeleted") boolean includeDeleted, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.productId = :id " +
            "AND (:includeDeleted = true OR p.status IS NULL " +
            "OR p.status <> in.vhglobal.ecommerce.entities.products.ProductStatus.DELETED)")
    Optional<Product> findVisibleById(@Param("id") String id, @Param("includeDeleted") boolean includeDeleted);
}
