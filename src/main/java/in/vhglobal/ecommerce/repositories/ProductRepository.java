package in.vhglobal.ecommerce.repositories;

import in.vhglobal.ecommerce.entities.products.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, String> {

    Page<Product> findAll(Pageable pageable);

    Page<Product> findByProductSubCategory_ProductSubCategoryId(String subCategoryId, Pageable pageable);
}
