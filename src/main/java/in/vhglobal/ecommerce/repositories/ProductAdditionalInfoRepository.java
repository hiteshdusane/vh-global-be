package in.vhglobal.ecommerce.repositories;

import in.vhglobal.ecommerce.entities.products.ProductAdditionalInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductAdditionalInfoRepository extends JpaRepository<ProductAdditionalInfo, String> {
}
