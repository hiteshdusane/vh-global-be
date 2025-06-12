package in.vhglobal.ecommerce.services;

import in.vhglobal.ecommerce.dtos.reponse.ProductResponse;
import in.vhglobal.ecommerce.dtos.request.ProductRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ProductService {

    ProductResponse createProduct(ProductRequest request);

    ProductResponse uploadImageForProduct(String productId, MultipartFile file) throws IOException;

    ProductResponse getProductById(String id);

    Page<ProductResponse> getAllProducts(Pageable pageable, boolean hydrateSubCategory);

    Page<ProductResponse> getBySubCategoryId(String subCategoryId, Pageable pageable, boolean hydrateSubCategory);

    void deleteProduct(String productId);
}
