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

    default ProductResponse getProductById(String id) {
        return getProductById(id, false);
    }

    ProductResponse getProductById(String id, boolean includeDeleted);

    default Page<ProductResponse> getAllProducts(Pageable pageable, boolean hydrateSubCategory) {
        return getAllProducts(pageable, hydrateSubCategory, false);
    }

    Page<ProductResponse> getAllProducts(Pageable pageable, boolean hydrateSubCategory, boolean includeDeleted);

    default Page<ProductResponse> getBySubCategoryId(String subCategoryId, Pageable pageable, boolean hydrateSubCategory) {
        return getBySubCategoryId(subCategoryId, pageable, hydrateSubCategory, false);
    }

    Page<ProductResponse> getBySubCategoryId(String subCategoryId, Pageable pageable, boolean hydrateSubCategory,
                                         boolean includeDeleted);

    default void deleteProduct(String productId) {
        deleteProduct(productId, false);
    }

    void deleteProduct(String productId, boolean hardDelete);

    void deleteProductSynchronously(String productId, boolean hardDelete);
}
