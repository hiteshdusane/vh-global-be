package in.vhglobal.ecommerce.services;

import in.vhglobal.ecommerce.dtos.reponse.ProductResponse;
import in.vhglobal.ecommerce.dtos.request.ProductRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {

    ProductResponse createProduct(ProductRequest request);

    ProductResponse getProductById(String id);

    Page<ProductResponse> getAllProducts(Pageable pageable, boolean hydrateSubCategory);

    Page<ProductResponse> getBySubCategoryId(String subCategoryId, Pageable pageable, boolean hydrateSubCategory);
}
