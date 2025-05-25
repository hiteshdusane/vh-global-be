package in.vhglobal.ecommerce.repositories;

import in.vhglobal.ecommerce.entities.products.ProductMainCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductMainCategoryRepository extends JpaRepository<ProductMainCategory, String> {
}
