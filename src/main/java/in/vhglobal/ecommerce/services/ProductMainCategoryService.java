package in.vhglobal.ecommerce.services;

import in.vhglobal.ecommerce.dtos.reponse.ProductMainCategoryResponse;
import in.vhglobal.ecommerce.dtos.request.ProductMainCategoryRequest;

import java.util.List;

public interface ProductMainCategoryService {

    ProductMainCategoryResponse create(ProductMainCategoryRequest request);

    default ProductMainCategoryResponse getById(String id) {
        return getById(id, false);
    }

    ProductMainCategoryResponse getById(String id, boolean includeDeleted);

    default List<ProductMainCategoryResponse> getAll() {
        return getAll(false);
    }

    List<ProductMainCategoryResponse> getAll(boolean includeDeleted);

    ProductMainCategoryResponse update(String id, ProductMainCategoryRequest request);

    default void delete(String id) {
        delete(id, false);
    }

    void delete(String id, boolean hardDelete);
}
