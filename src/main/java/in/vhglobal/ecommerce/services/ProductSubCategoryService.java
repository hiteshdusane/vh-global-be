package in.vhglobal.ecommerce.services;

import in.vhglobal.ecommerce.dtos.reponse.ProductSubCategoryResponse;
import in.vhglobal.ecommerce.dtos.request.ProductSubCategoryRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductSubCategoryService {

    ProductSubCategoryResponse create(ProductSubCategoryRequest request);

    default Page<ProductSubCategoryResponse> getAll(Pageable pageable, boolean hydrateMainCategory) {
        return getAll(pageable, hydrateMainCategory, false);
    }

    Page<ProductSubCategoryResponse> getAll(Pageable pageable, boolean hydrateMainCategory, boolean includeDeleted);

    default ProductSubCategoryResponse getById(String productSubCategoryId) {
        return getById(productSubCategoryId, false);
    }

    ProductSubCategoryResponse getById(String productSubCategoryId, boolean includeDeleted);

    default Page<ProductSubCategoryResponse> getByMainCategoryId(String mainCategoryId, Pageable pageable, boolean hydrateMainCategory) {
        return getByMainCategoryId(mainCategoryId, pageable, hydrateMainCategory, false);
    }

    Page<ProductSubCategoryResponse> getByMainCategoryId(String mainCategoryId, Pageable pageable, boolean hydrateMainCategory, boolean includeDeleted);

    default void deleteProductSubCategory(String subProductCategoryId) {
        deleteProductSubCategory(subProductCategoryId, false);
    }

    void deleteProductSubCategory(String subProductCategoryId, boolean hardDelete);
}
